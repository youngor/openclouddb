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
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.opencloudb.net.factory.FrontendConnectionFactory;

/**
 * @author mycat
 */
public final class NIOAcceptor extends Thread {
	private static final Logger LOGGER = Logger.getLogger(NIOAcceptor.class);
	private static final AcceptIdGenerator ID_GENERATOR = new AcceptIdGenerator();

	private final int port;
	private final AsynchronousServerSocketChannel serverChannel;
	private final FrontendConnectionFactory factory;
	private NIOProcessor[] processors;
	private int nextProcessor;
	private long acceptCount;

	public NIOAcceptor(String name, int port,
			FrontendConnectionFactory factory, AsynchronousChannelGroup group)
			throws IOException {
		super.setName(name);
		this.port = port;
		this.factory = factory;
		serverChannel = AsynchronousServerSocketChannel.open(group);
		/** 设置TCP属性 */
		serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 16 * 1024);
		// backlog=100
		serverChannel.bind(new InetSocketAddress(port), 100);
	}

	public void pendingAccept() {
		if (serverChannel.isOpen()) {
			Future<AsynchronousSocketChannel> future = serverChannel.accept();
			try {
				AsynchronousSocketChannel channel = future.get();
				accept(channel);
			} catch (Exception e) {

				e.printStackTrace();

			}

		} else {

			throw new IllegalStateException("Controller has been closed");

		}

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

	public void run() {
		System.out.println("started");
		while (true) {
			try {
				pendingAccept();

			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	private void accept(AsynchronousSocketChannel channel) {
		try {
			FrontendConnection c = factory.make(channel);
			c.setAccepted(true);
			c.setId(ID_GENERATOR.getId());
			NIOProcessor processor = nextProcessor();
			c.setProcessor(processor);
			c.register();
		} catch (Throwable e) {
			closeChannel(channel);
			LOGGER.warn(getName(), e);
		}
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

		private long acceptId = 0L;
		private final Object lock = new Object();

		private long getId() {
			synchronized (lock) {
				if (acceptId >= MAX_VALUE) {
					acceptId = 0L;
				}
				return ++acceptId;
			}
		}
	}

}