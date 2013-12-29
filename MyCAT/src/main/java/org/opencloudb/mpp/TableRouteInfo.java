package org.opencloudb.mpp;

import java.util.Map;
import java.util.Set;

import org.opencloudb.config.model.TableConfig;

/**
 * Table路由相关的的信息 包括分片相关字段的查询条件 关联的分片规则等
 * 
 * @author wuzhih
 * 
 */
public class TableRouteInfo {
	/**
	 * key is column name, value is set of vars. for example sql: where cola =
	 * '555' or cola in ('777','888') will be key->cala
	 * ,value->{'555','777','888'}
	 */
	public Map<String, Set<ColumnRoutePair>> columnRoutemap;
	public TableConfig matchedTable;
	// sharding rule of this table matched?
	public boolean ruleMatched;

}
