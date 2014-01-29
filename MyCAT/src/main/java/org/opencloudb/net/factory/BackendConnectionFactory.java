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
package org.opencloudb.net.factory;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import org.opencloudb.buffer.BufferQueue;
import org.opencloudb.net.BackendConnection;
import org.opencloudb.net.NIOConnector;

/**
 * @author mycat
 */
public abstract class BackendConnectionFactory {

    protected int socketRecvBuffer = 16 * 1024;
    protected int socketSendBuffer = 8 * 1024;
    protected int packetHeaderSize = 4;
    protected int maxPacketSize = 16 * 1024 * 1024;
    protected int writeQueueCapcity = 8;
    protected long idleTimeout = 8 * 3600 * 1000L;

    protected SocketChannel openSocketChannel() throws IOException {
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            Socket socket = channel.socket();
            socket.setReceiveBufferSize(socketRecvBuffer);
            socket.setSendBufferSize(socketSendBuffer);
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
        } catch (IOException e) {
            closeChannel(channel);
            throw e;
        } catch (RuntimeException e) {
            closeChannel(channel);
            throw e;
        }
        return channel;
    }

    protected void postConnect(BackendConnection c, NIOConnector connector) {
        c.setPacketHeaderSize(packetHeaderSize);
        c.setMaxPacketSize(maxPacketSize);
        c.setWriteQueue(new BufferQueue(writeQueueCapcity));
        c.setIdleTimeout(idleTimeout);
        c.setConnector(connector);
        connector.postConnect(c);
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

    private static void closeChannel(SocketChannel channel) {
        if (channel == null) {
            return;
        }
        Socket socket = channel.socket();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        try {
            channel.close();
        } catch (IOException e) {
        }
    }

}