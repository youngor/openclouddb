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
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.ConnectionMeta;
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
	// only one thread access at one time no need lock
	private volatile byte packetId;
	private volatile ByteBuffer buffer;
	private volatile boolean isRunning;
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

		if (isRunning) {
			terminateCallBack = callback;
		} else {
			zeroReached = true;
		}

		if (zeroReached) {
			callback.run();
		}
	}

	private void endRunning() {
		Runnable callback = null;
		if (isRunning) {
			isRunning = false;
			callback = terminateCallBack;
			terminateCallBack = null;
		}

		if (callback != null) {
			callback.run();
		}
	}

	private void recycleResources() {

		ByteBuffer buf = buffer;
		if (buf != null) {
			buffer = null;
			session.getSource().recycle(buffer);

		}
	}

	public void execute() throws Exception {
		ServerConnection sc = session.getSource();
		this.isRunning = true;
		this.packetId = 0;
		final PhysicalConnection conn = session.getTarget(node);
		if (!session.tryExistsCon(conn, node, new Runnable() {
			@Override
			public void run() {
				_execute(conn);
			}
		})) {
			// create new connection

			MycatConfig conf = MycatServer.getInstance().getConfig();
			PhysicalDBNode dn = conf.getDataNodes().get(node.getName());
			ConnectionMeta conMeta = new ConnectionMeta(dn.getDatabase(),
					sc.getCharset(), sc.getCharsetIndex(), sc.isAutocommit());
			dn.getConnection(conMeta, node, this, node);
		}

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
			session.clearResources();
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
		ErrorPacket err = new ErrorPacket();
		err.packetId = ++packetId;
		err.errno = ErrorCode.ER_YES;
		err.message = StringUtil.encode(
				"unknown backend charset: " + c.getCharset(), session
						.getSource().getCharset());

		this.backConnectionErr(err, c);
	}

	@Override
	public void connectionError(Throwable e, PhysicalConnection conn) {
		conn.setRunning(false);
		endRunning();
		ErrorPacket err = new ErrorPacket();
		err.packetId = ++packetId;
		err.errno = ErrorCode.ER_YES;
		err.message = StringUtil.encode(e.getMessage(), session.getSource()
				.getCharset());
		ServerConnection source = session.getSource();
		source.write(err.write(allocBuffer(), source));
	}

	@Override
	public void errorResponse(byte[] data, PhysicalConnection conn) {
		ErrorPacket err = new ErrorPacket();
		err.read(data);
		err.packetId = ++packetId;
		backConnectionErr(err, conn);

	}

	private void backConnectionErr(ErrorPacket errPkg, PhysicalConnection conn) {
		conn.setRunning(false);
		endRunning();
		session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled());
		ServerConnection source = session.getSource();
		source.setTxInterrupt();
		recycleResources();
		errPkg.write(source);
	}

	@Override
	public void okResponse(byte[] data, PhysicalConnection conn) {
		boolean executeResponse = conn.syncAndExcute();
		;
		if (executeResponse) {
			conn.setRunning(false);
			session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled());
			endRunning();
			ServerConnection source = session.getSource();
			OkPacket ok = new OkPacket();
			ok.read(data);

			recycleResources();
			// ok.packetId = ++packetId;// OK_PACKET
			source.setLastInsertId(ok.insertId);
			ok.write(source);

		}
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		ServerConnection source = session.getSource();
		conn.setRunning(false);
		conn.recordSql(source.getHost(), source.getSchema(),
				node.getStatement());
		session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled());
		endRunning();
		eof[3] = ++packetId;
		buffer = source.writeToBuffer(eof, allocBuffer());
		source.write(buffer);

	}

	/**
	 * lazy create ByteBuffer only when needed
	 * 
	 * @return
	 */
	private ByteBuffer allocBuffer() {
		if (buffer == null) {
			buffer = session.getSource().allocate();
		}
		return buffer;
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {
		header[3] = ++packetId;
		ServerConnection source = session.getSource();
		buffer = source.writeToBuffer(header, allocBuffer());
		for (int i = 0, len = fields.size(); i < len; ++i) {
			byte[] field = fields.get(i);
			field[3] = ++packetId;
			buffer = source.writeToBuffer(field, buffer);
		}
		eof[3] = ++packetId;
		buffer = source.writeToBuffer(eof, buffer);

	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
		row[3] = ++packetId;
		buffer = session.getSource().writeToBuffer(row, allocBuffer());
	}

	@Override
	public void writeQueueAvailable() {

	}

	@Override
	public void connectionClose(PhysicalConnection conn, String reason) {
		ErrorPacket err = new ErrorPacket();
		err.packetId = ++packetId;
		err.errno = ErrorCode.ER_YES;
		err.message = StringUtil.encode(reason, session.getSource()
				.getCharset());
		this.backConnectionErr(err, conn);

	}

	public void clearResources() {

	}
}