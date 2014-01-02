package org.opencloudb.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.config.Alarms;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.config.model.DataHostConfig;
import org.opencloudb.heartbeat.DBHeartbeat;
import org.opencloudb.mysql.nio.handler.DelegateResponseHandler;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.ServerConnection;
import org.opencloudb.util.TimeUtil;

public abstract class PhysicalDatasource {
	private static final Logger LOGGER = Logger
			.getLogger(PhysicalDatasource.class);

	private final String name;
	private final ReentrantLock lock = new ReentrantLock();
	private final int size;
	private final DBHostConfig config;
	private final PhysicalConnection[] items;
	private DBHeartbeat heartbeat;
	private final boolean readNode;
	private volatile long heartbeatRecoveryTime;
	private final DataHostConfig hostConfig;

	private PhysicalDBPool dbPool;

	private long executeCount;

	public PhysicalDatasource(DBHostConfig config, DataHostConfig hostConfig,
			boolean isReadNode) {
		this.size = config.getMaxCon();
		this.items = new PhysicalConnection[size];
		this.config = config;
		this.name = config.getHostName();
		this.hostConfig = hostConfig;
		heartbeat = this.createHeartBeat();
		this.readNode = isReadNode;
	}

	public boolean isMyConnection(PhysicalConnection con) {
		PhysicalConnection[] theItems = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			theItems = this.items;
		} finally {
			lock.unlock();
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

	public long getExecuteCount() {
		return executeCount;
	}

	public int getActiveCount() {
		int running = 0;
		for (PhysicalConnection con : this.items) {
			if (con != null && con.isBorrowed()) {
				running++;
			}
		}
		return running;
	}

	public DBHeartbeat getHeartbeat() {
		return heartbeat;
	}

	public int getIdleCount() {
		int idle = 0;
		for (PhysicalConnection con : this.items) {
			if (con != null && !con.isBorrowed()) {
				idle++;
			}
		}
		return idle;
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

					c.closeNoActive(" idle ");
					items[i] = null;
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public void clearCons(String reason) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final PhysicalConnection[] items = this.items;
			for (int i = 0; i < items.length; i++) {
				PhysicalConnection c = items[i];
				if (c != null) {
					c.closeNoActive(reason);
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

	private PhysicalConnection takeCon(PhysicalConnection conn,
			final ResponseHandler handler, final Object attachment,
			String schema) {
		conn.setBorrowed(true);
		if (schema != null) {
			conn.setSchema(schema);
		}
		executeCount++;
		conn.setAttachment(attachment);
		handler.connectionAcquired(conn);
		return conn;
	}

	public PhysicalConnection getConnection(final ConnectionMeta conMeta,
			final ResponseHandler handler, final Object attachment) throws Exception {
		int activeCount = this.getActiveCount();
		// used to store new created connection
		int emptyIndex = -1;
		int bestCandidate = -1;
		int bestCandidateSimilarity = -1;
		long bestCandidateTime = Long.MAX_VALUE;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			// get connection from pool
			final PhysicalConnection[] items = this.items;

			for (int i = 0; i < items.length; i++) {
				if (emptyIndex == -1 && items[i] == null) {
					emptyIndex = i;
				} else if (items[i] != null) {
					PhysicalConnection conn = items[i];
					// closed or quit
					if (conn.isClosedOrQuit()) {
						items[i] = null;
						if (emptyIndex == -1) {
							emptyIndex = i;
						}
					} else if (!conn.isBorrowed()) {
						// compare if more similary
						int similary = conMeta.getMetaSimilarity(conn);
						if (bestCandidateSimilarity < similary) {
							bestCandidateSimilarity = similary;
							bestCandidateTime=conn.getLastTime();
							bestCandidate = i;
						} else if (bestCandidateSimilarity == similary) {
							// compare if more old
							if (conn.getLastTime() < bestCandidateTime) {
								bestCandidateTime=conn.getLastTime();
								bestCandidate = i;
							}

						}
					}
				}
			}
			if (bestCandidate != -1) {
				return takeCon(items[bestCandidate], handler, attachment,
						conMeta.getSchema());
			} else if (emptyIndex == -1) {
				StringBuilder s = new StringBuilder();
				s.append(Alarms.DEFAULT).append("DATASOURCE EXCEED [name=")
						.append(name).append(",active=");
				s.append(activeCount).append(",size=").append(size).append(']');
				LOGGER.warn(s.toString());
				throw new IOException("datasource is full,can't get any more "
						+ this.getName());
			} else {
				// creat new connection
				items[emptyIndex] = new FakeConnection();
			}
		} finally {
			lock.unlock();
		}

		LOGGER.info("not ilde connection in pool,create new connection for "
				+ this.name);
		final int insertIndex = emptyIndex;
		// create connection
		return this.createNewConnection(new DelegateResponseHandler(handler) {
			@Override
			public void connectionError(Throwable e, PhysicalConnection conn) {
				handler.connectionError(e, conn);
				lock.lock();
				try {
					items[insertIndex] = null;
				} finally {
					lock.unlock();
				}
			}

			@Override
			public void connectionAcquired(PhysicalConnection conn) {
				lock.lock();
				try {
					items[insertIndex] = conn;
					takeCon(conn, handler, attachment, conMeta.getSchema());
				} finally {
					lock.unlock();
				}
			}
		});
	}

	private void returnCon(PhysicalConnection c) {
		c.setBorrowed(false);
		c.setLastTime(TimeUtil.currentTimeMillis());
	}

	public void releaseChannel(PhysicalConnection c) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("release channel " + c);
		}
		// release connection
		returnCon(c);
	}

	public abstract PhysicalConnection createNewConnection(
			ResponseHandler handler) throws IOException;

	public void deActive(PhysicalConnection con) {
		returnCon(con);
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

class FakeConnection implements PhysicalConnection {

	@Override
	public boolean isFromSlaveDB() {
		return false;
	}

	@Override
	public String getSchema() {
		return null;
	}

	@Override
	public void setSchema(String newSchema) {

	}

	@Override
	public long getLastTime() {
		return System.currentTimeMillis();
	}

	@Override
	public void closeNoActive(String reason) {

	}

	@Override
	public boolean isClosedOrQuit() {
		return false;
	}

	@Override
	public void setAttachment(Object attachment) {

	}

	@Override
	public void quit() {

	}

	@Override
	public void setLastTime(long currentTimeMillis) {
	}

	@Override
	public void release() {
	}

	@Override
	public void close(String reason) {

	}

	@Override
	public void setRunning(boolean running) {

	}

	@Override
	public boolean setResponseHandler(ResponseHandler commandHandler) {
		return false;
	}

	@Override
	public void commit() {

	}

	@Override
	public void query(String sql) throws UnsupportedEncodingException {

	}

	@Override
	public Object getAttachment() {
		return null;
	}

	@Override
	public long getThreadId() {
		return 0;
	}

	@Override
	public String getCharset() {
		return null;
	}

	@Override
	public void execute(RouteResultsetNode node, ServerConnection source,
			boolean autocommit) throws IOException {

	}

	@Override
	public void recordSql(String host, String schema, String statement) {

	}

	@Override
	public boolean syncAndExcute() {
		return false;
	}

	@Override
	public void rollback() {

	}

	@Override
	public boolean isSuppressReadTemporay() {
		return false;
	}

	@Override
	public void setSuppressReadTemporay(boolean b) {
	}

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public boolean isBorrowed() {
		return true;
	}

	@Override
	public void setBorrowed(boolean borrowed) {

	}

	@Override
	public boolean isAutocommit() {
		return false;
	}

	@Override
	public boolean isModifiedSQLExecuted() {
		return false;
	}

	@Override
	public int getTxIsolation() {
		return 0;
	}

}
