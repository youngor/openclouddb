package org.opencloudb.mpp;

import java.util.Map;

import org.opencloudb.route.RouteParseInf;

import com.akiban.sql.parser.QueryTreeNode;

public class InsertParsInf extends RouteParseInf{
	/**
	 * insert table's name
	 */
		public String tableName;
		public Map<String,String> columnPairMap;
		public QueryTreeNode fromQryNode;

}
