package org.opencloudb.mpp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShardingParseInfo {

	public void addShardingExpr(String tableName, String columnName,
			Object value) {
		Map<String, Set<ColumnRoutePair>> tableColumnsMap = tablesAndCondtions
				.get(tableName);
		if (tableColumnsMap == null) {
			System.out
					.println("not found table name ,may be child select result "
							+ tableName);
			return;
		}
		String uperColName = columnName.toUpperCase();
		Set<ColumnRoutePair> columValues = tableColumnsMap.get(uperColName);
		if (columValues == null) {
			columValues = new LinkedHashSet<ColumnRoutePair>();
			tablesAndCondtions.get(tableName).put(uperColName, columValues);
		}
		if (value instanceof Object[]) {
			for (Object item : (Object[]) value) {
				columValues.add(new ColumnRoutePair(item.toString()));
			}
		} else {
			columValues.add(new ColumnRoutePair(value.toString()));
		}
	}

	/**
	 * if find table name ,return table name ,else retur null
	 * 
	 * @param theName
	 * @return
	 */
	public String getTableName(String theName) {
		String upperName = theName.toUpperCase();
		if (tablesAndCondtions.containsKey(upperName)) {
			return upperName;
		} else {
			upperName = tableAliasMap.get(theName);
			return (upperName == null) ? null : upperName;
		}
	}

	public void clear() {
		shardingKeySet.clear();
		tablesAndCondtions.clear();
		tableAliasMap.clear();
		joinList.clear();
	}

	public Map<String, Map<String, Set<ColumnRoutePair>>> tablesAndCondtions = new LinkedHashMap<String, Map<String, Set<ColumnRoutePair>>>();
	public Set<String> shardingKeySet = new HashSet<String>();
	public List<JoinRel> joinList = new ArrayList<JoinRel>(1);
	/**
	 * key table alias, value talbe realname;
	 */
	public Map<String, String> tableAliasMap = new LinkedHashMap<String, String>();
	

	public void addJoin(JoinRel joinRel) {
		joinList.add(joinRel);

	}
}
