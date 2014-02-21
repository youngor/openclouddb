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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.opencloudb.util.TimeUtil;

/**
 * @author mycat
 */
public abstract class BackendConnection extends AbstractConnection {

	protected long id;
	protected String host;
	protected int port;
	protected int localPort;
	protected long idleTimeout;
	protected NIOConnector connector;
	protected boolean isFinishConnect;
	// supress socket read event temporary ,because client
	protected volatile boolean suppressReadTemporay;

	public BackendConnection(SocketChannel channel) {
		super(channel);
	}

	public boolean isSuppressReadTemporay() {
		return suppressReadTemporay;
	}

	public void setSuppressReadTemporay(boolean suppressReadTemporay) {
		this.suppressReadTemporay = suppressReadTemporay;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public boolean isIdleTimeout() {
		return TimeUtil.currentTimeMillis() > Math.max(lastWriteTime,
				lastReadTime) + idleTimeout;
	}

	public void setConnector(NIOConnector connector) {
		this.connector = connector;
	}

	public void connect(Selector selector) throws IOException {
		channel.register(selector, SelectionKey.OP_CONNECT, this);
		channel.connect(new InetSocketAddress(host, port));
	}

	public boolean finishConnect() throws IOException {
		if (channel.isConnectionPending()) {
			channel.finishConnect();
			localPort = channel.socket().getLocalPort();
			isFinishConnect = true;
			return true;
		} else {
			return false;
		}
	}

	public void setProcessor(NIOProcessor processor) {
		super.setProcessor(processor);
		processor.addBackend(this);
	}

	@Override
	public String toString() {
		return "BackendConnection [id=" + id + ", host=" + host + ", port="
				+ port + ", localPort=" + localPort + ", suppressReadTemporay="
				+ suppressReadTemporay + "]";
	}

}