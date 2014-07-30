package org.opencloudb.interceptor.impl;

import org.opencloudb.interceptor.SQLInterceptor;
import org.opencloudb.server.parser.ServerParse;

public class DefaultSqlInterceptor implements SQLInterceptor {

	/**
	 * escape mysql escape letter
	 */
	@Override
	public String interceptSQL(String sql, int sqlType) {
		if (sqlType == ServerParse.UPDATE || sqlType == ServerParse.INSERT||sqlType == ServerParse.SELECT||sqlType == ServerParse.DELETE) {
			return sql.replace("\\'", "''");
		} else {
			return sql;
		}
	}

}
