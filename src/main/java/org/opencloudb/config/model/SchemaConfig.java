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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author mycat
 */
public class SchemaConfig {

	private final String name;
	private final Map<String, TableConfig> tables;
	private final boolean noSharding;
	private final String dataNode;
	private final Set<String> metaDataNodes;
	private final Set<String> allDataNodes;
	/**
	 * key is join relation ,A.ID=B.PARENT_ID value is Root Table ,if a->b*->c*
	 * ,then A is root table
	 */
	private final Map<String, TableConfig> joinRel2TableMap = new HashMap<String, TableConfig>();

	public SchemaConfig(String name, String dataNode,
			Map<String, TableConfig> tables) {
		this.name = name;
		this.dataNode = dataNode;
		this.tables = tables;
		buildJoinMap(tables);
		this.noSharding = (tables == null || tables.isEmpty()) ? true : false;
		if (!noSharding && dataNode != null) {
			throw new RuntimeException(
					name+" in sharidng mode schema can't have dataNode ");
		}
		this.metaDataNodes = buildMetaDataNodes();
		this.allDataNodes = buildAllDataNodes();
	}

	private void buildJoinMap(Map<String, TableConfig> tables2) {

		if (tables == null || tables.isEmpty()) {
			return;
		}
		for (TableConfig tc : tables.values()) {
			if (tc.isChildTable()) {
				TableConfig rootTc = tc.getRootParent();
				String joinRel1 = tc.getName() + '.' + tc.getJoinKey() + '='
						+ tc.getParentTC().getName() + '.' + tc.getParentKey();
				String joinRel2 = tc.getParentTC().getName() + '.'
						+ tc.getParentKey() + '=' + tc.getName() + '.'
						+ tc.getJoinKey();
				joinRel2TableMap.put(joinRel1, rootTc);
				joinRel2TableMap.put(joinRel2, rootTc);
			}

		}

	}

	public Map<String, TableConfig> getJoinRel2TableMap() {
		return joinRel2TableMap;
	}

	public String getName() {
		return name;
	}

	public String getDataNode() {
		return dataNode;
	}

	public Map<String, TableConfig> getTables() {
		return tables;
	}

	public boolean isNoSharding() {
		return noSharding;
	}

	public Set<String> getMetaDataNodes() {
		return metaDataNodes;
	}

	public Set<String> getAllDataNodes() {
		return allDataNodes;
	}

	public String getRandomDataNode() {
		if (allDataNodes == null || allDataNodes.isEmpty()) {
			return null;
		}
		return allDataNodes.iterator().next();
	}

	/**
	 * 取得含有不同Meta信息的数据节点,比如表和表结构。
	 */
	private Set<String> buildMetaDataNodes() {
		Set<String> set = new HashSet<String>();
		if (!isEmpty(dataNode)) {
			set.add(dataNode);
		}
		if (!noSharding) {
			for (TableConfig tc : tables.values()) {
				set.add(tc.getDataNodes().get(0));
			}
		}

		return set;
	}

	/**
	 * 取得该schema的所有数据节点
	 */
	private Set<String> buildAllDataNodes() {
		Set<String> set = new HashSet<String>();
		if (!isEmpty(dataNode)) {
			set.add(dataNode);
		}
		if (!noSharding) {
			for (TableConfig tc : tables.values()) {
				set.addAll(tc.getDataNodes());
			}
		}
		return set;
	}

	private static boolean isEmpty(String str) {
		return ((str == null) || (str.length() == 0));
	}

}