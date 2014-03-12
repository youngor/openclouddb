/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.backend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.config.Alarms;
import org.opencloudb.config.model.DBHostConfig;
import org.opencloudb.config.model.DataHostConfig;
import org.opencloudb.heartbeat.DBHeartbeat;
import org.opencloudb.mysql.nio.handler.ConnectionHeartBeatHandler;
import org.opencloudb.mysql.nio.handler.DelegateResponseHandler;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.mysql.nio.handler.SimpleLogHandler;
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
	private final BackendConnection[] items;
	private DBHeartbeat heartbeat;
	private final boolean readNode;
	private volatile long heartbeatRecoveryTime;
	private final DataHostConfig hostConfig;
	private final ConnectionHeartBeatHandler conHeartBeatHanler = new ConnectionHeartBeatHandler();
	private PhysicalDBPool dbPool;

	private long executeCount;

	public PhysicalDatasource(DBHostConfig config, DataHostConfig hostConfig,
			boolean isReadNode) {
		this.size = config.getMaxCon();
		this.items = new BackendConnection[size];
		this.config = config;
		this.name = config.getHostName();
		this.hostConfig = hostConfig;
		heartbeat = this.createHeartBeat();
		this.readNode = isReadNode;
	}

	public boolean isMyConnection(BackendConnection con) {
		BackendConnection[] theItems = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			theItems = this.items;
		} finally {
			lock.unlock();
		}
		for (BackendConnection myCon : theItems) {
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
		for (BackendConnection con : this.items) {
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
		for (BackendConnection con : this.items) {
			if (con != null && !con.isBorrowed()) {
				idle++;
			}
		}
		return idle;
	}

	private boolean validSchema(String schema) {
		String theSchema = schema;
		return theSchema != null & !theSchema.equals("")
				&& !theSchema.equals("snyn...");
	}

	public void heatBeatCheck(long timeout, long conHeartBeatPeriod) {
		int IDLE_CLOSE_COUNT = 3;
		int MAX_CONS_IN_ONE_CHECK = 10;
		LinkedList<BackendConnection> heartBeatCons = new LinkedList<BackendConnection>();
		LinkedList<BackendConnection> idleConList = new LinkedList<BackendConnection>();
		int idleCons = 0;
		int activeCons = 0;
		long hearBeatTime = TimeUtil.currentTimeMillis() - conHeartBeatPeriod;
		long hearBeatTime2 = TimeUtil.currentTimeMillis() - 2
				* conHeartBeatPeriod;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {

			for (int i = 0; i < items.length; i++) {
				BackendConnection c = items[i];
				if (items[i] == null) {
					continue;
				} else if (items[i].isClosedOrQuit()) {
					continue;
				}
				boolean borrowed = items[i].isBorrowed();
				if (borrowed) {
					activeCons++;
				} else {
					idleCons++;
					idleConList.add(items[i]);
				}
				if (!borrowed) {
					if (validSchema(c.getSchema())) {
						if (c.getLastTime() < hearBeatTime) {
							if (heartBeatCons.size() < MAX_CONS_IN_ONE_CHECK) {
								// Heart beat check
								c.setBorrowed(true);
								heartBeatCons.add(c);
							}
						}
					} else if (c.getLastTime() < hearBeatTime2) {
						{// not valid schema conntion should close for idle
							// exceed 2*conHeartBeatPeriod
							c.close(" heart beate idle ");
							items[i] = null;
						}

					}
				}
			}
		} finally {
			lock.unlock();
		}
		if (!heartBeatCons.isEmpty()) {
			for (BackendConnection con : heartBeatCons) {
				conHeartBeatHanler
						.doHeartBeat(con, hostConfig.getHearbeatSQL());
			}
		}
		// check if there has timeouted heatbeat cons
		conHeartBeatHanler.abandTimeOuttedConns();
		// create if idle too little
		if (idleCons + activeCons < size && idleCons < hostConfig.getMinCon()) {
			LOGGER.info("create connections ,because idle connection not enough ,cur is "
					+ idleCons
					+ ", minCon is "
					+ hostConfig.getMinCon()
					+ " for " + name);
			SimpleLogHandler simpleHandler = new SimpleLogHandler();
			lock.lock();
			try {
				int createCount = (hostConfig.getMinCon() - idleCons) / 3;
				final BackendConnection[] items = this.items;
				for (int i = 0; i < items.length; i++) {
					if (this.getActiveCount() + this.getIdleCount() >= size) {
						break;
					}
					if (items[i] == null) {
						items[i] = new FakeConnection();
						--createCount;
						try {
							// creat new connection
							this.createNewConnection(false, simpleHandler, i,
									null, "");
						} catch (IOException e) {
							LOGGER.warn("create connection err " + e);
						}
						if (createCount <= 0) {
							break;
						}
					}
				}
			} finally {
				lock.unlock();
			}

		} else if (idleCons > hostConfig.getMinCon() + IDLE_CLOSE_COUNT) {// too
																			// many
																			// idle
			int closedCount = 0;
			ArrayList<BackendConnection> readyCloseCons = new ArrayList<BackendConnection>(
					IDLE_CLOSE_COUNT);
			for (BackendConnection idleCon : idleConList) {
				if (closedCount < IDLE_CLOSE_COUNT) {
					if (!idleCon.isBorrowed() && !idleCon.isClosedOrQuit()
							&& !lock.isLocked()) {
						lock.lock();
						try {
							if (!idleCon.isBorrowed()
									&& !idleCon.isClosedOrQuit()) {
								idleCon.setBorrowed(true);
								readyCloseCons.add(idleCon);
								closedCount++;

							}
						} finally {
							lock.unlock();
						}
					}
				}
			}
			for (BackendConnection realidleCon : readyCloseCons) {
				realidleCon.close("too many idle con");
			}
		}
	}

	public void clearCons(String reason) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final BackendConnection[] items = this.items;
			for (int i = 0; i < items.length; i++) {
				BackendConnection c = items[i];
				if (c != null) {
					c.close(reason);
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

	private BackendConnection takeCon(BackendConnection conn,
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

	private void createNewConnection(final boolean consume,
			final ResponseHandler handler, final int insertIndex,
			final Object attachment, final String schema) throws IOException {
		this.createNewConnection(new DelegateResponseHandler(handler) {
			@Override
			public void connectionError(Throwable e, BackendConnection conn) {
				lock.lock();
				try {
					items[insertIndex] = null;
				} finally {
					lock.unlock();
				}
				handler.connectionError(e, conn);
			}

			@Override
			public void connectionAcquired(BackendConnection conn) {
				lock.lock();
				try {
					items[insertIndex] = conn;
					if (consume) {
						takeCon(conn, handler, attachment, schema);
					}
				} finally {
					lock.unlock();
				}
			}
		});
	}

	public void getConnection(final ConnectionMeta conMeta,
			final ResponseHandler handler, final Object attachment)
			throws Exception {
		int activeCount = this.getActiveCount();
		// used to store new created connection
		int emptyIndex = -1;
		int bestCandidate = -1;
		int bestCandidateSimilarity = -1;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			// get connection from pool
			final BackendConnection[] items = this.items;

			for (int i = 0; i < items.length; i++) {
				if (emptyIndex == -1 && items[i] == null) {
					emptyIndex = i;
				} else if (items[i] != null) {
					BackendConnection conn = items[i];
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
							bestCandidate = i;
						}
					}
				}
			}
			if (bestCandidate != -1) {
				takeCon(items[bestCandidate], handler, attachment,
						conMeta.getSchema());
				return;
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
		createNewConnection(true, handler, insertIndex, attachment,
				conMeta.getSchema());
		return;
	}

	private void returnCon(BackendConnection c) {
		c.setAttachment(null);
		c.setBorrowed(false);
		c.setLastTime(TimeUtil.currentTimeMillis());
	}

	public void releaseChannel(BackendConnection c) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("release channel " + c);
		}
		// release connection
		returnCon(c);
	}

	public abstract void createNewConnection(ResponseHandler handler)
			throws IOException;

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

class FakeConnection implements BackendConnection {

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

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public void idleCheck() {

	}

	@Override
	public long getStartupTime() {
		return 0;
	}

	@Override
	public String getHost() {
		return null;
	}

	@Override
	public int getPort() {
		return 0;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public long getNetInBytes() {
		return 0;
	}

	@Override
	public long getNetOutBytes() {
		return 0;
	}

}