package org.opencloudb.cache.impl;

import org.mapdb.HTreeMap;
import org.opencloudb.cache.CachePool;
import org.opencloudb.cache.CacheStatic;

public class MapDBCachePool implements CachePool {

	private final HTreeMap<Object, Object> htreeMap;
	private final CacheStatic cacheStati = new CacheStatic();
    private final long maxSize;
	public MapDBCachePool(HTreeMap<Object, Object> htreeMap,long maxSize) {
		this.htreeMap = htreeMap;
		this.maxSize=maxSize;
		cacheStati.setMaxSize(maxSize);
	}

	@Override
	public void putIfAbsent(Object key, Object value) {
		if (htreeMap.putIfAbsent(key, value) == null) {
			cacheStati.incPutTimes();
		}

	}

	@Override
	public Object get(Object key) {
		Object value = htreeMap.get(key);
		if (value != null) {
			cacheStati.incHitTimes();
			return value;
		} else {
			cacheStati.incAccessTimes();
			return null;
		}
	}

	@Override
	public void clearCache() {
		htreeMap.clear();
		cacheStati.reset();

	}

	@Override
	public CacheStatic getCacheStatic() {
		
		cacheStati.setItemSize(htreeMap.sizeLong());
		return cacheStati;
	}

	@Override
	public long getMaxSize() {
		return maxSize;
	}

}
