package org.opencloudb.cache;
/**
 * factory used to create cachePool
 * @author wuzhih
 *
 */
public abstract class CachePoolFactory {

	/**
	 *  create a cache pool instance
	 * @param poolName
	 * @param cacheSize
	 * @param expireSeconds -1 for not expired
	 * @return
	 */
	public abstract CachePool createCachePool(String poolName,int cacheSize,int expireSeconds);
}
