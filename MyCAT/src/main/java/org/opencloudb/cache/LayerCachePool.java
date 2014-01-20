package org.opencloudb.cache;

import java.util.Map;

/**
 * Layered cache pool
 * 
 * @author wuzhih
 * 
 */
public interface LayerCachePool extends CachePool {

	public void putIfAbsent(String primaryKey, Object secondKey, Object value);

	public Object get(String primaryKey, Object secondKey);

	/**
	 * get all cache static, name is cache name
	 * @return map of CacheStatic
	 */
	public Map<String, CacheStatic> getAllCacheStatic();
}
