package org.opencloudb.mysql.nio.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.opencloudb.MycatConfig;
import org.opencloudb.MycatServer;
import org.opencloudb.backend.ConnectionMeta;
import org.opencloudb.backend.PhysicalConnection;
import org.opencloudb.backend.PhysicalDBNode;
import org.opencloudb.cache.CachePool;
import org.opencloudb.net.mysql.ErrorPacket;
import org.opencloudb.net.mysql.RowDataPacket;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.server.NonBlockingSession;
import org.opencloudb.server.parser.ServerParse;

/**
 * company where id=(select company_id from customer where id=3); the one which
 * return data (id) is the datanode to store child table's records
 * 
 * @author wuzhih
 * 
 */
public class FetchStoreNodeOfChildTableHandler implements ResponseHandler {
	private static final Logger LOGGER = Logger
			.getLogger(FetchStoreNodeOfChildTableHandler.class);
	private String sql;
	private volatile String result;
	private volatile String dataNode;
	private AtomicInteger finished = new AtomicInteger(0);
	protected final ReentrantLock lock = new ReentrantLock();

	public String execute(String schema, String sql, ArrayList<String> dataNodes) {
		String key = schema + ":" + sql;
		CachePool cache = MycatServer.getInstance().getCacheService()
				.getCachePool("ER_SQL2PARENTID");
		String result = (String) cache.get(key);
		if (result != null) {
			return result;
		}
		this.sql = sql;
		int totalCount = dataNodes.size();
		long startTime = System.currentTimeMillis();
		long endTime = startTime + 5 * 60 * 1000L;
		MycatConfig conf = MycatServer.getInstance().getConfig();

		for (String dn : dataNodes) {
			if (dataNode != null) {
				return dataNode;
			}
			PhysicalDBNode mysqlDN = conf.getDataNodes().get(dn);
			ConnectionMeta conMeta = new ConnectionMeta(mysqlDN.getDatabase(),
					null, -1, true);
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("execute in datanode " + dn);
				}
				mysqlDN.getConnection(conMeta, new RouteResultsetNode(dn,
						ServerParse.SELECT, sql), this, dn);
			} catch (Exception e) {
				LOGGER.warn("get connection err " + e);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {

			}
		}

		while (dataNode == null && System.currentTimeMillis() < endTime) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
			if (dataNode != null || finished.get() >= totalCount) {
				break;
			}
		}
		if (dataNode != null) {
			cache.putIfAbsent(key, dataNode);
		}
		return dataNode;

	}

	@Override
	public void connectionAcquired(PhysicalConnection conn) {
		conn.setRunning(true);
		conn.setResponseHandler(this);
		try {
			conn.query(sql);
		} catch (Exception e) {
			executeException(conn, e);
		}
	}

	@Override
	public void connectionError(Throwable e, PhysicalConnection conn) {
		finished.incrementAndGet();
		LOGGER.warn("connectionError " + e);

	}

	@Override
	public void errorResponse(byte[] data, PhysicalConnection conn) {
		finished.incrementAndGet();
		conn.setRunning(false);
		ErrorPacket err = new ErrorPacket();
		err.read(data);
		LOGGER.warn("errorResponse " + err.errno + " "
				+ new String(err.message));
		conn.setRunning(false);
		conn.release();

	}

	@Override
	public void okResponse(byte[] ok, PhysicalConnection conn) {
		boolean executeResponse = conn.syncAndExcute();
		if (executeResponse) {
			finished.incrementAndGet();
			conn.setRunning(false);
			conn.release();
		}

	}

	@Override
	public void rowResponse(byte[] row, PhysicalConnection conn) {
		if (result == null) {

			RowDataPacket rowDataPkg = new RowDataPacket(1);
			rowDataPkg.read(row);
			byte[] columnData = rowDataPkg.fieldValues.get(0);
			String columnVal = new String(columnData);
			result = columnVal;
			dataNode = (String) conn.getAttachment();
		} else {
			LOGGER.warn("find multi data nodes for child table store, sql is:  "
					+ sql);
		}

	}

	@Override
	public void rowEofResponse(byte[] eof, PhysicalConnection conn) {
		finished.incrementAndGet();
		conn.setRunning(false);
		conn.release();
	}

	private void executeException(PhysicalConnection c, Throwable e) {
		finished.incrementAndGet();
		LOGGER.warn("executeException   " + e);
		c.setRunning(false);
		c.close("exception:" + e);

	}

	@Override
	public void writeQueueAvailable() {

	}

	@Override
	public void connectionClose(PhysicalConnection conn, String reason) {

		LOGGER.warn("connection closed " + conn + " reason:" + reason);
	}

	@Override
	public void fieldEofResponse(byte[] header, List<byte[]> fields,
			byte[] eof, PhysicalConnection conn) {

	}

}
