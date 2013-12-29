package org.opencloudb.mpp;

import java.sql.SQLSyntaxErrorException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.DeleteNode;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.SelectNode;

/**
 * delete sql analyser
 * 
 * @author wuzhih
 * 
 */

public class DeleteSQLAnalyser {

	public static DeleteParsInf analyse(QueryTreeNode ast)
			throws SQLSyntaxErrorException {
		DeleteNode deleteNode = (DeleteNode) ast;
		String targetTable = deleteNode.getTargetTableName().getTableName()
				.toUpperCase();
		DeleteParsInf parsInf = new DeleteParsInf();
		ShardingParseInfo ctx = null;
		parsInf.tableName = targetTable;
		SelectNode selNode = (SelectNode) deleteNode.getResultSetNode();
		if (selNode.getWhereClause() != null) {
			// anlayse where condition
			if (ctx == null) {
				ctx = new ShardingParseInfo();
				parsInf.ctx = ctx;
			}
			Map<String, Set<ColumnRoutePair>> tableCondMap = new LinkedHashMap<String, Set<ColumnRoutePair>>();
			ctx.tablesAndCondtions.put(targetTable, tableCondMap);
			try {
				SelectSQLAnalyser.analyseWhereCondition(parsInf, false,
						targetTable, selNode.getWhereClause());
			} catch (StandardException e) {
				throw new SQLSyntaxErrorException(e);
			}
			parsInf.ctx = ctx;
		}
		return parsInf;
	}

}
