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

import java.util.LinkedHashMap;
import java.util.Map;

import org.opencloudb.util.FormatUtil;

/**
 * @author mycat
 */
public final class RouteResultset {
	private final String statement; // 原始语句
	private final int sqlType;
	private RouteResultsetNode[] nodes; // 路由结果节点

	private int limitStart;
	private boolean cacheAble;
	// used to store table's ID->datanodes cache
	// format is table.primaryKey
	private String primaryKey;
	// limit output total
	private int limitSize;
	private SQLMerge sqlMerge;

	public RouteResultset(String stmt, int sqlType) {
		this.statement = stmt;
		this.limitSize = -1;
		this.sqlType = sqlType;
	}

	public SQLMerge getSqlMerge() {
		return sqlMerge;
	}

	public boolean isCacheAble() {
		return cacheAble;
	}

	public void setCacheAble(boolean cacheAble) {
		this.cacheAble = cacheAble;
	}

	public boolean needMerge() {
		return limitSize > 0 || sqlMerge != null;
	}

	public int getSqlType() {
		return sqlType;
	}

	public boolean isHasAggrColumn() {
		return (sqlMerge != null) && sqlMerge.isHasAggrColumn();
	}

	public int getLimitStart() {
		return limitStart;
	}

	public String[] getGroupByCols() {
		return (sqlMerge != null) ? sqlMerge.getGroupByCols() : null;
	}

	private SQLMerge createSQLMergeIfNull() {
		if (sqlMerge == null) {
			sqlMerge = new SQLMerge();
		}
		return sqlMerge;
	}

	public Map<String, Integer> getMergeCols() {
		return (sqlMerge != null) ? sqlMerge.getMergeCols() : null;
	}

	public void setLimitStart(int limitStart) {
		this.limitStart = limitStart;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public boolean hasPrimaryKeyToCache() {
		return primaryKey != null;
	}

	public void setPrimaryKey(String primaryKey) {
		if (!primaryKey.contains(".")) {
			throw new java.lang.IllegalArgumentException(
					"must be table.primarykey fomat :" + primaryKey);
		}
		this.primaryKey = primaryKey;
	}

	/**
	 * return primary key items ,first is table name ,seconds is primary key
	 * 
	 * @return
	 */
	public String[] getPrimaryKeyItems() {
		return primaryKey.split("\\.");
	}

	public void setOrderByCols(LinkedHashMap<String, Integer> orderByCols) {
		if (orderByCols != null && !orderByCols.isEmpty()) {
			createSQLMergeIfNull().setOrderByCols(orderByCols);
		}
	}

	public void setHasAggrColumn(boolean hasAggrColumn) {
		if (hasAggrColumn) {
			createSQLMergeIfNull().setHasAggrColumn(true);
		}
	}

	public void setGroupByCols(String[] groupByCols) {
		if (groupByCols != null && groupByCols.length > 0) {
			createSQLMergeIfNull().setGroupByCols(groupByCols);
		}
	}

	public void setMergeCols(Map<String, Integer> mergeCols) {
		if (mergeCols != null && !mergeCols.isEmpty()) {
			createSQLMergeIfNull().setMergeCols(mergeCols);
		}

	}

	public LinkedHashMap<String, Integer> getOrderByCols() {
		return (sqlMerge != null) ? sqlMerge.getOrderByCols() : null;

	}

	public String getStatement() {
		return statement;
	}

	public RouteResultsetNode[] getNodes() {
		return nodes;
	}

	public void setNodes(RouteResultsetNode[] nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return -1 if no limit
	 */
	public int getLimitSize() {
		return limitSize;
	}

	public void setLimitSize(int limitSize) {
		this.limitSize = limitSize;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(statement).append(", route={");
		if (nodes != null) {
			for (int i = 0; i < nodes.length; ++i) {
				s.append("\n ").append(FormatUtil.format(i + 1, 3));
				s.append(" -> ").append(nodes[i]);
			}
		}
		s.append("\n}");
		return s.toString();
	}

}