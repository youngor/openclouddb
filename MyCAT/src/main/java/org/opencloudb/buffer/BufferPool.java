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
package org.opencloudb.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

/**
 * @author mycat
 */
public final class BufferPool {
	private static final Logger LOGGER = Logger.getLogger(BufferPool.class);
	private final int chunkSize;
	private final ByteBuffer[] items;
	private final ReentrantLock lock;
	private int putIndex;
	private int takeIndex;
	private int count;
	private volatile int newCount;

	public BufferPool(int bufferSize, int chunkSize) {
		this.chunkSize = chunkSize;
		int capacity = bufferSize / chunkSize;
		capacity = (bufferSize % chunkSize == 0) ? capacity : capacity + 1;
		this.items = new ByteBuffer[capacity];
		this.lock = new ReentrantLock();
		for (int i = 0; i < capacity; i++) {
			insert(createDirectBuffer(chunkSize));
		}
	}

	public int capacity() {
		return items.length;
	}

	public int size() {
		return count;
	}

	public int getNewCount() {
		return newCount;
	}

	public ByteBuffer allocate() {
		ByteBuffer node = null;
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			node = (count == 0) ? null : extract();
		} finally {
			lock.unlock();
		}
		if (node == null) {
			++newCount;
			LOGGER.warn("pool is full ,allocate tempory buffer ,total alloccated times:"
					+ newCount);
			return createTempBuffer(chunkSize);
		} else {
			return node;
		}
	}

	/**
	 * check if buffer already recycled ,only used when not sure if a buffer
	 * already recycled
	 * 
	 * @param buffer
	 */
	public void safeRecycle(ByteBuffer buffer) {
		checkValidBuffer(buffer);
		final boolean debug = LOGGER.isDebugEnabled();
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (testIfDuplicate(buffer)) {
				if (debug) {
					LOGGER.debug("already recycled buffer ");
				}
				return;
			}
			recycleBuffer(buffer);
		} finally {
			lock.unlock();
		}
	}

	private void checkValidBuffer(ByteBuffer buffer) {
		// 拒绝回收null和容量大于chunkSize的缓存
		if (buffer == null || !buffer.isDirect()) {
			return;
		} else if (buffer.capacity() > chunkSize) {
			buffer.clear();
			LOGGER.warn("cant' recycle  a buffer large than my pool chunksize "
					+ buffer.capacity());
			return;
		}
	}

	public void recycle(ByteBuffer buffer) {
		checkValidBuffer(buffer);
		recycleBuffer(buffer);
	}
	private void recycleBuffer(ByteBuffer buffer) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			// reportDuplicate(buffer);
			if (count != items.length) {
				buffer.clear();
				insert(buffer);

			} else {
				LOGGER.warn("can't recycle  buffer ,pool is full ");

			}
		} finally {
			lock.unlock();
		}
	}

	private void insert(ByteBuffer buffer) {
		items[putIndex] = buffer;

		putIndex = inc(putIndex);
		++count;

	}

	public boolean testIfDuplicate(ByteBuffer buffer) {
		for (ByteBuffer exists : items) {
			if (exists == buffer) {
				return true;
			}
		}
		return false;

	}

	private ByteBuffer extract() {
		final ByteBuffer[] items = this.items;
		ByteBuffer item = items[takeIndex];
		items[takeIndex] = null;
		takeIndex = inc(takeIndex);
		--count;
		return item;
	}

	private int inc(int i) {
		return (++i == items.length) ? 0 : i;
	}

	private ByteBuffer createTempBuffer(int size) {
		return ByteBuffer.allocate(size);
	}

	private ByteBuffer createDirectBuffer(int size) {
		// for performance
		return ByteBuffer.allocateDirect(size);
	}

	public ByteBuffer allocate(int size) {
		if (size <= this.chunkSize) {
			return allocate();
		} else {
			LOGGER.warn("allocate buffer size large than default chunksize:"
					+ this.chunkSize + " he want " + size);
			return createTempBuffer(size);
		}
	}

	public static void main(String[] args) {
		BufferPool pool = new BufferPool(1024 * 5, 1024);
		int i = pool.capacity();
		ArrayList<ByteBuffer> all = new ArrayList<ByteBuffer>();
		for (int j = 0; j <= i; j++) {
			all.add(pool.allocate());
		}
		for (ByteBuffer buf : all) {
			pool.recycle(buf);
		}
		System.out.println(pool.size());
	}
}