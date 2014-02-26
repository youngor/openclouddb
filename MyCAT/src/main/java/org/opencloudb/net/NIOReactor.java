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

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.opencloudb.config.ErrorCode;

/**
 * 网络事件反应器
 * 
 * @author mycat
 */
public final class NIOReactor {
	private static final Logger LOGGER = Logger.getLogger(NIOReactor.class);
	private final String name;
	private final RW reactorR;

	public NIOReactor(String name) throws IOException {
		this.name = name;
		this.reactorR = new RW();
	}

	final void startup() {
		new Thread(reactorR, name + "-RW").start();
	}

	final void postRegister(NIOConnection c) {
		reactorR.registerQueue.offer(c);
		reactorR.selector.wakeup();
	}

	final BlockingQueue<NIOConnection> getRegisterQueue() {
		return reactorR.registerQueue;
	}

	final long getReactCount() {
		return reactorR.reactCount;
	}

	private final class RW implements Runnable {
		private final Selector selector;
		private final BlockingQueue<NIOConnection> registerQueue;
		private long reactCount;

		private RW() throws IOException {
			this.selector = Selector.open();
			this.registerQueue = new LinkedBlockingQueue<NIOConnection>();
		}

		@Override
		public void run() {
			final Selector selector = this.selector;
			Set<SelectionKey> keys = null;
			for (;;) {

				++reactCount;
				try {
					selector.select(500L);
					register(selector);
					keys = selector.selectedKeys();
					for (SelectionKey key : keys) {
						try {
							Object att = key.attachment();
							if (att != null && key.isValid()) {
								AbstractConnection con = (AbstractConnection) att;
								int readyOps = key.readyOps();
								if ((readyOps & SelectionKey.OP_READ) != 0) {
									// System.out.println("xxx read " + att);
									read(con);
								}
								if ((readyOps & SelectionKey.OP_WRITE) != 0) {
									con.writeByQueue();
								}
							} else {
								// LOGGER.warn("key not valid ,cancel key ");
								key.cancel();
							}
						} catch (Throwable e) {
							LOGGER.warn(name, e);
						}

					}
				} catch (Throwable e) {
					LOGGER.warn(name, e);
				} finally {
					if (keys != null) {
						keys.clear();
					}

				}
				for (SelectionKey key : selector.keys()) {
					Object att = key.attachment();
					if (att != null) {
						AbstractConnection con = (AbstractConnection) att;
						// LOGGER.info("enable write "+this);
						con.checkWriteOpts(false);
					}
				}
			}
		}

		private void register(Selector selector) {
			NIOConnection c = null;
			while ((c = registerQueue.poll()) != null) {
				try {
					c.register(selector);
				} catch (Throwable e) {
					c.error(ErrorCode.ERR_REGISTER, e);
				}
			}
		}

		private void read(NIOConnection c) {

			try {
				if (!c.isClosed()) {
					c.read();
				}
			} catch (EOFException e) {
				if (!c.isClosed()) {
					c.close("exception:" + e.toString());
					c.error(ErrorCode.ERR_READ, e);
				}

			} catch (Throwable e) {
				c.close("exception:" + e.toString());
				c.error(ErrorCode.ERR_READ, e);

			}
		}

	}

}