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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.ConnectionMeta;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.backend.PhysicalDBNode;
import org.opencloudb.mpp.ColMeta;
import org.opencloudb.mpp.DataMergeService;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.net.mysql.FieldPacket;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.net.mysql.RowDataPacket;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.NonBlockingSession;
import org.opencloudb.server.ServerConnection;

/**
 * @author mycat
 */
public class MultiNodeQueryHandler extends MultiNodeHandler {
	private static final Logger LOGGER = Logger
			.getLogger(MultiNodeQueryHandler.class);

	private final RouteResultsetNode[] route;
	private final NonBlockingSession session;
	// private final CommitNodeHandler icHandler;
	private final DataMergeService dataMergeSvr;
	private volatile boolean mergeOutputed;
	private final boolean autocommit;

	public MultiNodeQueryHandler(RouteResultsetNode[] route,
			boolean autocommit, NonBlockingSession session,
			DataMergeService dataMergeSvr) {
		super(session);
		if (route == null) {
			throw new IllegalArgumentException("routeNode is null!");
		}
		this.autocommit = session.getSource().isAutocommit();
		this.session = session;
		this.route = route;
		this.lock = new ReentrantLock();
		// this.icHandler = new CommitNodeHandler(session);
		this.dataMergeSvr = dataMergeSvr;
	}

	private final ReentrantLock lock;
	private long affectedRows;
	private long insertId;
	private ByteBuffer buffer;
	private boolean fieldsReturned;

	public void execute() throws Exception {
		ServerConnection sc = session.getSource();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			this.reset(route.length);
			this.fieldsReturned = false;
			this.affectedRows = 0L;
			this.insertId = 0L;
			this.buffer = sc.allocate();
		} finally {
			lock.unlock();
		}
		MycatConfig conf = MycatServer.getInstance().getConfig();

		for (final RouteResultsetNode node : route) {
			final PhysicalConnection conn = session.getTarget(node);
			if (session.tryExistsCon(conn, node, new Runnable() {
				@Override
				public void run() {

					_execute(conn, node);
				}
			})) {
				// to next node
				continue;
			}
			// create new connection
			PhysicalDBNode dn = conf.getDataNodes().get(node.getName());
			ConnectionMeta conMeta = new ConnectionMeta(dn.getDatabase(),
					sc.getCharset(), sc.getCharsetIndex(), autocommit);
			dn.getConnection(conMeta, node, this, node);

		}
	}

	private void _execute(PhysicalConnection conn, RouteResultsetNode node) {
		if (clearIfSessionClosed(session)) {
			return;
		}
		conn.setResponseHandler(this);
		conn.setRunning(true);
		try {
			conn.execute(node, session.getSource(), autocommit);
		} catch (IOException e) {
			connectionError(e, conn);
		}
	}

	@Override
	public void connectionAcquired(final PhysicalConnection conn) {
		final RouteResultsetNode node = (RouteResultsetNode) conn
				.getAttachment();
		session.bindConnection(node, conn);
		session.getSource().getProcessor().getExecutor()
				.execute(new Runnable() {
					@Override
					public void run() {
						_execute(conn, node);
					}
				});
	}

	@Override
	public void errorResponse(byte[] data, PhysicalConnection conn) {
		ErrorPacket err = new ErrorPacket();
		err.read(data);
		String errmsg = new String(err.message);
		LOGGER.warn("error response from " + conn + " err " + errmsg + " code:"
				+ err.errno);
		this.setFail(errmsg);
		// try connection and finish conditon check
		canClose(conn, true);
	}

	@Override
	public void okResponse(byte[] data, PhysicalConnection conn) {
		boolean executeResponse = conn.syncAndExcute();
		if (executeResponse) {
			if (clearIfSessionClosed(session)) {
				return;
			} else if (canClose(conn, false)) {
				return;
			}
			ServerConnection source = session.getSource();
			OkPacket ok = new OkPacket();
			ok.read(data);
			lock.lock();
			try {
				affectedRows += ok.affectedRows;
				if (ok.insertId > 0) {
					insertId = (insertId == 0) ? ok.insertId : Math.min(
							insertId, ok.insertId);
				}
			} finally {
				lock.unlock();
			}
			if (decrementCountBy(1)) {
				// clear resources
				clearResources();
				if (this.autocommit) {// clear all connections
					session.releaseConnections();
				}
				if (this.isFail() || session.closed()) {
					tryErrorFinished(conn, true);
					return;
				}
				lock.lock();
				try {

					ok.packetId = ++packetId;// OK_PACKET
					ok.affectedRows = affectedRows;
					if (insertId > 0) {
						ok.insertId = insertId;
						source.setLastInsertId(insertId);
					}
					ok.write(source);
				} catch (Exception e) {
					LOGGER.warn("exception happens in success notification: "
							+ session.getSource(), e);
					// return err package
					createErrPkg(e.toString()).write(source);

				} finally {
					lock.unlock();
				}
			}
		}
	}

	private boolean canClose(PhysicalConnection conn, boolean tryErrorFinish) {
		boolean allFinshed = false;
		conn.setRunning(false);
		ServerConnection source = session.getSource();
		RouteResultsetNode curNode = (RouteResultsetNode) conn.getAttachment();
		conn.recordSql(source.getHost(), source.getSchema(),
				curNode.getStatement());
		// realse this connection if safe
		session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled());
		if (tryErrorFinish) {
			allFinshed = this.decrementCountBy(1);
			this.tryErrorFinished(conn, allFinshed);
		}

		return allFinshed;
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		if (clearIfSessionClosed(session)) {
			return;
		} else if (canClose(conn, false)) {
			return;
		}
		ServerConnection source = session.getSource();
		if (decrementCountBy(1)) {
			if (this.autocommit) {// clear all connections
				session.releaseConnections();
			}

			if (this.isFail() || session.closed()) {
				tryErrorFinished(conn, true);
				return;
			}
			try {
				lock.lock();
				if (dataMergeSvr != null && !mergeOutputed) {
					int i = 0;
					int start = dataMergeSvr.getRrs().getLimitStart();
					int end = start + dataMergeSvr.getRrs().getLimitSize();
					Iterator<RowDataPacket> itor = dataMergeSvr.getResults()
							.iterator();
					while (itor.hasNext()) {

						if (i < start) {
							i++;
							continue;
						} else if (i == end) {
							break;
						}
						i++;
						RowDataPacket row = itor.next();
						itor.remove();
						row.packetId = ++packetId;
						buffer = row.write(buffer, source);
					}
				}
				eof[3] = ++packetId;
				source.write(source.writeToBuffer(eof, buffer));
			} finally {
				if (dataMergeSvr != null) {
					dataMergeSvr.clear();
				}
				lock.unlock();
			}
		}
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {
		lock.lock();
		try {
			if (fieldsReturned) {
				return;
			}
			fieldsReturned = true;
			header[3] = ++packetId;
			ServerConnection source = session.getSource();
			buffer = source.writeToBuffer(header, buffer);
			int fieldCount = fields.size();

			Map<String, ColMeta> columToIndx = new HashMap<String, ColMeta>(
					fieldCount);
			boolean needMerg = (dataMergeSvr != null)
					&& dataMergeSvr.getRrs().needMerge();
			for (int i = 0, len = fieldCount; i < len; ++i) {
				byte[] field = fields.get(i);
				if (needMerg) {
					FieldPacket fieldPkg = new FieldPacket();
					fieldPkg.read(field);
					String fieldName = new String(fieldPkg.name);
					if (columToIndx != null
							&& !columToIndx.containsKey(fieldName)) {

						columToIndx.put(fieldName,
								new ColMeta(i, fieldPkg.type));
					}
				}

				field[3] = ++packetId;
				buffer = source.writeToBuffer(field, buffer);
			}
			if (dataMergeSvr != null) {
				dataMergeSvr.onRowMetaData(columToIndx, fieldCount);

			}

			eof[3] = ++packetId;
			buffer = source.writeToBuffer(eof, buffer);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
		lock.lock();
		try {
			if (dataMergeSvr != null) {
				boolean canOutput = dataMergeSvr.onNewRecord(
						((RouteResultsetNode) conn.getAttachment()).getName(),
						row);
			} else {
				row[3] = ++packetId;
				buffer = session.getSource().writeToBuffer(row, buffer);
			}

		} finally {
			lock.unlock();
		}
	}

	@Override
	public void clearResources() {
		if (dataMergeSvr != null) {
			dataMergeSvr.clear();
		}

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

	@Override
	public void writeQueueAvailable() {

	}

}