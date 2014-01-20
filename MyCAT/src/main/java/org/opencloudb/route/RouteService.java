package org.opencloudb.route;

import java.sql.SQLNonTransientException;

import org.opencloudb.cache.CachePool;
import org.opencloudb.cache.CacheService;
import org.opencloudb.cache.LayerCachePool;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.server.parser.ServerParse;

public class RouteService {
	private final CachePool sqlRouteCache;
	private final LayerCachePool tableId2DataNodeCache;

	public RouteService(CacheService cachService) {
		sqlRouteCache = cachService.getCachePool("SQLRouteCache");
		tableId2DataNodeCache = (LayerCachePool) cachService
				.getCachePool("TableID2DataNodeCache");
	}

	public LayerCachePool getTableId2DataNodeCache() {
		return tableId2DataNodeCache;
	}

	public RouteResultset route(SchemaConfig schema, int sqlType, String stmt,
			String charset, Object info) throws SQLNonTransientException {
		RouteResultset rrs = null;
		String cacheKey = null;
		if (sqlType == ServerParse.SELECT) {
			cacheKey = schema.getName() + stmt;
			rrs = (RouteResultset) sqlRouteCache.get(cacheKey);
			if (rrs != null) {
				return rrs;
			}
		}

		rrs = ServerRouterUtil.route(schema, sqlType, stmt, charset, info,
				tableId2DataNodeCache);
		if (sqlType == ServerParse.SELECT && rrs.isCacheAble()) {
			sqlRouteCache.putIfAbsent(cacheKey, rrs);
		}
		return rrs;
	}
}
