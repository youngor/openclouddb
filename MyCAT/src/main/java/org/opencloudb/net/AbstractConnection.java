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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
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
	protected final ReentrantLock keyLock;
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

	public AbstractConnection(SocketChannel channel) {
		this.channel = channel;
		this.keyLock = new ReentrantLock();
		this.isClosed = new AtomicBoolean(false);
		this.startupTime = TimeUtil.currentTimeMillis();
		this.lastReadTime = startupTime;
		this.lastWriteTime = startupTime;
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

	/**
	 * 分配缓存
	 */
	public ByteBuffer allocate() {
		ByteBuffer buffer = this.processor.getBufferPool().allocate();
		return buffer;
	}

	/**
	 * 回收缓存
	 */
	public final void recycle(ByteBuffer buffer) {
		this.processor.getBufferPool().recycle(buffer);
	}

	/**
	 * 试图回收缓存，当不确定此缓存是否已经回收过，则调用此方法
	 * 写队列的情况下，当放入到写队列的BUFFER写入socket以后，会自动回收，因此这个方法仅仅用于不确定是否已经被自动回收了的情况下调用
	 * 
	 * @param buffer
	 */
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
			// fix:异常时候不停刷日志的缺陷
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

		// 处理数据
		int offset = readBufferOffset, length = 0, position = buffer.position();
		for (;;) {
			length = getPacketLength(buffer, offset);
			if (length == -1) {// 未达到可计算数据包长度的数据
				if (!buffer.hasRemaining()) {
					buffer = checkReadBuffer(buffer, offset, position);
				}
				break;
			}
			if (position >= offset + length) {
				// 提取一个数据包的数据进行处理
				buffer.position(offset);
				byte[] data = new byte[length];
				buffer.get(data, 0, length);
				handle(data);

				// 设置偏移量
				offset += length;
				if (position == offset) {// 数据正好全部处理完毕
					if (readBufferOffset != 0) {
						readBufferOffset = 0;
					}
					buffer.clear();
					break;
				} else {// 还有剩余数据未处理
					readBufferOffset = offset;
					buffer.position(position);
					continue;
				}
			} else {// 未到达一个数据包的数据
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
			if ((processKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
				enableWrite();
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
		// 满足以下两个条件时，切换到基于事件的写操作。
		// 1.当前key对写事件不该兴趣。
		// 2.write0()返回false。
		boolean hasMoreWrite = !write0();
		if (hasMoreWrite) {
			if ((processKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
				enableWrite();
			}
		} else {// no more write
			// System.out.println("disable write "+this);
			if ((processKey.interestOps() & SelectionKey.OP_WRITE) != 0) {
				disableWrite();
			}
		}

	}

	/**
	 * 打开读事件
	 */
	public void enableRead() {
		final Lock lock = this.keyLock;
		lock.lock();
		/**
		 * 增加needWakeup参数判断，解决因负载均衡haproxy心跳检测带来的异常(CancelledKeyException)错误
		 */
		boolean needWakeup = false;
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("enable read fail " + e);
		} finally {
			lock.unlock();
		}
		if (needWakeup) {
			processKey.selector().wakeup();
		}
	}

	/**
	 * 关闭读事件
	 */
	public void disableRead() {
		final Lock lock = this.keyLock;
		lock.lock();
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() & OP_NOT_READ);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 检查WriteBuffer容量，不够则写出当前缓存块并申请新的缓存块。
	 */
	public ByteBuffer checkWriteBuffer(ByteBuffer buffer, int capacity) {
		if (capacity > buffer.remaining()) {
			write(buffer);
			return allocate();
		} else {
			return buffer;
		}
	}

	/**
	 * 把数据写到给定的缓存中，如果满了则提交当前缓存并申请新的缓存。
	 */
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

	/**
	 * 由Processor调用的空闲检查
	 */
	protected abstract void idleCheck();

	/**
	 * 清理遗留资源
	 */
	protected void cleanup() {

		// 回收接收缓存
		if (readBuffer != null) {
			recycle(readBuffer);
			this.readBuffer = null;
		}

		// 回收发送缓存
		if (writeQueue != null) {
			ByteBuffer buffer = null;
			while ((buffer = writeQueue.poll()) != null) {
				recycle(buffer);
			}
			writeQueue = null;
		}
	}

	/**
	 * 获取数据包长度，默认是MySQL数据包，其他数据包重载此方法。
	 */
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

	/**
	 * 检查ReadBuffer容量，不够则扩展当前缓存，直到最大值。
	 */
	private ByteBuffer checkReadBuffer(ByteBuffer buffer, int offset,
			int position) {
		// 当偏移量为0时需要扩容，否则移动数据至偏移量为0的位置。
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
			// 回收扩容前的缓存块
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
		// 检查是否有遗留数据未写出
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
		// 写出发送队列中的数据块
		while ((buffer = writeQueue.poll()) != null) {
			// 如果是一块未使用过的buffer，则执行关闭连接。
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

	/**
	 * 关闭写事件
	 */
	private void disableWrite() {
		final Lock lock = this.keyLock;
		lock.lock();
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() & OP_NOT_WRITE);
		} catch (Exception e) {
			LOGGER.warn("can't disable write " + e);

		} finally {
			lock.unlock();
		}
	}

	/**
	 * 打开写事件
	 */
	private void enableWrite() {
		boolean needWakeup = false;
		final Lock lock = this.keyLock;
		lock.lock();
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("can't enable write " + e);

		} finally {
			lock.unlock();
		}
		if (needWakeup) {
			processKey.selector().wakeup();
		}
	}

	private void clearSelectionKey() {
		final Lock lock = this.keyLock;
		lock.lock();
		try {
			SelectionKey key = this.processKey;
			if (key != null && key.isValid()) {
				key.attach(null);
				key.cancel();
			}
		} catch (Exception e) {
			LOGGER.warn("clear selector keys err:" + e);
		} finally {
			lock.unlock();
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