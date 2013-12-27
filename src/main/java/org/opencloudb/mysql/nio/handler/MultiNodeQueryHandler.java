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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
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
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			this.reset(route.length);
			this.fieldsReturned = false;
			this.affectedRows = 0L;
			this.insertId = 0L;
			this.buffer = session.getSource().allocate();
		} finally {
			lock.unlock();
		}

		if (session.closed()) {
			decrementCountToZero();
			recycleResources();
			return;
		}

		ThreadPoolExecutor executor = session.getSource().getProcessor()
				.getExecutor();
		for (final RouteResultsetNode node : route) {
			final PhysicalConnection conn = session.getTarget(node);
			if (conn != null) {
				if (!conn.isFromSlaveDB() || node.canRunnINReadDB(autocommit)) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("found connections in session to use "
								+ conn + " for " + node);
					}
					conn.setAttachment(node);
					executor.execute(new Runnable() {
						@Override
						public void run() {
							_execute(conn, node);
						}
					});
					continue;
				}
			}
			MycatConfig conf = MycatServer.getInstance().getConfig();
			PhysicalDBNode dn = conf.getDataNodes().get(node.getName());
			dn.getConnection(node, autocommit, this, node);

		}
	}

	private void _execute(PhysicalConnection conn, RouteResultsetNode node) {
		conn.setResponseHandler(this);
		conn.setRunning(true);
		if (session.closed()) {
			backendConnError(conn, "failed or cancelled by other thread");
			return;
		}
		try {
			conn.execute(node, session.getSource(), autocommit);
		} catch (IOException e) {
			connectionError(e, conn);
		}
	}

	@Override
	protected void recycleResources() {
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
	public void connectionAcquired(final PhysicalConnection conn) {
		Object attachment = conn.getAttachment();
		if (!(attachment instanceof RouteResultsetNode)) {
			backendConnError(
					conn,
					new StringBuilder()
							.append("wrong attachement from connection build: ")
							.append(conn).append(" bound by ")
							.append(session.getSource()).toString());
			conn.release();
			return;
		}
		final RouteResultsetNode node = (RouteResultsetNode) attachment;
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
	public void connectionError(Throwable e, PhysicalConnection conn) {
		// LOGGER.warn("connectionError "+conn+ " err:"+e);
		backendConnError(conn, "connection err!");
	}

	@Override
	public void errorResponse(byte[] data, PhysicalConnection conn) {
		ErrorPacket err = new ErrorPacket();
		err.read(data);
		LOGGER.warn("error response from " + conn + " err "
				+ new String(err.message));
		conn.setRunning(false);
		if (this.autocommit) {
			RouteResultsetNode node = (RouteResultsetNode) conn.getAttachment();
			session.releaseConnection(node, LOGGER.isDebugEnabled());
		}

		backendConnError(conn, err);
	}

	@Override
	public void okResponse(byte[] data, PhysicalConnection conn) {
		boolean executeResponse = false;
		try {
			executeResponse = conn.syncAndExcute();
		} catch (UnsupportedEncodingException e) {
			connectionError(e, conn);
		}
		RouteResultsetNode curNode = null;
		if (executeResponse) {
			ServerConnection source = session.getSource();
			conn.setRunning(false);
			Object attachment = conn.getAttachment();
			if (attachment instanceof RouteResultsetNode) {
				curNode = (RouteResultsetNode) attachment;
				conn.recordSql(source.getHost(), source.getSchema(),
						curNode.getStatement());
			} else {
				LOGGER.warn(new StringBuilder().append("back-end conn: ")
						.append(conn).append(" has wrong attachment: ")
						.append(attachment).append(", for front-end conn: ")
						.append(source));
			}
			// release cur finished node and connection
			if (autocommit) {
				session.releaseConnection(curNode, LOGGER.isDebugEnabled());
			}
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
				if (isFail.get()) {
					notifyError();
					return;
				}
				try {
					recycleResources();
					ok.packetId = ++packetId;// OK_PACKET
					ok.affectedRows = affectedRows;
					if (insertId > 0) {
						ok.insertId = insertId;
						source.setLastInsertId(insertId);
					}

					if (source.isAutocommit()) {
						session.releaseConnections();
						ok.write(source);
					} else {
						ok.write(source);
					}
				} catch (Exception e) {
					LOGGER.warn("exception happens in success notification: "
							+ session.getSource(), e);
				}
			}
		}
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		conn.setRunning(false);
		ServerConnection source = session.getSource();
		RouteResultsetNode curNode = null;
		Object attachment = conn.getAttachment();
		if (attachment instanceof RouteResultsetNode) {
			curNode = (RouteResultsetNode) attachment;
			conn.recordSql(source.getHost(), source.getSchema(),
					curNode.getStatement());
		} else {
			LOGGER.warn(new StringBuilder().append("back-end conn: ")
					.append(conn).append(" has wrong attachment: ")
					.append(attachment).append(", for front-end conn: ")
					.append(source));
		}
		if (source.isAutocommit()) {
			session.releaseConnection(curNode, LOGGER.isDebugEnabled());
		}
		if (decrementCountBy(1)) {
			if (isFail.get()) {
				notifyError();
				recycleResources();
				return;
			}
			try {
				if (source.isAutocommit()) {
					session.releaseConnections();
				}

			} catch (Exception e) {
				LOGGER.warn("exception happens in success notification: "
						+ session.getSource(), e);
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
	public void writeQueueAvailable() {
		// TODO Auto-generated method stub

	}

}