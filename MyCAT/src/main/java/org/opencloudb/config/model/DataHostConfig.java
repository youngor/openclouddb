package org.opencloudb.config.model;

import java.util.Map;

/**
 * Datahost is a group of DB servers which is synchronized with each other
 * 
 * @author wuzhih
 * 
 */
public class DataHostConfig {
	private String name;
	public static final int BALANCE_NONE = 0;
	private int maxCon = SystemConfig.DEFAULT_POOL_SIZE;
	private int minCon = 10;
	private int balance = BALANCE_NONE;
	private final String dbType;
	private final String dbDriver;
	private final DBHostConfig[] writeHosts;
	private final Map<Integer,DBHostConfig[]> readHosts;
	private String hearbeatSQL;
	public DataHostConfig(String name, String dbType, String dbDriver,
			DBHostConfig[] writeHosts, Map<Integer,DBHostConfig[]> readHosts) {
		super();
		this.name = name;
		this.dbType = dbType;
		this.dbDriver = dbDriver;
		this.writeHosts = writeHosts;
		this.readHosts = readHosts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxCon() {
		return maxCon;
	}

	public void setMaxCon(int maxCon) {
		this.maxCon = maxCon;
	}

	public int getMinCon() {
		return minCon;
	}

	public void setMinCon(int minCon) {
		this.minCon = minCon;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	
	public String getDbType() {
		return dbType;
	}

	public String getDbDriver() {
		return dbDriver;
	}

	public DBHostConfig[] getWriteHosts() {
		return writeHosts;
	}

	
	public Map<Integer, DBHostConfig[]> getReadHosts() {
		return readHosts;
	}

	public String getHearbeatSQL() {
		return hearbeatSQL;
	}

	public void setHearbeatSQL(String heartbeatSQL) {
		this.hearbeatSQL=heartbeatSQL;
		
	}
	
	
}
