package org.opencloudb.cache;

/**
 * Layered cache service
 * 
 * @author wuzhih
 * 
 */
public interface LayerCacheService extends CachePool {

	public void putIfAbsent(String primaryKey, Object secondKey, Object value);

	public Object get(String primaryKey, Object secondKey);

}
