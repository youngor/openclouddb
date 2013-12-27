/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2012-4-19)
 */
package org.opencloudb.mysql.nio.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.backend.PhysicalDBNode;
import org.opencloudb.cache.MysqlDataSetService;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.NonBlockingSession;
import org.opencloudb.server.ServerConnection;
import org.opencloudb.util.StringUtil;

/**
 * @author mycat
 */
public class SingleNodeHandler implements ResponseHandler, Terminatable {
	private static final Logger LOGGER = Logger
			.getLogger(SingleNodeHandler.class);
	private final RouteResultsetNode node;
	private final NonBlockingSession session;
	private byte packetId;
	private volatile ByteBuffer buffer;
	private ReentrantLock lock = new ReentrantLock();
	private boolean isRunning;
	private Runnable terminateCallBack;
	private static final MysqlDataSetService dataSetSrv = MysqlDataSetService
			.getInstance();

	public SingleNodeHandler(RouteResultsetNode route,
			NonBlockingSession session) {
		if (route == null) {
			throw new IllegalArgumentException("routeNode is null!");
		}
		if (session == null) {
			throw new IllegalArgumentException("session is null!");
		}
		this.session = session;
		this.node = route;
	}

	@Override
	public void terminate(Runnable callback) {
		boolean zeroReached = false;
		lock.lock();
		try {
			if (isRunning) {
				terminateCallBack = callback;
			} else {
				zeroReached = true;
			}
		} finally {
			lock.unlock();
		}
		if (zeroReached) {
			callback.run();
		}
	}

	private void endRunning() {
		Runnable callback = null;
		lock.lock();
		try {
			if (isRunning) {
				isRunning = false;
				callback = terminateCallBack;
				terminateCallBack = null;
			}
		} finally {
			lock.unlock();
		}
		if (callback != null) {
			callback.run();
		}
	}

	private void recycleResources() {
		ByteBuffer buf;
		lock.lock();
		try {
			buf = buffer;
			if (buf != null) {
				buffer = null;
			}
		} finally {
			lock.unlock();
		}
		if (buf != null) {
			session.getSource().recycle(buf);
		}
	}

	public void execute() throws Exception {
		lock.lock();
		try {
			this.isRunning = true;
			this.packetId = 0;
			this.buffer = session.getSource().allocate();
		} finally {
			lock.unlock();
		}

		final PhysicalConnection conn = session.getTarget(node);
		if (conn != null) {
			if (!conn.isFromSlaveDB()
					|| node.canRunnINReadDB(session.getSource()
							.isAutocommit())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("found connections in session to use "
							+ conn + " for " + node);
				}
			
			conn.setAttachment(node);
			session.getSource().getProcessor().getExecutor()
					.execute(new Runnable() {
						@Override
						public void run() {
							_execute(conn);
						}
					});
			return;
		 }
		}
			MycatConfig conf = MycatServer.getInstance().getConfig();
			PhysicalDBNode dn = conf.getDataNodes().get(node.getName());
			dn.getConnection(node, session.getSource().isAutocommit(), this,
					node);
		
	}

	@Override
	public void connectionAcquired(final PhysicalConnection conn) {
		conn.setRunning(true);
		session.bindConnection(node, conn);
		session.getSource().getProcessor().getExecutor()
				.execute(new Runnable() {
					@Override
					public void run() {
						_execute(conn);
					}
				});
	}

	private void _execute(PhysicalConnection conn) {
		if (session.closed()) {
			conn.setRunning(false);
			endRunning();
			session.clearConnections();
			return;
		}
		conn.setResponseHandler(this);
		try {
			conn.execute(node, session.getSource(), session.getSource()
					.isAutocommit());
		} catch (IOException e1) {
			executeException(conn);
			return;
		}
	}

	private void executeException(PhysicalConnection c) {
		c.setRunning(false);
		endRunning();
		session.clearConnections();
		ErrorPacket err = new ErrorPacket();
		err.packetId = ++packetId;
		err.errno = ErrorCode.ER_YES;
		err.message = StringUtil.encode(
				"unknown backend charset: " + c.getCharset(), session
						.getSource().getCharset());
		ServerConnection source = session.getSource();
		source.write(err.write(buffer, source));
	}

	@Override
	public void connectionError(Throwable e, PhysicalConnection conn) {
		// System.out.println("connectionError:" + e.toString());
		if (!session.closeConnection(node)) {
			conn.close();
		}
		endRunning();
		ErrorPacket err = new ErrorPacket();
		err.packetId = ++packetId;
		err.errno = ErrorCode.ER_YES;
		err.message = StringUtil.encode(e.getMessage(), session.getSource()
				.getCharset());
		ServerConnection source = session.getSource();
		source.write(err.write(buffer, source));
	}

	@Override
	public void errorResponse(byte[] data, PhysicalConnection conn) {
		// System.out.println("received errorResponse from conn:" + conn);

		ErrorPacket err = new ErrorPacket();
		err.read(data);
		conn.setRunning(false);
		session.clearConnections();
		endRunning();
		ServerConnection source = session.getSource();
		source.setTxInterrupt();

		recycleResources();
		err.write(source);
	}

	@Override
	public void okResponse(byte[] data, PhysicalConnection conn) {
		boolean executeResponse = false;
		try {
			executeResponse = conn.syncAndExcute();
			// System.out.println("executeResponse  "+executeResponse);
			// System.out.println("received okResponse from conn:" + conn
			// + " response:" + executeResponse);
		} catch (UnsupportedEncodingException e) {
			executeException(conn);
		}
		if (executeResponse) {
			conn.setRunning(false);
			ServerConnection source = session.getSource();
			endRunning();
			OkPacket ok = new OkPacket();
			ok.read(data);

			recycleResources();
			// ok.packetId = ++packetId;// OK_PACKET
			source.setLastInsertId(ok.insertId);

			if (source.isAutocommit()) {
				session.releaseConnections();
				ok.write(source);
			} else {
				ok.write(source);
			}

		}
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		// System.out.println("received  rowEofResponse from conn:" + conn);
		ServerConnection source = session.getSource();
		conn.setRunning(false);
		conn.recordSql(source.getHost(), source.getSchema(),
				node.getStatement());

		if (source.isAutocommit()) {
			session.releaseConnections();
		}
		endRunning();
		lock.lock();
		try {
			eof[3] = ++packetId;
			buffer = source.writeToBuffer(eof, buffer);
			source.write(buffer);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {
		// System.out.println("received  fieldEofResponse from conn:" + conn);
		lock.lock();
		try {
			header[3] = ++packetId;
			ServerConnection source = session.getSource();
			buffer = source.writeToBuffer(header, buffer);
			for (int i = 0, len = fields.size(); i < len; ++i) {
				byte[] field = fields.get(i);
				field[3] = ++packetId;
				buffer = source.writeToBuffer(field, buffer);
			}
			eof[3] = ++packetId;
			buffer = source.writeToBuffer(eof, buffer);
		} finally {
			lock.unlock();
		}
		// if(dataSetSrv.isEnabled() )
		// {
		// dataSetSrv.needCache()
		// }

	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
		// System.out.println("received rowResponse from conn:" + conn);
		lock.lock();
		try {
			row[3] = ++packetId;
			buffer = session.getSource().writeToBuffer(row, buffer);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void writeQueueAvailable() {

	}

}