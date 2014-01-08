package org.opencloudb.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.opencloudb.cache.impl.EnchachePool;
import org.opencloudb.handler.ConfFileHandler;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * cache service for other component default using memory cache encache
 * 
 * @author wuzhih
 * 
 */
public class CacheService {
	private static final Logger logger = Logger.getLogger(CacheService.class);
	private static final CacheManager cacheManager = CacheManager.create();
	private final Map<String, CachePool> allPools = new HashMap<String, CachePool>();

	public CacheService() throws IOException {

		// load cache pool defined
		init();

	}

	private void init() throws IOException {
		Properties props = new Properties();
		props.load(CacheService.class
				.getResourceAsStream("/cacheservice.properties"));
		final String poolKeyPref = "pool.";
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = (String) entry.getKey();

			if (key.startsWith(poolKeyPref)) {
				String value = (String) entry.getValue();
				createPool(key.substring(poolKeyPref.length()), value);
			}
		}
	}

	private void createPool(String poolName, String type) {
		if (allPools.containsKey(poolName)) {
			throw new java.lang.IllegalArgumentException(
					"duplicate cache pool name: " + poolName);
		}
		if (type.equalsIgnoreCase("encache")) {
			Cache enCache = cacheManager.getCache(poolName);
			if (enCache == null) {
				throw new java.lang.IllegalArgumentException(
						"encache cache pool name: " + poolName
								+ "not configed at ehcache.xml");
			}
			allPools.put(poolName, new EnchachePool(enCache));
		} else {
			throw new java.lang.IllegalArgumentException(
					"cache pool not implemented yet: " + poolName);
		}

	}

	/**
	 * get cache pool by name ,caller should cache result
	 * 
	 * @param poolName
	 * @return CachePool
	 */
	public CachePool getCachePool(String poolName) {
		CachePool pool = allPools.get(poolName);
		if (pool == null) {
			throw new IllegalArgumentException("can't find cache pool:"
					+ poolName);
		} else {
			return pool;
		}

	}

	public void clearCache() {
		
		logger.info("clear all cache pool ");
		for (CachePool pool : allPools.values()) {

			pool.clearCache();
		}

	}

}
