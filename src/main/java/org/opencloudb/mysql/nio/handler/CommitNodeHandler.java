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
 * (created at 2012-4-28)
 */
package org.opencloudb.mysql.nio.handler;

import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.NonBlockingSession;
import org.opencloudb.server.ServerConnection;

/**
 * @author mycat
 */
public class CommitNodeHandler extends MultiNodeHandler {
	private static final Logger LOGGER = Logger
			.getLogger(CommitNodeHandler.class);
	private OkPacket okPacket;

	public CommitNodeHandler(NonBlockingSession session) {
		super(session);
	}

	public void commit() {
		commit(null);
	}

	private void commit(OkPacket packet) {
		final int initCount = session.getTargetCount();
		lock.lock();
		try {
			reset(initCount);
			okPacket = packet;
		} finally {
			lock.unlock();
		}

		if (clearIfSessionClosed(session)) {
			return;
		}

		// 执行
		Executor executor = session.getSource().getProcessor().getExecutor();
		int started = 0;
		for (RouteResultsetNode rrn : session.getTargetKeys()) {
			if (rrn == null) {
				try {
					LOGGER.error("null is contained in RoutResultsetNodes, source = "
							+ session.getSource());
				} catch (Exception e) {
				}
				continue;
			}
			final PhysicalConnection conn = session.getTarget(rrn);
			if (conn != null) {
				conn.setRunning(true);
				executor.execute(new Runnable() {
					@Override
					public void run() {
						if (clearIfSessionClosed(session)) {
							return;
						}
						conn.setResponseHandler(CommitNodeHandler.this);
						conn.commit();
					}
				});
				++started;
			}
		}

		if (started < initCount && decrementCountBy(initCount - started)) {
			/**
			 * assumption: only caused by front-end connection close. <br/>
			 * Otherwise, packet must be returned to front-end
			 */
			session.clearResources();
		}
	}

	@Override
	public void connectionAcquired(PhysicalConnection conn) {
		LOGGER.error("unexpected invocation: connectionAcquired from commit");
		
	}

	@Override
	public void okResponse(byte[] ok, PhysicalConnection conn) {
		conn.setRunning(false);
		if (decrementCountBy(1)) {
			session.clearResources();
			if (this.isFail() || session.closed()) {
				tryErrorFinished(conn, true);
			} else {
				if (okPacket == null) {
					ServerConnection source = session.getSource();
					source.write(ok);
				} else {
					okPacket.write(session.getSource());
				}
			}
		}
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		LOGGER.error(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getSource())
				.append(": field's eof").toString());
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {
		LOGGER.error(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getSource())
				.append(": field's eof").toString());
	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
		LOGGER.warn(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getSource())
				.append(": row data packet").toString());
	}

	@Override
	public void writeQueueAvailable() {

	}

}