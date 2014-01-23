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
	protected void idleCheck() {
		// nothing
	}

	@Override
	public String toString() {
		return "BackendConnection [id=" + id + ", host=" + host + ", port="
				+ port + ", localPort=" + localPort + ", suppressReadTemporay="
				+ suppressReadTemporay + "]";
	}

}