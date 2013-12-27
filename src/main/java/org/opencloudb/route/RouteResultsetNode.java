/*
 * Copyright 2012-2015 org.opencloudb.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencloudb.route;

import org.opencloudb.server.parser.ServerParse;

/**
 * @author mycat
 */
public final class RouteResultsetNode {
	private final String name; // 数据节点名称
	private final String statement; // 执行的语句
	private final int sqlType;
	private final boolean canRunInReadDB;
	private final boolean hasBlanceFlag;

	public RouteResultsetNode(String name, int sqlType, String statement) {
		this.name = name;
		this.sqlType = sqlType;
		this.statement = statement;
		canRunInReadDB = (sqlType == ServerParse.SELECT || sqlType == ServerParse.SHOW);
		hasBlanceFlag = statement.startsWith("/*balance*/");
	}

	public boolean canRunnINReadDB(boolean autocommit) {
		return canRunInReadDB && (autocommit || hasBlanceFlag);

	}

	public String getName() {
		return name;
	}

	public int getSqlType() {
		return sqlType;
	}

	public String getStatement() {
		return statement;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof RouteResultsetNode) {
			RouteResultsetNode rrn = (RouteResultsetNode) obj;
			if (equals(name, rrn.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(name);
		s.append('{').append(statement).append('}');
		return s.toString();
	}

	private static boolean equals(String str1, String str2) {
		if (str1 == null) {
			return str2 == null;
		}
		return str1.equals(str2);
	}

}