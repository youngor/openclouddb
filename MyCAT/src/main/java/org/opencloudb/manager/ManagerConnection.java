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
package org.opencloudb.manager;

import java.io.EOFException;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;
import org.opencloudb.MycatServer;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.net.FrontendConnection;
import org.opencloudb.util.TimeUtil;

/**
 * @author mycat
 */
public class ManagerConnection extends FrontendConnection {
	private static final Logger LOGGER = Logger
			.getLogger(ManagerConnection.class);
	private static final long AUTH_TIMEOUT = 15 * 1000L;

	public ManagerConnection(SocketChannel channel) {
		super(channel);
	}

	@Override
	public boolean isIdleTimeout() {
		if (isAuthenticated) {
			return super.isIdleTimeout();
		} else {
			return TimeUtil.currentTimeMillis() > Math.max(lastWriteTime,
					lastReadTime) + AUTH_TIMEOUT;
		}
	}

	@Override
	public void handle(final byte[] data) {
		MycatServer.getInstance().getManagerExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					handler.handle(data);
				} catch (Throwable t) {
					error(ErrorCode.ERR_HANDLE_DATA, t);
				}
			}
		});
	}

}