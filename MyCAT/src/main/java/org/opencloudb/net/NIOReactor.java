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
					selector.select(1000L);
					register(selector);
					keys = selector.selectedKeys();

					for (SelectionKey key : keys) {
						try {
							Object att = key.attachment();
							if (att != null && key.isValid()) {
								AbstractConnection con = (AbstractConnection) att;
								int readyOps = key.readyOps();
								if ((readyOps & SelectionKey.OP_READ) != 0) {
									//System.out.println("xxx read " + att);
									read(con);
								}
								if ((readyOps & SelectionKey.OP_WRITE) != 0) {
									//System.out.println("xxx write " + att);
									con.writeByQueue();
								}
							} else {
								//LOGGER.warn("key not valid ,cancel key ");
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