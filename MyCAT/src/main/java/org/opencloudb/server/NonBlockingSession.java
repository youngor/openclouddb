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
package org.opencloudb.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.ConnectionMeta;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.backend.PhysicalDBNode;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.mpp.DataMergeService;
import org.opencloudb.mysql.nio.handler.CommitNodeHandler;
import org.opencloudb.mysql.nio.handler.KillConnectionHandler;
import org.opencloudb.mysql.nio.handler.MultiNodeQueryHandler;
import org.opencloudb.mysql.nio.handler.RollbackNodeHandler;
import org.opencloudb.mysql.nio.handler.RollbackReleaseHandler;
import org.opencloudb.mysql.nio.handler.SingleNodeHandler;
import org.opencloudb.mysql.nio.handler.Terminatable;
import org.opencloudb.net.FrontendConnection;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.route.RouteResultset;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.parser.ServerParse;

/**
 * @author mycat
 * @author mycat
 */
public class NonBlockingSession implements Session {
	private static final Logger LOGGER = Logger
			.getLogger(NonBlockingSession.class);

	private final ServerConnection source;
	private final ConcurrentHashMap<RouteResultsetNode, PhysicalConnection> target;
	private final AtomicBoolean terminating;

	// life-cycle: each sql execution
	private volatile SingleNodeHandler singleNodeHandler;
	private volatile MultiNodeQueryHandler multiNodeHandler;
	private volatile CommitNodeHandler commitHandler;
	private volatile RollbackNodeHandler rollbackHandler;
	private boolean openWRFluxContrl = false;
	public NonBlockingSession(ServerConnection source, int openWRFluxContrl) {
		this.source = source;
		this.target = new ConcurrentHashMap<RouteResultsetNode, PhysicalConnection>(
				2, 1);
		this.terminating = new AtomicBoolean(false);
		this.openWRFluxContrl = (openWRFluxContrl == 1);
	}

	/**
	 * temporary supress channel read event ,because front connection is blocked
	 */
	public void supressTargetChannelReadEvent() {
		if (!openWRFluxContrl) {
			return;
		}
		final boolean isDebug = LOGGER.isDebugEnabled();
		for (PhysicalConnection con : target.values()) {
			if (!con.isSuppressReadTemporay()) {
				if (isDebug) {
					LOGGER.debug("supress backend connection read event ,for front con blocked write "
							+ source + " backcon:" + con);
				}
				con.setSuppressReadTemporay(true);
				((org.opencloudb.net.AbstractConnection) con).disableRead();
			}
		}
	}



	/**
	 * temporary upsupress channel read event ,because front connection is
	 * blocked
	 */
	public void unSupressTargetChannelReadEvent() {
		if (!openWRFluxContrl) {
			return;
		}
		final boolean isDebug = LOGGER.isDebugEnabled();

		for (PhysicalConnection con : target.values()) {
			if (con.isSuppressReadTemporay()) {
				if (isDebug) {
					LOGGER.debug("upsupress backend connection read event ,for front con can write more "
							+ source + " backcon:" + con);
				}
				con.setSuppressReadTemporay(false);
				((org.opencloudb.net.AbstractConnection) con).enableRead();
			}

		}
	}

	@Override
	public ServerConnection getSource() {
		return source;
	}

	@Override
	public int getTargetCount() {
		return target.size();
	}

	public Set<RouteResultsetNode> getTargetKeys() {
		return target.keySet();
	}

	public PhysicalConnection getTarget(RouteResultsetNode key) {
		return target.get(key);
	}

	public PhysicalConnection removeTarget(RouteResultsetNode key) {
		return target.remove(key);
	}

	@Override
	public void execute(RouteResultset rrs, int type) {
		if (LOGGER.isDebugEnabled()) {
			StringBuilder s = new StringBuilder();
			LOGGER.debug(s.append(source).append(rrs).toString() + " rrs ");
		}

		// 检查路由结果是否为空
		RouteResultsetNode[] nodes = rrs.getNodes();
		if (nodes == null || nodes.length == 0 || nodes[0].getName() == null
				|| nodes[0].getName().equals("")) {
			source.writeErrMessage(ErrorCode.ER_NO_DB_ERROR,
					"No dataNode found ,please check tables defined in schema:"
							+ source.getSchema());
			return;
		}

		if (nodes.length == 1) {
			singleNodeHandler = new SingleNodeHandler(nodes[0], this);
			try {
				singleNodeHandler.execute();
			} catch (Exception e) {
				LOGGER.warn(new StringBuilder().append(source).append(rrs), e);
				source.writeErrMessage(ErrorCode.ERR_HANDLE_DATA, e.toString());
			}
		} else {
			boolean autocommit = source.isAutocommit();
			DataMergeService dataMergeSvr = null;
			if (ServerParse.SELECT == type && rrs.needMerge()) {
				dataMergeSvr = new DataMergeService(rrs);
			}
			multiNodeHandler = new MultiNodeQueryHandler(rrs, autocommit, this,
					dataMergeSvr);
			try {
				multiNodeHandler.execute();
			} catch (Exception e) {
				LOGGER.warn(new StringBuilder().append(source).append(rrs), e);
				source.writeErrMessage(ErrorCode.ERR_HANDLE_DATA, e.toString());
			}
		}
	}

	public void commit() {
		final int initCount = target.size();
		if (initCount <= 0) {
			ByteBuffer buffer = source.allocate();
			buffer = source.writeToBuffer(OkPacket.OK, buffer);
			source.write(buffer);
			return;
		}
		commitHandler = new CommitNodeHandler(this);
		commitHandler.commit();
	}

	public void rollback() {
		final int initCount = target.size();
		if (initCount <= 0) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no session bound connections found ,no need send rollback cmd ");
			}
			ByteBuffer buffer = source.allocate();
			buffer = source.writeToBuffer(OkPacket.OK, buffer);
			source.write(buffer);
			return;
		}
		rollbackHandler = new RollbackNodeHandler(this);
		rollbackHandler.rollback();
	}

	@Override
	public void cancel(FrontendConnection sponsor) {

	}

	/**
	 * {@link ServerConnection#isClosed()} must be true before invoking this
	 */
	public void terminate() {
		if (!terminating.compareAndSet(false, true)) {
			return;
		}
		kill(new Runnable() {
			@Override
			public void run() {
				new Terminator().nextInvocation(singleNodeHandler)
						.nextInvocation(multiNodeHandler)
						.nextInvocation(commitHandler)
						.nextInvocation(rollbackHandler)
						.nextInvocation(new Terminatable() {
							@Override
							public void terminate(Runnable runnable) {
								clearConnections(false);
							}
						}).nextInvocation(new Terminatable() {
							@Override
							public void terminate(Runnable runnable) {
								terminating.set(false);
							}
						}).invoke();
			}
		});
	}

	public void releaseConnectionIfSafe(PhysicalConnection conn, boolean debug) {
		RouteResultsetNode node = (RouteResultsetNode) conn.getAttachment();

		if (node != null) {
			if (this.source.isAutocommit() || conn.isFromSlaveDB()
					|| !conn.isModifiedSQLExecuted()) {
				releaseConnection((RouteResultsetNode) conn.getAttachment(),
						LOGGER.isDebugEnabled());
			}
		}
	}

	public void releaseConnection(RouteResultsetNode rrn, boolean debug) {

		PhysicalConnection c = target.remove(rrn);
		if (c != null) {
			if (debug) {
				LOGGER.debug("relase connection " + c);
			}
			if (c.getAttachment() != null) {
				c.setAttachment(null);
			}
			if (c.isRunning()) {
				LOGGER.warn("close running connection is found " + c);
				c.close("abnomal");
			} else if (!c.isClosedOrQuit()) {
				if (c.isAutocommit()) {
					c.release();
				} else {
					c.setResponseHandler(new RollbackReleaseHandler());
					c.rollback();
				}
			}
		}
	}

	public void releaseConnections() {
		boolean debug = LOGGER.isDebugEnabled();
		for (RouteResultsetNode rrn : target.keySet()) {
			releaseConnection(rrn, debug);
		}
	}

	/**
	 * @return previous bound connection
	 */
	public PhysicalConnection bindConnection(RouteResultsetNode key,
			PhysicalConnection conn) {
		// System.out.println("bind connection "+conn+
		// " to key "+key.getName()+" on sesion "+this);
		return target.put(key, conn);
	}

	private static class Terminator {
		private LinkedList<Terminatable> list = new LinkedList<Terminatable>();
		private Iterator<Terminatable> iter;

		public Terminator nextInvocation(Terminatable term) {
			list.add(term);
			return this;
		}

		public void invoke() {
			iter = list.iterator();
			terminate();
		}

		private void terminate() {
			if (iter.hasNext()) {
				Terminatable term = iter.next();
				if (term != null) {
					term.terminate(new Runnable() {
						@Override
						public void run() {
							terminate();
						}
					});
				} else {
					terminate();
				}
			}
		}
	}

	public boolean tryExistsCon(final PhysicalConnection conn,
			RouteResultsetNode node, Runnable runable) {

		if (conn == null) {
			return false;
		}
		if (!conn.isFromSlaveDB()
				|| node.canRunnINReadDB(getSource().isAutocommit())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("found connections in session to use " + conn
						+ " for " + node);
			}
			conn.setAttachment(node);
			getSource().getProcessor().getExecutor().execute(runable);
			return true;
		} else {
			// slavedb connection and can't use anymore ,release it
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("release slave connection,can't be used in trasaction  "
						+ conn + " for " + node);
			}
			releaseConnection(node, LOGGER.isDebugEnabled());
		}
		return false;
	}

	private void kill(Runnable run) {
		boolean hooked = false;
		AtomicInteger count = null;
		Map<RouteResultsetNode, PhysicalConnection> killees = null;
		for (RouteResultsetNode node : target.keySet()) {
			PhysicalConnection c = target.get(node);
			if (c != null && c.isRunning()) {
				if (!hooked) {
					hooked = true;
					killees = new HashMap<RouteResultsetNode, PhysicalConnection>();
					count = new AtomicInteger(0);
				}
				killees.put(node, c);
				count.incrementAndGet();
			}
		}
		if (hooked) {
			ConnectionMeta conMeta = new ConnectionMeta(null, null, -1, true);
			for (Entry<RouteResultsetNode, PhysicalConnection> en : killees
					.entrySet()) {
				KillConnectionHandler kill = new KillConnectionHandler(
						en.getValue(), this, run, count);
				MycatConfig conf = MycatServer.getInstance().getConfig();
				PhysicalDBNode dn = conf.getDataNodes().get(
						en.getKey().getName());
				try {
					dn.getConnectionFromSameSource(conMeta, en.getValue(),
							kill, en.getKey());
				} catch (Exception e) {
					LOGGER.error(
							"get killer connection failed for " + en.getKey(),
							e);
					kill.connectionError(e, null);
				}
			}
		} else {
			run.run();
		}
	}

	private void clearConnections(boolean pessimisticRelease) {
		for (RouteResultsetNode node : target.keySet()) {
			PhysicalConnection c = target.remove(node);

			if (c == null || c.isClosedOrQuit()) {
				continue;
			}

			// 如果通道正在运行中，则关闭当前通道。
			if (c.isRunning() || (pessimisticRelease && source.isClosed())) {
				c.close("source closed");
				continue;
			}

			// 非事务中的通道，直接释放资源。
			if (c.isAutocommit()) {
				c.release();
				continue;
			}

			c.setResponseHandler(new RollbackReleaseHandler());
			c.rollback();
		}
	}

	public void clearResources() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("clear session resources " + this);
		}
		this.releaseConnections();
		if (this.singleNodeHandler != null) {
			singleNodeHandler.clearResources();
			singleNodeHandler=null;
		}
		if (this.multiNodeHandler != null) {
			multiNodeHandler.clearResources();
			multiNodeHandler=null;
		}
	}

	public boolean closed() {
		return source.isClosed();
	}

}