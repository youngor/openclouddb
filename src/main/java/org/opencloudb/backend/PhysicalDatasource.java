package org.opencloudb.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.config.Alarms;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.config.model.DataHostConfig;
import org.opencloudb.heartbeat.DBHeartbeat;
import org.opencloudb.mysql.nio.handler.DelegateResponseHandler;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.util.TimeUtil;

public abstract class PhysicalDatasource {
	private static final Logger LOGGER = Logger
			.getLogger(PhysicalDatasource.class);

	protected final String name;
	protected final ReentrantLock lock = new ReentrantLock();
	private final int size;
	private final DBHostConfig config;
	private final PhysicalConnection[] items;
	private final ArrayList<PhysicalConnection> runningItems;
	protected int activeCount;
	protected int idleCount;
	protected DBHeartbeat heartbeat;

	private final boolean readNode;
	private volatile long heartbeatRecoveryTime;
	protected final DataHostConfig hostConfig;

	private PhysicalDBPool dbPool;

	public PhysicalDatasource(DBHostConfig config, DataHostConfig hostConfig,
			boolean isReadNode) {
		this.size = config.getMaxCon();
		this.items = new PhysicalConnection[size];
		this.config = config;
		this.name = config.getHostName();
		this.hostConfig = hostConfig;
		heartbeat = this.createHeartBeat();
		this.readNode = isReadNode;
		this.runningItems = new ArrayList<PhysicalConnection>(size * 2 / 3);
	}

	public boolean isMyConnection(PhysicalConnection con) {
		PhysicalConnection[] theItems = null;
		ArrayList<PhysicalConnection> theRunningItems = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			theItems = this.items;
			theRunningItems = this.runningItems;
		} finally {
			lock.unlock();
		}

		for (PhysicalConnection myCon : theRunningItems) {
			if (myCon == con) {
				return true;
			}
		}
		for (PhysicalConnection myCon : theItems) {
			if (myCon == con) {
				return true;
			}
		}
		return false;
	}

	public DataHostConfig getHostConfig() {
		return hostConfig;
	}

	public boolean isReadNode() {
		return readNode;
	}

	public int getSize() {
		return size;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public void setIdleCount(int idleCount) {
		this.idleCount = idleCount;
	}

	public void setDbPool(PhysicalDBPool dbPool) {
		this.dbPool = dbPool;
	}

	public PhysicalDBPool getDbPool() {
		return dbPool;
	}

	public abstract DBHeartbeat createHeartBeat();

	public String getName() {
		return name;
	}

	public int getActiveCount() {
		return activeCount;
	}

	public DBHeartbeat getHeartbeat() {
		return heartbeat;
	}

	public int getIdleCount() {
		return idleCount;
	}

	public void idleCheck(long timeout) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final PhysicalConnection[] items = this.items;
			long time = TimeUtil.currentTimeMillis() - timeout;
			for (int i = 0; i < items.length; i++) {
				PhysicalConnection c = items[i];
				if (c != null && time > c.getLastTime()) {
					c.closeNoActive();
					--idleCount;
					items[i] = null;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public void clearCons() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final PhysicalConnection[] items = this.items;
			for (int i = 0; i < items.length; i++) {
				PhysicalConnection c = items[i];
				if (c != null) {
					c.closeNoActive();
					--idleCount;
					items[i] = null;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public void startHeartbeat() {
		heartbeat.start();
	}

	public void stopHeartbeat() {
		heartbeat.stop();
	}

	public void doHeartbeat() {
		// 未到预定恢复时间，不执行心跳检测。
		if (TimeUtil.currentTimeMillis() < heartbeatRecoveryTime) {
			return;
		}
		if (!heartbeat.isStop()) {
			try {
				heartbeat.heartbeat();
			} catch (Throwable e) {
				LOGGER.error(name + " heartbeat error.", e);
			}
		}
	}

	private PhysicalConnection takeCon(int index,
			final ResponseHandler handler, final Object attachment,
			String schema) {

		PhysicalConnection conn = items[index];
		items[index] = null;
		runningItems.add(conn);
		--idleCount;
		++activeCount;
		if (schema != null) {
			conn.setSchema(schema);
		}
		conn.setAttachment(attachment);
		handler.connectionAcquired(conn);

		return conn;
	}

	public PhysicalConnection getConnection(final ResponseHandler handler,
			final Object attachment, final String schema) throws Exception {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			// too many active connections
			if (activeCount >= size) {
				StringBuilder s = new StringBuilder();
				s.append(Alarms.DEFAULT).append("DATASOURCE EXCEED [name=")
						.append(name).append(",active=");
				s.append(activeCount).append(",size=").append(size).append(']');
				LOGGER.warn(s.toString());
				throw new IOException("datasource is full,can't get any more "
						+ this.getName());
			}

			// get connection from pool
			final PhysicalConnection[] items = this.items;
			int oldestIdleConIndx = -1;
			long oldestConTime = Long.MAX_VALUE;
			for (int i = 0, len = items.length; idleCount > 0 && i < len; ++i) {
				if (items[i] != null) {
					PhysicalConnection conn = items[i];
					if (conn.isClosedOrQuit()) {
						items[i] = null;
						--idleCount;
						continue;
					} else {
						if (schema.equals(conn.getSchema())) {
							return takeCon(i, handler, attachment, null);
						} else if (conn.getLastTime() < oldestConTime) {
							oldestIdleConIndx = i;
							oldestConTime = conn.getLastTime();
						}
					}
				}
			}
			if (oldestIdleConIndx > -1) {
				return takeCon(oldestIdleConIndx, handler, attachment, schema);
			}
		} finally {
			lock.unlock();
		}
		LOGGER.info("not ilde connection in pool,create new connection for "
				+ this.name);
		// create connection
		return this.createNewConnection(new DelegateResponseHandler(handler) {
			@Override
			public void connectionError(Throwable e, PhysicalConnection conn) {
				handler.connectionError(e, conn);
			}

			@Override
			public void connectionAcquired(PhysicalConnection conn) {
				lock.lock();
				try {
					++activeCount;
					runningItems.add(conn);
				} finally {
					lock.unlock();
				}
				conn.setSchema(schema);
				conn.setAttachment(attachment);
				handler.connectionAcquired(conn);
			}
		});
	}

	public void releaseChannel(PhysicalConnection c) {
		if (c == null || c.isClosedOrQuit()) {
			return;
		}

		// release connection
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final PhysicalConnection[] items = this.items;
			for (int i = 0; i < items.length; i++) {
				if (items[i] == null) {
					++idleCount;
					--activeCount;
					c.setLastTime(TimeUtil.currentTimeMillis());
					items[i] = c;
					return;
				}
			}
		} finally {
			lock.unlock();
		}

		// close excess connection
		c.quit();
	}

	public abstract PhysicalConnection createNewConnection(
			ResponseHandler handler) throws IOException;

	public void deActive() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			--activeCount;
		} finally {
			lock.unlock();
		}
	}

	public long getHeartbeatRecoveryTime() {
		return heartbeatRecoveryTime;
	}

	public void setHeartbeatRecoveryTime(long heartbeatRecoveryTime) {
		this.heartbeatRecoveryTime = heartbeatRecoveryTime;
	}

	public DBHostConfig getConfig() {
		return config;
	}
}
