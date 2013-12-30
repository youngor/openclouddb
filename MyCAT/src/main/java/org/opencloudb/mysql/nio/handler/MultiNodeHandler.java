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
 * (created at 2012-5-4)
 */
package org.opencloudb.mysql.nio.handler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.server.NonBlockingSession;
import org.opencloudb.util.StringUtil;

/**
 * @author mycat
 */
abstract class MultiNodeHandler implements ResponseHandler, Terminatable {
	private static final Logger LOGGER = Logger
			.getLogger(MultiNodeHandler.class);
	protected final ReentrantLock lock = new ReentrantLock();
	protected final NonBlockingSession session;
	private AtomicBoolean isFailed = new AtomicBoolean(false);
	private volatile String error;
	protected byte packetId;

	public MultiNodeHandler(NonBlockingSession session) {
		if (session == null) {
			throw new IllegalArgumentException("session is null!");
		}
		this.session = session;
	}

	public void setFail(String errMsg) {
		isFailed.set(true);
		error = errMsg;
	}

	public boolean isFail() {
		return isFailed.get();
	}

	private int nodeCount;
	private Runnable terminateCallBack;

	@Override
	public void terminate(Runnable terminateCallBack) {
		boolean zeroReached = false;
		lock.lock();
		try {
			if (nodeCount > 0) {
				this.terminateCallBack = terminateCallBack;
			} else {
				zeroReached = true;
			}
		} finally {
			lock.unlock();
		}
		if (zeroReached) {
			terminateCallBack.run();
		}
	}

	protected void decrementCountToZero() {
		Runnable callback;
		lock.lock();
		try {
			nodeCount = 0;
			callback = this.terminateCallBack;
			this.terminateCallBack = null;
		} finally {
			lock.unlock();
		}
		if (callback != null) {
			callback.run();
		}
	}

	public void connectionError(Throwable e, PhysicalConnection conn) {
		boolean canClose = decrementCountBy(1);
		this.tryErrorFinished(conn, canClose);
	}

	public void errorResponse(byte[] data, PhysicalConnection conn) {
		conn.setRunning(false);
		session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled());
		ErrorPacket err = new ErrorPacket();
		err.read(data);
		String errmsg = new String(err.message);
		this.setFail(errmsg);
		LOGGER.warn("error response from " + conn + " err " + errmsg + " code:"
				+ err.errno);

		this.tryErrorFinished(conn, this.decrementCountBy(1));
	}

	public boolean clearIfSessionClosed(NonBlockingSession session) {
		if (session.closed()) {
			LOGGER.info("session closed ,clear resources " + session);
			session.clearResources();
			this.clearResources();
			return true;
		} else {
			return false;
		}

	}

	protected boolean decrementCountBy(int finished) {
		boolean zeroReached = false;
		Runnable callback = null;
		lock.lock();
		try {
			if (zeroReached = --nodeCount == 0) {
				callback = this.terminateCallBack;
				this.terminateCallBack = null;
			}
		} finally {
			lock.unlock();
		}
		if (zeroReached && callback != null) {
			callback.run();
		}
		return zeroReached;
	}

	protected void reset(int initCount) {
		nodeCount = initCount;
		isFailed.set(false);
		error = null;
		packetId = 0;
	}

	protected ErrorPacket createErrPkg(String errmgs) {
		ErrorPacket err = new ErrorPacket();
		lock.lock();
		try {
			err.packetId = ++packetId;
		} finally {
			lock.unlock();
		}
		err.errno = ErrorCode.ER_YES;
		err.message = StringUtil.encode(errmgs, session.getSource()
				.getCharset());
		return err;
	}

	protected void tryErrorFinished(PhysicalConnection conn, boolean allEnd) {
		if (allEnd) {
			if (session.getSource().isAutocommit()) {
				session.clearResources();
				createErrPkg(this.error).write(session.getSource());
			} else {
				session.getSource().setTxInterrupt();
			}
			// clear resouces
			clearResources();
		}

	}

	public void connectionClose(PhysicalConnection conn, String reason) {
		conn.setRunning(false);
		this.setFail("closed connection:" + reason + " con:" + conn);
		session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled());
		tryErrorFinished(conn, this.decrementCountBy(1));
	}

	public void clearResources() {
	}
}