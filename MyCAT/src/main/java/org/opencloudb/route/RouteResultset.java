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
	private LinkedHashMap<String, Integer> orderByCols;
	private Map<String, Integer> mergeCols;
	private String[] groupByCols;
	private int limitStart;
	// limit output total
	private int limitSize;
	private boolean hasAggrColumn;
	
	public RouteResultset(String stmt,int sqlType) {
		this.statement = stmt;
		this.limitSize = -1;
		this.sqlType=sqlType;
	}
	
	public boolean needMerge() {
		return limitSize > 0 || orderByCols != null || groupByCols != null||hasAggrColumn;
	}

	public int getSqlType() {
		return sqlType;
	}

	public boolean isHasAggrColumn() {
		return hasAggrColumn;
	}

	public int getLimitStart() {
		return limitStart;
	}

	public String[] getGroupByCols() {
		return groupByCols;
	}

	public void setGroupByCols(String[] groupByCols) {
		this.groupByCols = groupByCols;
	}

	
	public Map<String, Integer> getMergeCols() {
		return mergeCols;
	}

	public void setMergeCols(Map<String, Integer> mergeCols) {
		this.mergeCols = mergeCols;
	}

	
	public void setLimitStart(int limitStart) {
		this.limitStart = limitStart;
	}

	

	public void setOrderByCols(LinkedHashMap<String, Integer> orderByCols) {
		this.orderByCols = orderByCols;
	}

	public LinkedHashMap<String, Integer> getOrderByCols() {
		return orderByCols;
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

	public void setHasAggrColumn(boolean hasAggrColumn) {
		this.hasAggrColumn=hasAggrColumn;
		
	}

}