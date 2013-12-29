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
package org.opencloudb.mysql.nio;

import java.io.UnsupportedEncodingException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.config.Capabilities;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.config.Isolations;
import org.opencloudb.exception.UnknownTxIsolationException;
import org.opencloudb.mysql.CharsetUtil;
import org.opencloudb.mysql.SecurityUtil;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.net.BackendConnection;
import org.opencloudb.net.mysql.AuthPacket;
import org.opencloudb.net.mysql.CommandPacket;
import org.opencloudb.net.mysql.HandshakePacket;
import org.opencloudb.net.mysql.MySQLPacket;
import org.opencloudb.net.mysql.QuitPacket;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.ServerConnection;
import org.opencloudb.util.TimeUtil;

/**
 * @author mycat
 */
public class MySQLConnection extends BackendConnection implements
		PhysicalConnection {
	private static final Logger LOGGER = Logger
			.getLogger(MySQLConnection.class);
	private static final long CLIENT_FLAGS = initClientFlags();
	private final AtomicBoolean isRunning = new AtomicBoolean();
	private volatile long lastTime; // QS_TODO
	private volatile String schema = "";
	private volatile String oldSchema;
	private volatile boolean borrowed = false;
	private volatile boolean modifiedSQLExecuted = false;

	private static long initClientFlags() {
		int flag = 0;
		flag |= Capabilities.CLIENT_LONG_PASSWORD;
		flag |= Capabilities.CLIENT_FOUND_ROWS;
		flag |= Capabilities.CLIENT_LONG_FLAG;
		flag |= Capabilities.CLIENT_CONNECT_WITH_DB;
		// flag |= Capabilities.CLIENT_NO_SCHEMA;
		// flag |= Capabilities.CLIENT_COMPRESS;
		flag |= Capabilities.CLIENT_ODBC;
		// flag |= Capabilities.CLIENT_LOCAL_FILES;
		flag |= Capabilities.CLIENT_IGNORE_SPACE;
		flag |= Capabilities.CLIENT_PROTOCOL_41;
		flag |= Capabilities.CLIENT_INTERACTIVE;
		// flag |= Capabilities.CLIENT_SSL;
		flag |= Capabilities.CLIENT_IGNORE_SIGPIPE;
		flag |= Capabilities.CLIENT_TRANSACTIONS;
		// flag |= Capabilities.CLIENT_RESERVED;
		flag |= Capabilities.CLIENT_SECURE_CONNECTION;
		// client extension
		// flag |= Capabilities.CLIENT_MULTI_STATEMENTS;
		// flag |= Capabilities.CLIENT_MULTI_RESULTS;
		return flag;
	}

	private static final CommandPacket _READ_UNCOMMITTED = new CommandPacket();
	private static final CommandPacket _READ_COMMITTED = new CommandPacket();
	private static final CommandPacket _REPEATED_READ = new CommandPacket();
	private static final CommandPacket _SERIALIZABLE = new CommandPacket();
	private static final CommandPacket _AUTOCOMMIT_ON = new CommandPacket();
	private static final CommandPacket _AUTOCOMMIT_OFF = new CommandPacket();
	private static final CommandPacket _COMMIT = new CommandPacket();
	private static final CommandPacket _ROLLBACK = new CommandPacket();
	static {
		_READ_UNCOMMITTED.packetId = 0;
		_READ_UNCOMMITTED.command = MySQLPacket.COM_QUERY;
		_READ_UNCOMMITTED.arg = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED"
				.getBytes();
		_READ_COMMITTED.packetId = 0;
		_READ_COMMITTED.command = MySQLPacket.COM_QUERY;
		_READ_COMMITTED.arg = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED"
				.getBytes();
		_REPEATED_READ.packetId = 0;
		_REPEATED_READ.command = MySQLPacket.COM_QUERY;
		_REPEATED_READ.arg = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ"
				.getBytes();
		_SERIALIZABLE.packetId = 0;
		_SERIALIZABLE.command = MySQLPacket.COM_QUERY;
		_SERIALIZABLE.arg = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE"
				.getBytes();
		_AUTOCOMMIT_ON.packetId = 0;
		_AUTOCOMMIT_ON.command = MySQLPacket.COM_QUERY;
		_AUTOCOMMIT_ON.arg = "SET autocommit=1".getBytes();
		_AUTOCOMMIT_OFF.packetId = 0;
		_AUTOCOMMIT_OFF.command = MySQLPacket.COM_QUERY;
		_AUTOCOMMIT_OFF.arg = "SET autocommit=0".getBytes();
		_COMMIT.packetId = 0;
		_COMMIT.command = MySQLPacket.COM_QUERY;
		_COMMIT.arg = "commit".getBytes();
		_ROLLBACK.packetId = 0;
		_ROLLBACK.command = MySQLPacket.COM_QUERY;
		_ROLLBACK.arg = "rollback".getBytes();
	}

	private MySQLDataSource pool;
	private boolean fromSlaveDB;
	private long threadId;
	private HandshakePacket handshake;
	private int charsetIndex;
	private String charset;
	private volatile int txIsolation;
	private volatile boolean autocommit;
	private long clientFlags;
	private boolean isAuthenticated;
	private String user;
	private String password;
	private Object attachment;
	private ResponseHandler respHandler;

	private final AtomicBoolean isQuit;
	private volatile StatusSync statusSync;

	public MySQLConnection(SocketChannel channel, boolean fromSlaveDB) {
		super(channel);
		this.clientFlags = CLIENT_FLAGS;
		this.lastTime = TimeUtil.currentTimeMillis();
		this.isQuit = new AtomicBoolean(false);
		this.autocommit = true;
		this.fromSlaveDB = fromSlaveDB;
	}

	public String getSchema() {
		return this.schema;
	}

	public void setSchema(String newSchema) {
		this.oldSchema = schema;
		this.schema = newSchema;
	}

	public MySQLDataSource getPool() {
		return pool;
	}

	public void setPool(MySQLDataSource pool) {
		this.pool = pool;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public HandshakePacket getHandshake() {
		return handshake;
	}

	public void setHandshake(HandshakePacket handshake) {
		this.handshake = handshake;
	}

	public void setCharsetIndex(int charsetIndex) {
		this.charsetIndex = charsetIndex;
	}

	public long getThreadId() {
		return threadId;
	}

	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public String getPassword() {
		return password;
	}

	public void authenticate() {
		AuthPacket packet = new AuthPacket();
		packet.packetId = 1;
		packet.clientFlags = clientFlags;
		packet.maxPacketSize = maxPacketSize;
		packet.charsetIndex = charsetIndex;
		packet.user = user;
		try {
			packet.password = passwd(password, handshake);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		packet.database = schema;
		packet.write(this);
	}

	public void setRunning(boolean running) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("set running " + running + " for " + this);
		}
		isRunning.set(running);
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public boolean isAutocommit() {
		return autocommit;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public boolean isClosedOrQuit() {
		return isClosed() || isQuit.get();
	}

	protected void sendQueryCmd(String query)
			throws UnsupportedEncodingException {
		CommandPacket packet = new CommandPacket();
		packet.packetId = 0;
		packet.command = MySQLPacket.COM_QUERY;
		packet.arg = query.getBytes(charset);
		lastTime = TimeUtil.currentTimeMillis();
		packet.write((BackendConnection) this);
	}

	private static class StatusSync {
		private final RouteResultsetNode rrn;
		private final MySQLConnection conn;
		private CommandPacket schemaCmd;
		private CommandPacket charCmd;
		private CommandPacket isoCmd;
		private CommandPacket acCmd;
		private final String schema;
		private final int charIndex;
		private final int txIsolation;
		private final boolean autocommit;
		private volatile boolean executed;

		public StatusSync(MySQLConnection conn, RouteResultsetNode rrn,
				ServerConnection sc, boolean autocommit) {
			this.conn = conn;
			this.rrn = rrn;
			this.charIndex = sc.getCharsetIndex();
			this.schema = conn.schema;
			this.schemaCmd = !schema.equals(conn.oldSchema) ? getChangeSchemaCommand(schema)
					: null;
			this.charCmd = conn.charsetIndex != charIndex ? getCharsetCommand(charIndex)
					: null;
			this.txIsolation = sc.getTxIsolation();

			this.isoCmd = conn.txIsolation != txIsolation ? getTxIsolationCommand(txIsolation)
					: null;
			if (!conn.modifiedSQLExecuted||conn.isFromSlaveDB()) {
				// never executed modify sql,so auto commit
				this.autocommit = true;
				this.acCmd = _AUTOCOMMIT_ON;
			} else {
				this.autocommit = autocommit;
				this.acCmd = conn.autocommit != autocommit ? (autocommit ? _AUTOCOMMIT_ON
						: _AUTOCOMMIT_OFF)
						: null;

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("connectino syn command : schemaCmd:" + schemaCmd
						+ " charCmd:" + charCmd + " txIsolationCmd:" + isoCmd
						+ " autocommitCmd: " + acCmd);
			}

		}

		private Runnable updater;

		public boolean isExecuted() {
			return executed;
		}

		public boolean isSync() {
			return schemaCmd == null && charCmd == null && isoCmd == null
					&& acCmd == null;
		}

		public void update() {
			Runnable updater = this.updater;
			if (updater != null) {
				updater.run();
			}
		}

		/**
		 * @return false if sync complete
		 */
		public boolean sync() {
			CommandPacket cmd;
			if (schemaCmd != null) {
				updater = new Runnable() {
					@Override
					public void run() {
						conn.oldSchema = schema;
					}
				};
				cmd = schemaCmd;
				schemaCmd = null;
				cmd.write((BackendConnection) conn);
				// System.out.println("syn schema "+conn+" schema "+schema);
				return true;
			}
			if (charCmd != null) {
				updater = new Runnable() {
					@Override
					public void run() {
						int ci = StatusSync.this.charIndex;
						conn.charsetIndex = ci;
						conn.charset = CharsetUtil.getCharset(ci);
					}
				};
				cmd = charCmd;
				charCmd = null;
				cmd.write((BackendConnection) conn);
				// System.out.println("syn charCmd "+conn);
				return true;
			}
			if (isoCmd != null) {
				updater = new Runnable() {
					@Override
					public void run() {
						conn.txIsolation = StatusSync.this.txIsolation;
					}
				};
				cmd = isoCmd;
				isoCmd = null;
				cmd.write((BackendConnection) conn);
				// System.out.println("syn iso "+conn);
				return true;
			}
			if (acCmd != null) {
				updater = new Runnable() {
					@Override
					public void run() {
						conn.autocommit = StatusSync.this.autocommit;
					}
				};
				cmd = acCmd;
				acCmd = null;
				cmd.write((BackendConnection) conn);
				// System.out.println("syn autocomit "+conn);
				return true;
			}
			return false;
		}

		public void execute() throws UnsupportedEncodingException {
			executed = true;
			conn.sendQueryCmd(rrn.getStatement());

		}

		private static CommandPacket getTxIsolationCommand(int txIsolation) {
			switch (txIsolation) {
			case Isolations.READ_UNCOMMITTED:
				return _READ_UNCOMMITTED;
			case Isolations.READ_COMMITTED:
				return _READ_COMMITTED;
			case Isolations.REPEATED_READ:
				return _REPEATED_READ;
			case Isolations.SERIALIZABLE:
				return _SERIALIZABLE;
			default:
				throw new UnknownTxIsolationException("txIsolation:"
						+ txIsolation);
			}
		}

		private static CommandPacket getCharsetCommand(int ci) {
			String charset = CharsetUtil.getCharset(ci);
			StringBuilder s = new StringBuilder();
			s.append("SET names ").append(charset);
			CommandPacket cmd = new CommandPacket();
			cmd.packetId = 0;
			cmd.command = MySQLPacket.COM_QUERY;
			cmd.arg = s.toString().getBytes();
			return cmd;
		}

		private static CommandPacket getChangeSchemaCommand(String schema) {
			StringBuilder s = new StringBuilder();
			s.append(schema);
			CommandPacket cmd = new CommandPacket();
			cmd.packetId = 0;
			cmd.command = MySQLPacket.COM_INIT_DB;
			cmd.arg = s.toString().getBytes();
			return cmd;
		}
	}

	/**
	 * @return if synchronization finished and execute-sql has already been sent
	 *         before
	 */
	public boolean syncAndExcute() throws UnsupportedEncodingException {
		StatusSync sync = statusSync;
		if (sync.isExecuted()) {
			return true;
		}
		if (sync.isSync()) {
			sync.update();
			sync.execute();
		} else {
			sync.update();
			sync.sync();
		}
		return false;
	}

	public void execute(RouteResultsetNode rrn, ServerConnection sc,
			boolean autocommit) throws UnsupportedEncodingException {
		if (!modifiedSQLExecuted && rrn.isModifySQL()) {
			modifiedSQLExecuted = true;
		}
		StatusSync sync = new StatusSync(this, rrn, sc, autocommit);
		statusSync = sync;
		if (sync.isSync() || !sync.sync()) {
			sync.execute();
		}
	}

	/**
	 * by wuzh ,execute a query and ignore transaction settings for performance
	 * 
	 * @param sql
	 * @throws UnsupportedEncodingException
	 */
	public void query(String sql) throws UnsupportedEncodingException {
		sendQueryCmd(sql);
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public void quit() {
		if (isQuit.compareAndSet(false, true) && !isClosed()) {
			if (isAuthenticated) {
				// QS_TODO check
				write(writeToBuffer(QuitPacket.QUIT, allocate()));
				write(processor.getBufferPool().allocate());
			} else {
				close("normal");
			}
		}
	}

	@Override
	public void close(String reason) {
		isQuit.set(true);
		super.close(reason);
		if (isClosed.get()) {
			if (this.respHandler != null) {
				this.respHandler.connectionClose(this, reason);
			}
			pool.deActive(this);
		}
	}

	public void commit() {
		_COMMIT.write((BackendConnection) this);
	}

	public void rollback() {
		_ROLLBACK.write((BackendConnection) this);
	}

	public void release() {
		attachment = null;
		statusSync = null;
		modifiedSQLExecuted = false;
		setResponseHandler(null);
		pool.releaseChannel(this);
	}

	@Override
	public void error(int errCode, Throwable t) {
		LOGGER.warn(toString() + " error code: " + errCode, t);
		switch (errCode) {
		case ErrorCode.ERR_HANDLE_DATA:
			// handle error ..
			break;
		case ErrorCode.ERR_PUT_WRITE_QUEUE:
			// QS_TODO
			break;
		case ErrorCode.ERR_CONNECT_SOCKET:
			if (handler instanceof MySQLConnectionHandler) {
				MySQLConnectionHandler theHandler = (MySQLConnectionHandler) handler;
				theHandler.connectionError(t);
			} else {
				((MySQLConnectionAuthenticator) handler).connectionError(this,
						t);
			}
			break;

		}
	}

	public boolean setResponseHandler(ResponseHandler queryHandler) {
		if (handler instanceof MySQLConnectionHandler) {
			((MySQLConnectionHandler) handler).setResponseHandler(queryHandler);
			respHandler = queryHandler;
			return true;
		} else if (queryHandler != null) {
			LOGGER.warn("set not MySQLConnectionHandler "
					+ queryHandler.getClass().getCanonicalName());
		}
		return false;
	}

	/**
	 * 写队列为空，可以继续写数据
	 */
	public void writeQueueAvailable() {
		if (respHandler != null) {
			respHandler.writeQueueAvailable();
		}
	}

	/**
	 * 记录sql执行信息
	 */
	public void recordSql(String host, String schema, String stmt) {
		final long now = TimeUtil.currentTimeMillis();
		if (now > this.lastTime) {
			// long time = now - this.lastTime;
			// SQLRecorder sqlRecorder = this.pool.getSqlRecorder();
			// if (sqlRecorder.check(time)) {
			// SQLRecord recorder = new SQLRecord();
			// recorder.host = host;
			// recorder.schema = schema;
			// recorder.statement = stmt;
			// recorder.startTime = lastTime;
			// recorder.executeTime = time;
			// recorder.dataNode = pool.getName();
			// recorder.dataNodeIndex = pool.getIndex();
			// sqlRecorder.add(recorder);
			// }
		}
		this.lastTime = now;
	}

	public void closeNoActive(String reason) {
		if (isClosed.compareAndSet(false, true)) {
			close(reason);
		}
	}

	private static byte[] passwd(String pass, HandshakePacket hs)
			throws NoSuchAlgorithmException {
		if (pass == null || pass.length() == 0) {
			return null;
		}
		byte[] passwd = pass.getBytes();
		int sl1 = hs.seed.length;
		int sl2 = hs.restOfScrambleBuff.length;
		byte[] seed = new byte[sl1 + sl2];
		System.arraycopy(hs.seed, 0, seed, 0, sl1);
		System.arraycopy(hs.restOfScrambleBuff, 0, seed, sl1, sl2);
		return SecurityUtil.scramble411(passwd, seed);
	}

	@Override
	public boolean isFromSlaveDB() {
		return fromSlaveDB;
	}

	@Override
	public boolean isBorrowed() {
		return borrowed;
	}

	@Override
	public void setBorrowed(boolean borrowed) {
		this.borrowed = borrowed;
	}

	@Override
	public String toString() {
		return "MySQLConnection [id=" + id + ", isRunning=" + isRunning
				+ ", lastTime=" + lastTime + ", schema=" + schema
				+ ", borrowed=" + borrowed + ", fromSlaveDB=" + fromSlaveDB
				+ ", threadId=" + threadId + ", charset=" + charset
				+ ", txIsolation=" + txIsolation + ", autocommit=" + autocommit
				+ ", attachment=" + attachment + ", respHandler=" + respHandler
				+ ", host=" + host + ", port=" + port
				+ ", suppressReadTemporay=" + suppressReadTemporay + ", modifiedSQLExecuted=" + modifiedSQLExecuted + "]";
	}

	@Override
	public boolean isModifiedSQLExecuted() {
		return modifiedSQLExecuted;
	}

}