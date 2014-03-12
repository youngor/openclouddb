package org.opencloudb.jdbc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.opencloudb.backend.BackendConnection;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.net.mysql.EOFPacket;
import org.opencloudb.net.mysql.FieldPacket;
import org.opencloudb.net.mysql.MySQLPacket;
import org.opencloudb.net.mysql.OkPacket;
import org.opencloudb.net.mysql.ResultSetHeaderPacket;
import org.opencloudb.net.mysql.RowDataPacket;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.ServerConnection;
import org.opencloudb.util.ResultSetUtil;

public class JDBCConnection implements BackendConnection {
	private JDBCDatasource pool;
	private volatile String schema;
	private volatile String oldSchema;
	private boolean autocommit;
	private byte packetId;
	private int txIsolation;
	private volatile boolean running = false;
	private volatile boolean borrowed;
	private ResultSet rs = null;
	private long id = 0;
	private String host;
	private int port;
	private Connection con;
	private ResponseHandler respHandler;
	private volatile Object attachement;
	List<RowDataPacket> rowsPkg = new LinkedList<RowDataPacket>();
	List<FieldPacket> fieldPks = new LinkedList<FieldPacket>();
	boolean headerOutputed = false;
	private volatile boolean modifiedSQLExecuted;

	public JDBCConnection() {

	}

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	@Override
	public void close(String reason) {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setPool(JDBCDatasource pool) {
		this.pool = pool;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public boolean isClosed() {
		try {
			return con == null || con.isClosed();
		} catch (SQLException e) {
			return true;
		}
	}

	@Override
	public void idleCheck() {
		// TODO Auto-generated method stub

	}

	@Override
	public long getStartupTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNetInBytes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNetOutBytes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isModifiedSQLExecuted() {
		return modifiedSQLExecuted;
	}

	@Override
	public boolean isFromSlaveDB() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSchema(String newSchema) {
		this.oldSchema = this.schema;
		this.schema = newSchema;

	}

	@Override
	public long getLastTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isClosedOrQuit() {
		return this.isClosed();
	}

	@Override
	public void setAttachment(Object attachment) {
		this.attachement = attachment;

	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLastTime(long currentTimeMillis) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		modifiedSQLExecuted = false;
		setResponseHandler(null);
		pool.releaseChannel(this);
	}

	@Override
	public void setRunning(boolean running) {
		this.running = running;

	}

	@Override
	public boolean setResponseHandler(ResponseHandler commandHandler) {
		respHandler = commandHandler;
		return false;
	}

	@Override
	public void commit() {

	}

	private void executeSQL(RouteResultsetNode rrn, ServerConnection sc,
			boolean autocommit) throws IOException {
		// set running??

		String orgin = rrn.getStatement();
		String sql = rrn.getStatement().toLowerCase();
		try {
			if (!this.schema.equals(this.oldSchema)) {
				con.setCatalog(schema);
				this.oldSchema = schema;
			}
			if (sql.startsWith("select")) {
				rs = con.createStatement().executeQuery(orgin);

				ResultSetUtil.resultSetToPacket(sc.getCharset(), con, fieldPks,
						rs, rowsPkg);

				ResultSetHeaderPacket headerPkg = new ResultSetHeaderPacket();
				headerPkg.fieldCount = fieldPks.size();
				// todo
				headerPkg.write(sc.allocate(), sc);

			} else if (sql.startsWith("create") || sql.startsWith("drop")
					|| sql.startsWith("insert")) {
				int count = con.createStatement().executeUpdate(sql);
				OkPacket okPck = new OkPacket();
				okPck.affectedRows = count;
				okPck.insertId = 0;
				okPck.packetId = ++packetId;
				okPck.message = " OK!".getBytes();
				//okPck.write(sc.allocate(), sc);
				this.respHandler.okResponse(okPck.write(),this);

			} else {
				throw new RuntimeException("not supported");
			}
		} catch (SQLException e) {
			// e.printStackTrace();
			this.respHandler.errorResponse(e.getMessage().getBytes(), this);
			// throw new IOException(e);
			// e.printStackTrace();
			// ErrorPacket err = new ErrorPacket();
			// err.errno = 10101;
			// err.message = StringUtil.encode(e.toString(), sc.getCharset());
			// err.write(sc.allocate(), sc);
		} finally {
			fieldPks.clear();
			rowsPkg.clear();
		}

	}

	public MySQLPacket receive() throws IOException {
		if (!this.fieldPks.isEmpty()) {
			Iterator<FieldPacket> itor = fieldPks.iterator();
			FieldPacket curField = itor.next();
			curField.packetId = ++packetId;
			itor.remove();
			return curField;
		} else {
			if (!headerOutputed) {
				headerOutputed = true;
				EOFPacket eofPckg = new EOFPacket();
				eofPckg.packetId = ++packetId;
				return eofPckg;
			} else if (!this.rowsPkg.isEmpty()) {
				System.out.println("row ");
				Iterator<RowDataPacket> itor = rowsPkg.iterator();
				RowDataPacket curRow = itor.next();
				curRow.packetId = ++packetId;
				itor.remove();
				return curRow;
			}
		}
		EOFPacket eofPckg = new EOFPacket();
		eofPckg.packetId = ++packetId;
		return eofPckg;

	}

	@Override
	public void query(String sql) throws UnsupportedEncodingException {

	}

	@Override
	public Object getAttachment() {
		return this.attachement;
	}

	@Override
	public String getCharset() {
		return null;
	}

	@Override
	public void execute(RouteResultsetNode node, ServerConnection source,
			boolean autocommit) throws IOException {
		executeSQL(node, source, autocommit);

	}

	@Override
	public void recordSql(String host, String schema, String statement) {

	}

	@Override
	public boolean syncAndExcute() {
		return true;
	}

	@Override
	public void rollback() {

	}

	@Override
	public boolean isSuppressReadTemporay() {
		return false;
	}

	@Override
	public void setSuppressReadTemporay(boolean b) {

	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public boolean isBorrowed() {
		return this.borrowed;
	}

	@Override
	public void setBorrowed(boolean borrowed) {
		this.borrowed = borrowed;

	}

	@Override
	public int getTxIsolation() {
		return txIsolation;
	}

	@Override
	public boolean isAutocommit() {
		return autocommit;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "JDBCConnection [autocommit=" + autocommit + ", txIsolation="
				+ txIsolation + ", running=" + running + ", borrowed="
				+ borrowed + ", rs=" + rs + ", id=" + id + ", host=" + host
				+ ", port=" + port + "]";
	}

}
