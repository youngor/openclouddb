package org.opencloudb.heartbeat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.opencloudb.statistic.HeartbeatRecorder;

public abstract class DBHeartbeat {
	public static final int OK_STATUS = 1;
	public static final int ERROR_STATUS = -1;
	public static final int TIMEOUT_STATUS = -2;
	public static final int INIT_STATUS = 0;
	private static final long DEFAULT_HEARTBEAT_TIMEOUT = 30 * 1000L;
	private static final int DEFAULT_HEARTBEAT_RETRY = 10;
	// heartbeat config
	protected long heartbeatTimeout = DEFAULT_HEARTBEAT_TIMEOUT; // 心跳超时时间
	protected int heartbeatRetry = DEFAULT_HEARTBEAT_RETRY; // 检查连接发生异常到切换，重试次数
	protected String heartbeatSQL;// 静态心跳语句
	protected final AtomicBoolean isStop = new AtomicBoolean(true);
	protected final AtomicBoolean isChecking = new AtomicBoolean(false);
	protected int errorCount;
	protected volatile int status;
	protected final HeartbeatRecorder recorder = new HeartbeatRecorder();

	public int getStatus() {
		return status;
	}

	public boolean isChecking() {
		return isChecking.get();
	}

	public abstract void start();

	public abstract void stop();

	public boolean isStop() {
		return isStop.get();
	}

	public int getErrorCount() {
		return errorCount;
	}

	public HeartbeatRecorder getRecorder() {
		return recorder;
	}

	public abstract String getLastActiveTime();

	public abstract long getTimeout();

	public abstract void heartbeat();

	public long getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	public void setHeartbeatTimeout(long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public int getHeartbeatRetry() {
		return heartbeatRetry;
	}

	public void setHeartbeatRetry(int heartbeatRetry) {
		this.heartbeatRetry = heartbeatRetry;
	}

	public String getHeartbeatSQL() {
		return heartbeatSQL;
	}

	public void setHeartbeatSQL(String heartbeatSQL) {
		this.heartbeatSQL = heartbeatSQL;
	}

	public boolean isNeedHeartbeat() {
		return heartbeatSQL != null;
	}

}
