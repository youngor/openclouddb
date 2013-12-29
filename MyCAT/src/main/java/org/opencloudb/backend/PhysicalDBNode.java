package org.opencloudb.backend;

import org.apache.log4j.Logger;
import org.opencloudb.mysql.nio.handler.ResponseHandler;
import org.opencloudb.route.RouteResultsetNode;

public class PhysicalDBNode {
	protected static final Logger LOGGER = Logger
			.getLogger(PhysicalDBNode.class);

	protected final String name;
	protected final String database;
	protected final PhysicalDBPool dbPool;
	protected volatile long executeCount;

	public PhysicalDBNode(String hostName, String database,
			PhysicalDBPool dbPool) {
		this.name = hostName;
		this.database = database;
		this.dbPool = dbPool;
	}

	public String getName() {
		return name;
	}

	public long getExecuteCount() {
		return executeCount;
	}

	public PhysicalDBPool getDbPool() {
		return dbPool;
	}

	public String getDatabase() {
		return database;
	}

	public boolean isInitSuccess() {
		return dbPool.isInitSuccess();
	}

	/**
	 * get connection from the same datasource
	 * 
	 * @param exitsCon
	 * @throws Exception
	 */
	public void getConnectionFromSameSource(PhysicalConnection exitsCon,
			ResponseHandler handler, Object attachment) throws Exception {
		PhysicalDatasource ds = this.dbPool.findDatasouce(exitsCon);
		if (ds == null) {
			throw new RuntimeException(
					"can't find exits connection,maybe fininshed " + exitsCon);
		} else {
			ds.getConnection(handler, attachment, this.database);
		}

	}

	public void getConnection(RouteResultsetNode rrs, boolean autocommit,
			ResponseHandler handler, Object attachment) throws Exception {
		if (dbPool.isInitSuccess()) {
			if (rrs.canRunnINReadDB(autocommit)) {
				dbPool.getRWBanlanceCon(handler, attachment, this.database);
			} else {
				dbPool.getSource().getConnection(handler, attachment,
						this.database);
			}

			executeCount++;
		} else {
			throw new IllegalArgumentException("Invalid DataSource:"
					+ dbPool.getActivedIndex());
		}
	}
}
