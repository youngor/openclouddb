package org.opencloudb.cache;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MysqlDataSetService {
	private volatile boolean enabled = false;
	// max expire time is 300 seconds
	private int maxExpire = 300;
	private final ConcurrentHashMap<String, MysqlDataSetCache> cachedMap = new ConcurrentHashMap<String, MysqlDataSetCache>();
	private volatile Set<String> needCachedSQL = new HashSet<String>();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMaxExpire() {
		return maxExpire;
	}

	public void setMaxExpire(int maxExpire) {
		this.maxExpire = maxExpire;
	}

	private static MysqlDataSetService instance = new MysqlDataSetService();

	public static MysqlDataSetService getInstance() {
		return instance;
	}

	private MysqlDataSetService() {

	}

	/**
	 * sql should not include LIMIT range
	 * 
	 * @param sql
	 * @return
	 */
	public MysqlDataSetCache findDataSetCache(String sql) {
		if (!enabled) {
			return null;
		}
		MysqlDataSetCache cache = cachedMap.get(sql);
		if (validCache(cache)) {
			return cache;
		} else {
			cachedMap.remove(sql);
		}

		return null;

	}

	public String needCache(String sql)
	{
		return needCachedSQL.contains(sql)?sql:null;
	}
	public boolean addIfNotExists(MysqlDataSetCache newCache) {
		return (cachedMap.putIfAbsent(newCache.getSql(), newCache) == null);
	}

	private boolean validCache(MysqlDataSetCache cache) {
		return (!cache.isStoring()
				&& (cache.getCreateTime() + this.maxExpire * 1000 < System
						.currentTimeMillis()) && (new File(cache.getDataFile())
				.exists()));
	}
}
