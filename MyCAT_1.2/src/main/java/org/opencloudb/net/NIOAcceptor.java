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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.opencloudb.net.factory.FrontendConnectionFactory;

/**
 * @author mycat
 */
public final class NIOAcceptor implements
		CompletionHandler<AsynchronousSocketChannel, Long> {
	private static final Logger LOGGER = Logger.getLogger(NIOAcceptor.class);
	private static final AcceptIdGenerator ID_GENERATOR = new AcceptIdGenerator();

	private final int port;
	private final AsynchronousServerSocketChannel serverChannel;
	private final FrontendConnectionFactory factory;
	private NIOProcessor[] processors;
	private int nextProcessor;
	private long acceptCount;
   private final String name;
	public NIOAcceptor(String name, String ip,int port,
			FrontendConnectionFactory factory, AsynchronousChannelGroup group)
			throws IOException {
		this.name=name;
		this.port = port;
		this.factory = factory;
		serverChannel = AsynchronousServerSocketChannel.open(group);
		/** 设置TCP属性 */
		serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
		// backlog=100
		serverChannel.bind(new InetSocketAddress(ip,port), 100);
	}

	public String getName() {
		return name;
	}

	public void start() {
		this.pendingAccept();
	}

	public int getPort() {
		return port;
	}

	public long getAcceptCount() {
		return acceptCount;
	}

	public void setProcessors(NIOProcessor[] processors) {
		this.processors = processors;
	}

	private void accept(AsynchronousSocketChannel channel, Long id) {
		try {
			FrontendConnection c = factory.make(channel);
			c.setAccepted(true);
			c.setId(id);
			NIOProcessor processor = nextProcessor();
			c.setProcessor(processor);
			c.register();
		} catch (Throwable e) {
			closeChannel(channel);
		}
	}

	private void pendingAccept() {
		if (serverChannel.isOpen()) {
			serverChannel.accept(ID_GENERATOR.getId(), this);
		} else {
			throw new IllegalStateException(
					"MyCAT Server Channel has been closed");
		}

	}

	@Override
	public void completed(AsynchronousSocketChannel result, Long id) {
		accept(result, id);
		// next pending waiting
		pendingAccept();

	}

	@Override
	public void failed(Throwable exc, Long id) {
		LOGGER.info("acception connect failed:" + exc);
		// next pending waiting
		pendingAccept();

	}

	private NIOProcessor nextProcessor() {
		if (++nextProcessor == processors.length) {
			nextProcessor = 0;
		}
		return processors[nextProcessor];
	}

	private static void closeChannel(AsynchronousSocketChannel channel) {
		if (channel == null) {
			return;
		}
		try {
			channel.close();
		} catch (IOException e) {
		}
	}

	/**
	 * 前端连接ID生成器
	 * 
	 * @author mycat
	 */
	private static class AcceptIdGenerator {

		private static final long MAX_VALUE = 0xffffffffL;

		private AtomicLong acceptId = new AtomicLong();
		private final Object lock = new Object();

		private long getId() {
			long newValue = acceptId.getAndIncrement();
			if (newValue >= MAX_VALUE) {
				synchronized (lock) {
					newValue = acceptId.getAndIncrement();
					if (newValue >= MAX_VALUE) {
						acceptId.set(0);
					}
				}
				return acceptId.getAndDecrement();
			} else {
				return newValue;
			}
		}
	}
}
