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

	/**
	 * used for statics
	 * 
	 * @return
	 */
	public int snapshotSize() {
		return count;
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