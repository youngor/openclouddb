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
 * (created at 2012-5-12)
 */
package org.opencloudb.mysql.nio.handler;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.net.BackendConnection;
import org.opencloudb.net.mysql.CommandPacket;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.net.mysql.MySQLPacket;
import org.opencloudb.server.NonBlockingSession;

/**
 * @author mycat
 */
public class KillConnectionHandler implements ResponseHandler {
	private static final Logger LOGGER = Logger
			.getLogger(KillConnectionHandler.class);

	private final PhysicalConnection killee;
	private final NonBlockingSession session;
	private final Runnable finishHook;
	private final AtomicInteger counter;

	public KillConnectionHandler(PhysicalConnection killee,
			NonBlockingSession session, Runnable finishHook,
			AtomicInteger counter) {
		this.killee = killee;
		this.session = session;
		this.finishHook = finishHook;
		this.counter = counter;
	}

	@Override
	public void connectionAcquired(PhysicalConnection conn) {
		conn.setResponseHandler(this);
		CommandPacket packet = new CommandPacket();
		packet.packetId = 0;
		packet.command = MySQLPacket.COM_QUERY;
		packet.arg = new StringBuilder("KILL ").append(killee.getThreadId())
				.toString().getBytes();
		packet.write((BackendConnection) conn);
	}

	private void finished() {
		if (counter.decrementAndGet() <= 0) {
			finishHook.run();
		}
	}

	@Override
	public void connectionError(Throwable e, PhysicalConnection conn) {
		if (conn != null) {
			conn.close();
		}
		killee.close();
		finished();
	}

	@Override
	public void okResponse(byte[] ok, PhysicalConnection conn) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("kill connection success connection id:"
					+ killee.getThreadId());
		}
		conn.release();
		killee.close();
		finished();
	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		LOGGER.error(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getSource())
				.append(": field's eof").toString());
		conn.quit();
		killee.close();
		finished();
	}

	@Override
	public void errorResponse(byte[] data, PhysicalConnection conn) {
		ErrorPacket err = new ErrorPacket();
		err.read(data);
		String msg = null;
		try {
			msg = new String(err.message, conn.getCharset());
		} catch (UnsupportedEncodingException e) {
			msg = new String(err.message);
		}
		LOGGER.warn("kill backend connection " + killee + " failed: " + msg
				+ " con:" + conn);
		conn.release();
		killee.close();
		finished();
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {
	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
	}

	@Override
	public void writeQueueAvailable() {
		// TODO Auto-generated method stub

	}

}