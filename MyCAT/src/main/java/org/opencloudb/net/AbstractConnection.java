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

	public AbstractConnection(SocketChannel channel) {
		this.channel = channel;
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
	 * 鍒嗛厤缂撳瓨
	 */
	public ByteBuffer allocate() {
		ByteBuffer buffer = this.processor.getBufferPool().allocate();
		return buffer;
	}

	/**
	 * 鍥炴敹缂撳瓨
	 */
	public final void recycle(ByteBuffer buffer) {
		this.processor.getBufferPool().recycle(buffer);
	}

	/**
	 * 璇曞浘鍥炴敹缂撳瓨锛屽綋涓嶇‘瀹氭缂撳瓨鏄惁宸茬粡鍥炴敹杩囷紝鍒欒皟鐢ㄦ鏂规硶
	 * 鍐欓槦鍒楃殑鎯呭喌涓嬶紝褰撴斁鍏ュ埌鍐欓槦鍒楃殑BUFFER鍐欏叆socket浠ュ悗锛屼細鑷
	 * 姩鍥炴敹锛屽洜姝よ繖涓柟娉曚粎浠呯敤浜庝笉纭畾鏄惁宸茬粡琚嚜鍔ㄥ洖鏀朵簡鐨勬儏鍐典笅璋冪敤
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
			// fix:寮傚父鏃跺�涓嶅仠鍒锋棩蹇楃殑缂洪櫡
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
			if (length == -1) {// 鏈揪鍒板彲璁＄畻鏁版嵁鍖呴暱搴︾殑鏁版嵁
				if (!buffer.hasRemaining()) {
					buffer = checkReadBuffer(buffer, offset, position);
				}
				break;
			}
			if (position >= offset + length) {
				// 鎻愬彇涓�釜鏁版嵁鍖呯殑鏁版嵁杩涜澶勭悊
				buffer.position(offset);
				byte[] data = new byte[length];
				buffer.get(data, 0, length);
				handle(data);

				// 璁剧疆鍋忕Щ閲�
				offset += length;
				if (position == offset) {// 鏁版嵁姝ｅソ鍏ㄩ儴澶勭悊瀹屾瘯
					if (readBufferOffset != 0) {
						readBufferOffset = 0;
					}
					buffer.clear();
					break;
				} else {// 杩樻湁鍓╀綑鏁版嵁鏈鐞�
					readBufferOffset = offset;
					buffer.position(position);
					continue;
				}
			} else {// 鏈埌杈句竴涓暟鎹寘鐨勬暟鎹�
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
					enableWrite();
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
	public boolean writeByQueue() throws IOException {

		// System.out.println("writeByQueue ");
		if (isClosed.get()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("not write queue ,connection closed " + this);
			}
			return false;
		}

		// 婊¤冻浠ヤ笅涓や釜鏉′欢鏃讹紝鍒囨崲鍒板熀浜庝簨浠剁殑鍐欐搷浣溿�
		// 1.褰撳墠key瀵瑰啓浜嬩欢涓嶈鍏磋叮銆�
		// 2.write0()杩斿洖false銆�
		boolean noMoreData = write0();
		if (noMoreData) {
			disableWrite();
			return true;
		} else {
			if ((processKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
				enableWrite();
			}
			return false;
		}

	}

	/**
	 * 鎵撳紑璇讳簨浠�
	 */
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

	/**
	 * 妫�煡WriteBuffer瀹归噺锛屼笉澶熷垯鍐欏嚭褰撳墠缂撳瓨鍧楀苟鐢宠鏂扮殑缂撳瓨鍧椼�
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
	 * 鎶婃暟鎹啓鍒扮粰瀹氱殑缂撳瓨涓紝濡傛灉婊′簡鍒欐彁浜ゅ綋鍓嶇紦瀛樺苟鐢宠鏂扮殑缂撳瓨銆�
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
	 * 鐢盤rocessor璋冪敤鐨勭┖闂叉鏌�
	 */
	protected abstract void idleCheck();

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

	/**
	 * 鑾峰彇鏁版嵁鍖呴暱搴︼紝榛樿鏄疢ySQL鏁版嵁鍖咃紝鍏朵粬鏁版嵁鍖呴噸杞芥鏂规硶銆�
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
	 * 妫�煡ReadBuffer瀹归噺锛屼笉澶熷垯鎵╁睍褰撳墠缂撳瓨锛岀洿鍒版渶澶у�銆�
	 */
	private ByteBuffer checkReadBuffer(ByteBuffer buffer, int offset,
			int position) {
		// 褰撳亸绉婚噺涓�鏃堕渶瑕佹墿瀹癸紝鍚﹀垯绉诲姩鏁版嵁鑷冲亸绉婚噺涓�鐨勪綅缃�
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
			// 鍥炴敹鎵╁鍓嶇殑缂撳瓨鍧�
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
		// 妫�煡鏄惁鏈夐仐鐣欐暟鎹湭鍐欏嚭
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
		// 鍐欏嚭鍙戦�闃熷垪涓殑鏁版嵁鍧�
		while ((buffer = writeQueue.poll()) != null) {
			// 濡傛灉鏄竴鍧楁湭浣跨敤杩囩殑buffer锛屽垯鎵ц鍏抽棴杩炴帴銆�
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
	 * 鍏抽棴鍐欎簨浠�
	 */
	private void disableWrite() {
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() & OP_NOT_WRITE);
		} catch (Exception e) {
			LOGGER.warn("can't disable write " + e);
		}

	}

	/**
	 * 鎵撳紑鍐欎簨浠�
	 */
	private void enableWrite() {
		boolean needWakeup = false;
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("can't enable write " + e);

		}
		if (needWakeup) {
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