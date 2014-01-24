package org.opencloudb.config.model;

public class DBHostConfig {
	private long idleTimeout = SystemConfig.DEFAULT_IDLE_TIMEOUT; // 连接池中连接空闲超时时间
	private final String hostName;
	private final String ip;
	private final int port;
	private final String url;
	private final String user;
	private final String password;
	private int maxCon ;
	private int minCon ;
	private String dbType;
	
	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public DBHostConfig(String hostName, String ip, int port, String url,
			String user, String password) {
		super();
		this.hostName = hostName;
		this.ip = ip;
		this.port = port;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
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

	public String getHostName() {
		return hostName;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "DBHostConfig [hostName=" + hostName + ", url=" + url + "]";
	}

}
