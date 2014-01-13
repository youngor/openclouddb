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
/**
 * (created at 2012-6-13)
 */
package org.opencloudb.config.model;

import java.util.ArrayList;
import java.util.Random;

import org.opencloudb.config.model.rule.RuleConfig;
import org.opencloudb.util.SplitUtil;

/**
 * @author mycat
 */
public class TableConfig {
	public static final int TYPE_GLOBAL_TABLE = 1;
	public static final int TYPE_GLOBAL_DEFAULT = 0;
	private final String name;
	private final String primaryKey;
	private final int tableType;
	private final ArrayList<String> dataNodes;
	private final RuleConfig rule;
	private final String partitionColumn;
	private final boolean ruleRequired;
	private final TableConfig parentTC;
	private final boolean childTable;
	private final String joinKey;
	private final String parentKey;
	private final String locateRTableKeySql;
	// only has one level of parent
	private final boolean secondLevel;
	private final boolean partionKeyIsPrimaryKey;
	private final Random rand = new Random();

	public TableConfig(String name, String primaryKey, int tableType,
			String dataNode, RuleConfig rule, boolean ruleRequired,
			TableConfig parentTC, boolean isChildTable, String joinKey,
			String parentKey) {
		if (name == null) {
			throw new IllegalArgumentException("table name is null");
		} else if (dataNode == null) {
			throw new IllegalArgumentException("dataNode name is null");
		}
		this.primaryKey = primaryKey;
		this.tableType = tableType;
		if (ruleRequired && rule == null) {
			throw new IllegalArgumentException("ruleRequired but rule is null");
		}

		this.name = name.toUpperCase();
		String theDataNodes[] = SplitUtil.split(dataNode, ',', '$', '-', '[',
				']');

		if (theDataNodes == null || theDataNodes.length <= 0) {
			throw new IllegalArgumentException("invalid table dataNodes: "
					+ dataNode);
		}
		dataNodes = new ArrayList<String>(theDataNodes.length);
		for (String dn : theDataNodes) {
			dataNodes.add(dn);
		}
		this.rule = rule;
		this.partitionColumn = (rule == null) ? null : rule.getColumn();
		partionKeyIsPrimaryKey=(partitionColumn==null)?primaryKey==null:partitionColumn.equals(primaryKey);
		this.ruleRequired = ruleRequired;
		this.childTable = isChildTable;
		this.parentTC = parentTC;
		this.joinKey = joinKey;
		this.parentKey = parentKey;
		if (parentTC != null) {
			locateRTableKeySql = genLocateRootParentSQL();
			secondLevel = (parentTC.parentTC == null);
		} else {
			locateRTableKeySql = null;
			secondLevel = false;
		}
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public boolean isSecondLevel() {
		return secondLevel;
	}

	public String getLocateRTableKeySql() {
		return locateRTableKeySql;
	}

	public String genLocateRootParentSQL() {
		TableConfig tb = this;
		StringBuilder tableSb = new StringBuilder();
		StringBuilder condition = new StringBuilder();
		TableConfig prevTC = null;
		int level = 0;
		String latestCond = null;
		while (tb.parentTC != null) {
			tableSb.append(tb.parentTC.name).append(',');
			String relation = null;
			if (level == 0) {
				latestCond = " " + tb.parentTC.getName() + '.' + tb.parentKey
						+ "=";
			} else {
				relation = tb.parentTC.getName() + '.' + tb.parentKey + '='
						+ tb.name + '.' + tb.joinKey;
				condition.append(relation).append(" AND ");
			}
			level++;
			prevTC = tb;
			tb = tb.parentTC;
		}
		String sql = "SELECT "
				+ prevTC.parentTC.name
				+ '.'
				+ prevTC.parentKey
				+ " FROM "
				+ tableSb.substring(0, tableSb.length() - 1)
				+ " WHERE "
				+ ((level < 2) ? latestCond : condition.toString() + latestCond);
		// System.out.println(this.name+" sql " + sql);
		return sql;

	}

	public String getPartitionColumn() {
		return partitionColumn;
	}

	public int getTableType() {
		return tableType;
	}

	/**
	 * get root parent
	 * 
	 * @return
	 */
	public TableConfig getRootParent() {
		if (parentTC == null) {
			return null;
		}
		TableConfig preParent = parentTC;
		TableConfig parent = preParent.getParentTC();

		while (parent != null) {
			preParent = parent;
			parent = parent.getParentTC();
		}
		return preParent;
	}

	public TableConfig getParentTC() {
		return parentTC;
	}

	public boolean isChildTable() {
		return childTable;
	}

	public String getJoinKey() {
		return joinKey;
	}

	public String getParentKey() {
		return parentKey;
	}

	/**
	 * @return upper-case
	 */
	public String getName() {
		return name;
	}

	public ArrayList<String> getDataNodes() {
		return dataNodes;
	}

	public String getRandomDataNode() {
		int index = Math.abs(rand.nextInt()) % dataNodes.size();
		return dataNodes.get(index);
	}

	public boolean isRuleRequired() {
		return ruleRequired;
	}

	public RuleConfig getRule() {
		return rule;
	}

	public boolean primaryKeyIsPartionKey() {
		return partionKeyIsPrimaryKey;
	}

}