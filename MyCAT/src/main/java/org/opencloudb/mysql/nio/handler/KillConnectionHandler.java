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
package org.opencloudb.mysql.nio.handler;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.opencloudb.backend.BackendConnection;
import org.opencloudb.mysql.nio.MySQLConnection;
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

	private final MySQLConnection killee;
	private final NonBlockingSession session;
	private final Runnable finishHook;
	private final AtomicInteger counter;

	public KillConnectionHandler(BackendConnection killee,
			NonBlockingSession session, Runnable finishHook,
			AtomicInteger counter) {
		this.killee = (MySQLConnection) killee;
		this.session = session;
		this.finishHook = finishHook;
		this.counter = counter;
	}

	@Override
	public void connectionAcquired(BackendConnection conn) {
		MySQLConnection mysqlCon = (MySQLConnection) conn;
		conn.setResponseHandler(this);
		CommandPacket packet = new CommandPacket();
		packet.packetId = 0;
		packet.command = MySQLPacket.COM_QUERY;
		packet.arg = new StringBuilder("KILL ").append(killee.getThreadId())
				.toString().getBytes();
		packet.write(mysqlCon);
	}

	private void finished() {
		if (counter.decrementAndGet() <= 0) {
			finishHook.run();
		}
	}

	@Override
	public void connectionError(Throwable e, BackendConnection conn) {
		killee.close("exception:" + e.toString());
		finished();
	}

	@Override
	public void okResponse(byte[] ok, BackendConnection conn) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("kill connection success connection id:"
					+ killee.getThreadId());
		}
		conn.release();
		killee.close("killed");
		finished();
	}

	@Override
	public void rowEofResponse(byte[] eof, BackendConnection conn) {
		LOGGER.error(new StringBuilder().append("unexpected packet for ")
				.append(conn).append(" bound by ").append(session.getSource())
				.append(": field's eof").toString());
		conn.quit();
		killee.close("killed");
		finished();
	}

	@Override
	public void errorResponse(byte[] data, BackendConnection conn) {
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
		killee.close("exception:" + msg);
		finished();
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, BackendConnection conn) {
	}

	@Override
	public void rowResponse(byte[] row, BackendConnection conn) {
	}

	@Override
	public void writeQueueAvailable() {

	}

	@Override
	public void connectionClose(BackendConnection conn, String reason) {
		finished();

	}

}