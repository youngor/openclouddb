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
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SelectionKey;
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
	protected final AsynchronousSocketChannel channel;
	protected NIOProcessor processor;
	protected NIOHandler handler;
	protected SelectionKey processKey;
	protected int packetHeaderSize;
	protected int maxPacketSize;
	private java.util.concurrent.locks.ReentrantLock writeLock = new ReentrantLock();
	protected volatile int readBufferOffset;
	private volatile ByteBuffer readBuffer;
	private volatile ByteBuffer writeBuffer;
	private volatile boolean writing;
	// private volatile boolean writing = false;
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

	private long idleTimeout;
	private static AIOReadHandler aioReadHandler = new AIOReadHandler();
	private static AIOWriteHandler aioWriteHandler = new AIOWriteHandler();

	public AbstractConnection(AsynchronousSocketChannel channel) {
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

	public AsynchronousSocketChannel getChannel() {
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
	public void register() throws IOException {

	}

	public void asynRead() {

		ByteBuffer theBuffer = readBuffer;
		if (theBuffer == null) {
			theBuffer = ByteBuffer.allocate(1024);
			this.readBuffer = theBuffer;
			channel.read(theBuffer, this, aioReadHandler);

		} else if (theBuffer.hasRemaining()) {
			// theBuffer.compact();
			channel.read(theBuffer, this, aioReadHandler);
		} else {
			throw new java.lang.IllegalArgumentException("full buffer to read ");
		}
	}

	public void onReadData(int got) throws IOException {
		if (isClosed.get()) {
			return;
		}
		ByteBuffer buffer = this.readBuffer;
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
		try {
			writeLock.lock();
			if (writing == false && writeQueue.isEmpty()) {
				writeBuffer = buffer;
				asynWrite(buffer);
			} else {
				writeQueue.put(buffer);
			}
		} catch (InterruptedException e) {
			error(ErrorCode.ERR_PUT_WRITE_QUEUE, e);
			return;
		} finally {
			writeLock.unlock();
		}

	}

	private void asynWrite(ByteBuffer buffer) {
		writing = true;
		buffer.flip();
		this.channel.write(buffer, this, aioWriteHandler);
	}

	public ByteBuffer checkWriteBuffer(ByteBuffer buffer, int capacity,
			boolean writeSocketIfFull) {
		if (capacity > buffer.remaining()) {
			if (writeSocketIfFull) {
				write(buffer);
				return allocate();
			} else {// Relocate a larger buffer
				buffer.flip();
				ByteBuffer newBuf = ByteBuffer.allocate(capacity);
				newBuf.put(buffer);
				this.recycle(buffer);
				return newBuf;
			}
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
			this.cleanup();
			LOGGER.info("close connection,reason:" + reason + " " + this);
		}
	}

	public boolean isClosed() {
		return isClosed.get();
	}

	public void idleCheck() {
		if (isIdleTimeout()) {
			LOGGER.info(toString() + " idle timeout");
			close(" idle ");
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
			this.readBufferOffset = 0;
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

	protected void onWriteFinished(int result) {
		if (isClosed.get()) {
			return;
		}
		netOutBytes += result;
		processor.addNetOutBytes(result);
		lastWriteTime = TimeUtil.currentTimeMillis();
		try {
			writeLock.lock();
			ByteBuffer theBuffer = writeBuffer;
			if (theBuffer.hasRemaining()) {
				theBuffer.compact();
				asynWrite(theBuffer);
			} else {// write finished
				this.recycle(theBuffer);
				writeBuffer = null;
				writing = false;
				theBuffer = writeQueue.poll();
				if (theBuffer != null) {
					this.writeBuffer = theBuffer;
					asynWrite(theBuffer);
				} else {
					writeBuffer = null;
				}

			}
		} finally {
			writeLock.unlock();
		}
	}

	private void closeSocket() {

		if (channel != null) {
			boolean isSocketClosed = true;
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

class AIOWriteHandler implements CompletionHandler<Integer, AbstractConnection> {

	@Override
	public void completed(Integer result, AbstractConnection con) {
		if (result >= 0) {
			con.onWriteFinished(result);
		} else {
			con.close("write erro " + result);
		}
	}

	@Override
	public void failed(Throwable exc, AbstractConnection con) {
		con.close("write failed " + exc);
	}

}

class AIOReadHandler implements CompletionHandler<Integer, AbstractConnection> {

	@Override
	public void completed(Integer i, AbstractConnection con) {
		if (i > 0) {
			try {
				con.onReadData(i);
				con.asynRead();
			} catch (IOException e) {
				con.close("handle err:" + e);
			}
		} else if (i == -1) {
			// System.out.println("read -1 xxxxxxxxx "+con);
			con.close("client closed");
		}

	}

	@Override
	public void failed(Throwable exc, AbstractConnection con) {
		con.close(exc.toString());

	}
}
