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
package org.opencloudb.net;

import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;
import org.opencloudb.buffer.BufferQueue;
import org.opencloudb.config.ErrorCode;

/**
 * @author mycat
 */
public final class NIOConnector implements
		CompletionHandler<Void, BackendAIOConnection> {
	private static final Logger LOGGER = Logger.getLogger(NIOConnector.class);
	private static final ConnectIdGenerator ID_GENERATOR = new ConnectIdGenerator();
	protected int socketRecvBuffer = 16 * 1024;
	protected int socketSendBuffer = 8 * 1024;
	protected int packetHeaderSize = 4;
	protected int maxPacketSize = 16 * 1024 * 1024;
	protected int writeQueueCapcity = 8;
	protected long idleTimeout = 8 * 3600 * 1000L;
	private NIOProcessor[] processors;
	private int nextProcessor;
	private long connectCount;

	@Override
	public void completed(Void result, BackendAIOConnection attachment) {
		finishConnect(attachment);
	}

	public int getSocketRecvBuffer() {
		return socketRecvBuffer;
	}

	public void setSocketRecvBuffer(int socketRecvBuffer) {
		this.socketRecvBuffer = socketRecvBuffer;
	}

	public int getSocketSendBuffer() {
		return socketSendBuffer;
	}

	public void setSocketSendBuffer(int socketSendBuffer) {
		this.socketSendBuffer = socketSendBuffer;
	}

	public int getPacketHeaderSize() {
		return packetHeaderSize;
	}

	public void setPacketHeaderSize(int packetHeaderSize) {
		this.packetHeaderSize = packetHeaderSize;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	public int getWriteQueueCapcity() {
		return writeQueueCapcity;
	}

	public void setWriteQueueCapcity(int writeQueueCapcity) {
		this.writeQueueCapcity = writeQueueCapcity;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public void failed(Throwable exc, BackendAIOConnection conn) {
		conn.onConnectFailed(exc);
	}

	private void postConnect(BackendAIOConnection c) {
		c.setPacketHeaderSize(packetHeaderSize);
		c.setMaxPacketSize(maxPacketSize);
		c.setWriteQueue(new BufferQueue(writeQueueCapcity));
		c.setIdleTimeout(idleTimeout);
	}

	public long getConnectCount() {
		return connectCount;
	}

	public void setProcessors(NIOProcessor[] processors) {
		this.processors = processors;
	}

	private void finishConnect(BackendAIOConnection c) {
		postConnect(c);
		try {
			if (c.finishConnect()) {
				c.setId(ID_GENERATOR.getId());
				NIOProcessor processor = nextProcessor();
				c.setProcessor(processor);
				c.register();
			}
		} catch (Throwable e) {
			LOGGER.info("connect err " + e);
			c.error(ErrorCode.ERR_CONNECT_SOCKET, e);
		}
	}

	private NIOProcessor nextProcessor() {
		int inx = ++nextProcessor;
		if (nextProcessor >= processors.length) {
			nextProcessor = 0;
		}
		return processors[inx];
	}

	/**
	 * 后端连接ID生成器
	 * 
	 * @author mycat
	 */
	private static class ConnectIdGenerator {

		private static final long MAX_VALUE = Long.MAX_VALUE;

		private long connectId = 0L;
		private final Object lock = new Object();

		private long getId() {
			synchronized (lock) {
				if (connectId >= MAX_VALUE) {
					connectId = 0L;
				}
				return ++connectId;
			}
		}
	}

}