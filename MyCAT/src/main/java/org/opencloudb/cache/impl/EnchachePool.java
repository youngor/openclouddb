package org.opencloudb.cache.impl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.opencloudb.cache.CachePool;

/**
 * encache based cache pool
 * 
 * @author wuzhih
 * 
 */
public class EnchachePool implements CachePool {
	private final Cache enCache;

	public EnchachePool(Cache enCache) {
		this.enCache = enCache;

	}

	@Override
	public void putIfAbsent(Object key, Object value) {
		Element el = new Element(key, value);
		enCache.putIfAbsent(el);

	}

	@Override
	public Object get(Object key) {
		Element cacheEl = enCache.get(key);
		if (cacheEl != null) {
			return cacheEl.getObjectValue();
		} else {
			// System.out.println("statistics :,hit "+enCache.getStatistics().getCacheHits()+" "+enCache.getStatistics().toString());
			return null;
		}
	}

	@Override
	public void clearCache() {
		enCache.removeAll();
		enCache.clearStatistics();

	}

}
