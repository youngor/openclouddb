package org.opencloudb.cache;

/**
 * simple cache pool for implement
 * 
 * @author wuzhih
 * 
 */
public interface CachePool {

	public void putIfAbsent(Object key, Object value);

	public Object get(Object key);

	public void clearCache();

	public CacheStatic getCacheStatic();

	public long getMaxSize();
}
