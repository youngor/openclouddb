package org.opencloudb.route;

import java.sql.SQLNonTransientException;

import org.opencloudb.cache.CachePool;
import org.opencloudb.cache.CacheService;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.server.parser.ServerParse;

public class RouteService {
	private final CachePool sqlRouteCache;

	public RouteService(CacheService cachService) {
		sqlRouteCache = cachService.getCachePool("SQLRouteCache");

	}

	public RouteResultset route(SchemaConfig schema, int sqlType, String stmt,
			String charset, Object info) throws SQLNonTransientException {
		RouteResultset rrs = null;
		String cacheKey = null;
		if (sqlType == ServerParse.SELECT) {
			cacheKey = schema + stmt;
			rrs = (RouteResultset) sqlRouteCache.get(cacheKey);
			if (rrs != null) {
				return rrs;
			}
		}

		rrs = ServerRouterUtil.route(schema, sqlType, stmt, charset, info);
		if (sqlType == ServerParse.SELECT) {
			sqlRouteCache.putIfAbsent(cacheKey, rrs);
		}
		return rrs;
	}
}
