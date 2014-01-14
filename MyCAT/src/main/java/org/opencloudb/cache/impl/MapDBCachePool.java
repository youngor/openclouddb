package org.opencloudb.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.opencloudb.cache.LayerCacheService;

public class MapDBCachePool implements LayerCacheService {
	private final DB db;
	private Map<String, HTreeMap<Object, Object>> allCaches = new HashMap<String, HTreeMap<Object, Object>>();
	private final ReentrantLock lock;
	private int defaultCacheSize = 10000;
	private final String defaultCache = "default";

	public MapDBCachePool() {
		lock = new ReentrantLock();
		db = DBMaker.newDirectMemoryDB().cacheSize(defaultCacheSize)
				.cacheLRUEnable().make();
	}

	private HTreeMap<Object, Object> createCache(String name) {
		return this.db.createHashMap(name).makeOrGet();
	}

	private HTreeMap<Object, Object> getCache(String cacheName) {
		HTreeMap<Object, Object> cache = allCaches.get(cacheName);
		if (cache == null) {
			lock.lock();
			try {

				cache = allCaches.get(cacheName);
				if (cache == null) {
					cache = this.createCache(cacheName);
					allCaches.put(cacheName, cache);
				}

			} finally {
				lock.unlock();
			}
		}
		return cache;
	}

	@Override
	public void putIfAbsent(Object key, Object value) {
		putIfAbsent(defaultCache,key,value);

	}

	@Override
	public Object get(Object key) {
		return get(defaultCache, key);
	}

	@Override
	public void clearCache() {
		for (Map.Entry<String, Object> cache : db.getAll().entrySet()) {
			HTreeMap<?, ?> cacheMap = (HTreeMap<?, ?>) cache;
			cacheMap.clear();
			// cacheMap.sizeLong()
		}

	}

	@Override
	public void putIfAbsent(String primaryKey, Object secondKey, Object value) {
		getCache(primaryKey).putIfAbsent(secondKey, value);
	}

	@Override
	public Object get(String primaryKey, Object secondKey) {
		return getCache(primaryKey).get(secondKey);
	}
}
