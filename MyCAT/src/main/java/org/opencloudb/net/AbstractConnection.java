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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.buffer.BufferQueue;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.util.TimeUtil;

/**
 * @author mycat
 */
public abstract class AbstractConnection implements NIOConnection {
	protected static final Logger LOGGER = Logger
			.getLogger(AbstractConnection.class);
	private static final int OP_NOT_READ = ~SelectionKey.OP_READ;
	private static final int OP_NOT_WRITE = ~SelectionKey.OP_WRITE;
	protected final SocketChannel channel;
	protected NIOProcessor processor;
	protected NIOHandler handler;
	protected SelectionKey processKey;
	protected int packetHeaderSize;
	protected int maxPacketSize;
	protected int readBufferOffset;
	private ByteBuffer readBuffer;
	protected BufferQueue writeQueue;
	protected boolean isRegistered;
	protected final AtomicBoolean isClosed;
	protected boolean isSocketClosed;
	protected long startupTime;
	protected long lastReadTime;
	protected long lastWriteTime;
	protected long netInBytes;
	protected long netOutBytes;
	protected int writeAttempts;
	protected final ReentrantLock keyLock = new ReentrantLock();
	private long idleTimeout;

	public AbstractConnection(SocketChannel channel) {
		this.channel = channel;
		this.isClosed = new AtomicBoolean(false);
		this.startupTime = TimeUtil.currentTimeMillis();
		this.lastReadTime = startupTime;
		this.lastWriteTime = startupTime;
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

	public SocketChannel getChannel() {
		return channel;
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

	public long getStartupTime() {
		return startupTime;
	}

	public long getLastReadTime() {
		return lastReadTime;
	}

	public void setProcessor(NIOProcessor processor) {
		this.processor = processor;
		this.readBuffer = processor.getBufferPool().allocate();
	}

	public long getLastWriteTime() {
		return lastWriteTime;
	}

	public long getNetInBytes() {
		return netInBytes;
	}

	public long getNetOutBytes() {
		return netOutBytes;
	}

	public int getWriteAttempts() {
		return writeAttempts;
	}

	public NIOProcessor getProcessor() {
		return processor;
	}

	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}

	public BufferQueue getWriteQueue() {
		return writeQueue;
	}

	public void setWriteQueue(BufferQueue writeQueue) {
		this.writeQueue = writeQueue;
	}

	public ByteBuffer allocate() {
		ByteBuffer buffer = this.processor.getBufferPool().allocate();
		return buffer;
	}

	public final void recycle(ByteBuffer buffer) {
		this.processor.getBufferPool().recycle(buffer);
	}

	public final void recycleIfNeed(ByteBuffer buffer) {
		this.processor.getBufferPool().safeRecycle(buffer);
	}

	public void writeQueueBlocked() {

	}

	public void writeQueueAvailable() {
	}

	public void setHandler(NIOHandler handler) {
		this.handler = handler;
	}

	@Override
	public void handle(byte[] data) {
		try {
			handler.handle(data);
		} catch (Throwable e) {
			close("exeption:" + e.toString());
			if (e instanceof ConnectionException) {
				error(ErrorCode.ERR_CONNECT_SOCKET, e);
			} else {
				error(ErrorCode.ERR_HANDLE_DATA, e);
			}
		}
	}

	@Override
	public void register(Selector selector) throws IOException {
		try {
			processKey = channel.register(selector, SelectionKey.OP_READ, this);
			isRegistered = true;
		} finally {
			if (isClosed.get()) {
				clearSelectionKey();
			}
		}
	}

	@Override
	public void read() throws IOException {
		ByteBuffer buffer = this.readBuffer;
		int got = channel.read(buffer);
		lastReadTime = TimeUtil.currentTimeMillis();
		if (got < 0) {
			if (!this.isClosed()) {
				this.close("socket closed");
				return;
			}
		} else if (got == 0) {
			return;
		}
		netInBytes += got;
		processor.addNetInBytes(got);

		// 澶勭悊鏁版嵁
		int offset = readBufferOffset, length = 0, position = buffer.position();
		for (;;) {
			length = getPacketLength(buffer, offset);
			if (length == -1) {
				if (!buffer.hasRemaining()) {
					buffer = checkReadBuffer(buffer, offset, position);
				}
				break;
			}
			if (position >= offset + length) {
				buffer.position(offset);
				byte[] data = new byte[length];
				buffer.get(data, 0, length);
				handle(data);

				offset += length;
				if (position == offset) {
					if (readBufferOffset != 0) {
						readBufferOffset = 0;
					}
					buffer.clear();
					break;
				} else {
					readBufferOffset = offset;
					buffer.position(position);
					continue;
				}
			} else {
				if (!buffer.hasRemaining()) {
					buffer = checkReadBuffer(buffer, offset, position);
				}
				break;
			}
		}
	}

	public void write(byte[] data) {
		ByteBuffer buffer = allocate();
		buffer = writeToBuffer(data, buffer);
		write(buffer);
	}

	@Override
	public final void write(ByteBuffer buffer) {
		if (isClosed.get()) {
			recycle(buffer);
			return;
		}
		if (isRegistered) {
			try {
				int writeQueueStatus = writeQueue.put(buffer);
				if ((processKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
					enableWrite(true);
				}

				switch (writeQueueStatus) {
				case BufferQueue.NEARLY_EMPTY: {
					this.writeQueueAvailable();
					break;
				}
				case BufferQueue.NEARLY_FULL: {
					this.writeQueueBlocked();
					break;
				}
				}

			} catch (InterruptedException e) {
				error(ErrorCode.ERR_PUT_WRITE_QUEUE, e);
				return;
			}

		} else {
			recycle(buffer);
			close("not registed con");
		}
	}

	@Override
	public void writeByQueue() throws IOException {

		// System.out.println("writeByQueue ");
		if (isClosed.get()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("not write queue ,connection closed " + this);
			}
			return;
		}

		boolean noMoreData = write0();
		if (noMoreData) {
			disableWrite();
		} else {
			if ((processKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
				enableWrite(false);
			}
		}

	}

	public void enableRead() {

		boolean needWakeup = false;
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("enable read fail " + e);
		}
		if (needWakeup) {
			processKey.selector().wakeup();
		}
	}

	/**
	 * 鍏抽棴璇讳簨浠�
	 */
	public void disableRead() {

		SelectionKey key = this.processKey;
		key.interestOps(key.interestOps() & OP_NOT_READ);
	}

	public ByteBuffer checkWriteBuffer(ByteBuffer buffer, int capacity) {
		if (capacity > buffer.remaining()) {
			write(buffer);
			return allocate();
		} else {
			return buffer;
		}
	}

	public ByteBuffer writeToBuffer(byte[] src, ByteBuffer buffer) {
		int offset = 0;
		int length = src.length;
		int remaining = buffer.remaining();
		while (length > 0) {
			if (remaining >= length) {
				buffer.put(src, offset, length);
				break;
			} else {
				buffer.put(src, offset, remaining);
				write(buffer);
				buffer = allocate();
				offset += remaining;
				length -= remaining;
				remaining = buffer.remaining();
				continue;
			}
		}
		return buffer;
	}

	@Override
	public void close(String reason) {
		if (!isClosed.get()) {
			closeSocket();
			isClosed.set(true);
			LOGGER.info("close connection,reason:" + reason + " " + this);
		}
	}

	public boolean isClosed() {
		return isClosed.get();
	}

	protected void idleCheck() {
		if (isIdleTimeout()) {
			LOGGER.info(toString() + " idle timeout");
			close(" idle ");
		} else {
			this.checkWriteOpts(true);
		}
	}

	/**
	 * 娓呯悊閬楃暀璧勬簮
	 */
	protected void cleanup() {

		// 鍥炴敹鎺ユ敹缂撳瓨
		if (readBuffer != null) {
			recycle(readBuffer);
			this.readBuffer = null;
		}

		// 鍥炴敹鍙戦�缂撳瓨
		if (writeQueue != null) {
			ByteBuffer buffer = null;
			while ((buffer = writeQueue.poll()) != null) {
				recycle(buffer);
			}
			writeQueue = null;
		}
	}

	protected int getPacketLength(ByteBuffer buffer, int offset) {
		if (buffer.position() < offset + packetHeaderSize) {
			return -1;
		} else {
			int length = buffer.get(offset) & 0xff;
			length |= (buffer.get(++offset) & 0xff) << 8;
			length |= (buffer.get(++offset) & 0xff) << 16;
			return length + packetHeaderSize;
		}
	}

	private ByteBuffer checkReadBuffer(ByteBuffer buffer, int offset,
			int position) {
		if (offset == 0) {
			if (buffer.capacity() >= maxPacketSize) {
				throw new IllegalArgumentException(
						"Packet size over the limit.");
			}
			int size = buffer.capacity() << 1;
			size = (size > maxPacketSize) ? maxPacketSize : size;
			ByteBuffer newBuffer = processor.getBufferPool().allocate(size);
			buffer.position(offset);
			newBuffer.put(buffer);
			readBuffer = newBuffer;
			recycle(buffer);
			return newBuffer;
		} else {
			buffer.position(offset);
			buffer.compact();
			readBufferOffset = 0;
			return buffer;
		}
	}

	/**
	 * if has more data to write ,return false else retun true
	 * 
	 * @return
	 * @throws IOException
	 */
	private boolean write0() throws IOException {
		int written = 0;
		ByteBuffer buffer = writeQueue.attachment();
		if (buffer != null) {
			while (buffer.hasRemaining()) {
				written = channel.write(buffer);
				if (written > 0) {
					netOutBytes += written;
					processor.addNetOutBytes(written);
					lastWriteTime = TimeUtil.currentTimeMillis();
				} else {
					break;
				}
			}

			if (buffer.hasRemaining()) {
				writeAttempts++;
				return false;
			} else {
				writeQueue.attach(null);
				recycle(buffer);
			}
		}
		while ((buffer = writeQueue.poll()) != null) {
			if (buffer.position() == 0) {
				recycle(buffer);
				this.close("quit send");
				return true;
			}
			buffer.flip();
			while (buffer.hasRemaining()) {
				written = channel.write(buffer);
				if (written > 0) {
					lastWriteTime = TimeUtil.currentTimeMillis();
					netOutBytes += written;
					processor.addNetOutBytes(written);
					lastWriteTime = TimeUtil.currentTimeMillis();
				} else {
					break;
				}
			}
			if (buffer.hasRemaining()) {
				writeQueue.attach(buffer);
				writeAttempts++;
				return false;
			} else {
				recycle(buffer);
			}
		}
		return true;
	}

	private void disableWrite() {
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() & OP_NOT_WRITE);
		} catch (Exception e) {
			LOGGER.warn("can't disable write " + e);
		}

	}

	public void checkWriteOpts(boolean wakeup) {
		if (this.writeQueue.snapshotSize() > 1
				&& (processKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
			//LOGGER.info("enable write "+this);
			enableWrite(wakeup);
		}
	}

	private void enableWrite(boolean wakeup) {
		boolean needWakeup = false;
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("can't enable write " + e);

		}
		if (needWakeup && wakeup) {
			processKey.selector().wakeup();
		}
	}

	private void clearSelectionKey() {
		try {
			SelectionKey key = this.processKey;
			if (key != null && key.isValid()) {
				key.attach(null);
				key.cancel();
			}
		} catch (Exception e) {
			LOGGER.warn("clear selector keys err:" + e);
		}
	}

	private void closeSocket() {
		clearSelectionKey();
		SocketChannel channel = this.channel;
		if (channel != null) {
			boolean isSocketClosed = true;
			Socket socket = channel.socket();
			if (socket != null) {
				try {
					socket.close();
				} catch (Throwable e) {
				}
				isSocketClosed = socket.isClosed();
			}
			try {
				channel.close();
			} catch (Throwable e) {
			}
			boolean closed = isSocketClosed && (!channel.isOpen());
			if (closed == false) {
				LOGGER.warn("close socket of connnection failed " + this);
			}

		}
	}

}