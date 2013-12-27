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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mycat
 */
public final class BufferQueue {

	private int takeIndex;
	private int putIndex;
	private int count;
	private final ByteBuffer[] items;
	private final ReentrantLock lock;
	private final Condition notFull;
	private ByteBuffer attachment;
	private final int total;
	public static final int NEARLY_FULL = -1;
	public static final int NEARLY_EMPTY = 1;

	public BufferQueue(int capacity) {
		this.total = capacity;
		items = new ByteBuffer[total];
		lock = new ReentrantLock();
		notFull = lock.newCondition();

	}

	public ByteBuffer attachment() {
		return attachment;
	}

	public void attach(ByteBuffer buffer) {
		this.attachment = buffer;
	}

	public int size() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return count;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * queue used 3/4
	 * 
	 * @return
	 */
	public int isNearlyFullOREmpty() {
		// System.out.println("queue size "+total+" cur "+count);
		if (count > (total - 2)) {
			return NEARLY_FULL;
		} else if (count < total * 1 / 3) {
			return NEARLY_EMPTY;
		}
		return 0;
	}

	/**
	 * 
	 * @param buffer
	 * @return if queue is nearlyful or is nearlyemp
	 * @throws InterruptedException
	 */
	public int put(ByteBuffer buffer) throws InterruptedException {
		final ByteBuffer[] items = this.items;
		final ReentrantLock lock = this.lock;
		lock.lockInterruptibly();
		try {
			try {
				while (count == items.length) {
					notFull.await();
				}
			} catch (InterruptedException ie) {
				notFull.signal();
				throw ie;
			}
			insert(buffer);
			return this.isNearlyFullOREmpty();
		} finally {
			lock.unlock();

		}
	}

	public ByteBuffer poll() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (count == 0) {
				return null;
			}
			return extract();
		} finally {
			lock.unlock();
		}
	}

	private void insert(ByteBuffer buffer) {
		items[putIndex] = buffer;
		putIndex = inc(putIndex);
		++count;
	}

	private ByteBuffer extract() {
		final ByteBuffer[] items = this.items;
		ByteBuffer buffer = items[takeIndex];
		items[takeIndex] = null;
		takeIndex = inc(takeIndex);
		--count;
		notFull.signal();
		return buffer;
	}

	private int inc(int i) {
		return (++i == items.length) ? 0 : i;
	}

}