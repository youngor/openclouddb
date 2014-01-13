package org.opencloudb;

import java.util.HashMap;

import org.opencloudb.cache.CachePool;

public class SimpleCachePool implements CachePool {
	private HashMap<Object, Object> cacheMap = new HashMap<Object, Object>();

	@Override
	public void putIfAbsent(Object key, Object value) {
		cacheMap.put(key, value);

	}

	@Override
	public Object get(Object key) {
		return cacheMap.get(key);
	}

	@Override
	public void clearCache() {
		cacheMap.clear();

	}
};