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
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

/**
 * @author mycat
 */
public final class BufferQueue {
	private static final Logger LOGGER = Logger.getLogger(BufferQueue.class);
	private ByteBuffer attachment;
	private final int total;
	private volatile int curSize = 0;
	private final ConcurrentLinkedQueue<ByteBuffer> items = new ConcurrentLinkedQueue<ByteBuffer>();

	public BufferQueue(int capacity) {
		this.total = capacity;
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
		return this.items.size();
	}

	/**
	 * 
	 * @param buffer
	 * @throws InterruptedException
	 */
	public void put(ByteBuffer buffer) throws InterruptedException {
		this.items.offer(buffer);
		curSize++;
		if (curSize > total) {
			LOGGER.warn("bufferQueue size exceeded ,maybe sql returned too many records ,cursize:"
					+ curSize);
		}
	}

	public ByteBuffer poll() {
		ByteBuffer buf = items.poll();
		if (buf != null && curSize > 0) {
			curSize--;
		}
		return buf;
	}

}