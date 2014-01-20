package org.opencloudb.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

import org.opencloudb.cache.CachePool;
import org.opencloudb.cache.CachePoolFactory;

public class EnchachePooFactory extends CachePoolFactory {

	@Override
	public CachePool createCachePool(String poolName, int cacheSize,
			int expiredSeconds) {
		CacheManager cacheManager = CacheManager.create();
		Cache enCache = cacheManager.getCache(poolName);
		if (enCache == null) {

			CacheConfiguration cacheConf = cacheManager.getConfiguration()
					.getDefaultCacheConfiguration().clone();
			cacheConf.setName(poolName);
			if (cacheConf.getMaxEntriesLocalHeap() != 0) {
				cacheConf.setMaxEntriesLocalHeap(cacheSize);
			} else {
				cacheConf.setMaxBytesLocalHeap(String.valueOf(cacheSize));
			}
			cacheConf.setTimeToIdleSeconds(expiredSeconds);
			Cache cache = new Cache(cacheConf);
			cacheManager.addCache(cache);
			return new EnchachePool(poolName,cache,cacheSize);
		} else {
			return new EnchachePool(poolName,enCache,cacheSize);
		}
	}

}
