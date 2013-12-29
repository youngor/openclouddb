package org.opencloudb.mpp;

import java.sql.SQLSyntaxErrorException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.akiban.sql.parser.ConstantNode;
import com.akiban.sql.parser.InsertNode;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.ResultColumnList;
import com.akiban.sql.parser.ResultSetNode;
import com.akiban.sql.parser.RowResultSetNode;
import com.akiban.sql.parser.RowsResultSetNode;
import com.akiban.sql.parser.ValueNode;

/**
 * insert sql analyser
 * 
 * @author wuzhih
 * 
 */
// INSERT [LOW_PRIORITY |DELAYED| HIGH_PRIORITY] [IGNORE]
// [INTO]tbl_name[(col_name,...)] VALUES ({expr| DEFAULT},...),(...),... [ON
// DUPLICATE KEY UPDATEcol_name=expr, ... ]

// insert into table1 select * FROM table2 WHERE id not in ( select id from
// table1)

public class InsertSQLAnalyser {

	public static InsertParsInf analyse(QueryTreeNode ast)
			throws SQLSyntaxErrorException {
		InsertNode insrtNode = (InsertNode) ast;
		String targetTable = insrtNode.getTargetTableName().getTableName()
				.toUpperCase();
		InsertParsInf parsInf = new InsertParsInf();
		// must linked hash map to keep sequnce
		Map<String, String> colMap = new LinkedHashMap<String, String>();
		parsInf.columnPairMap = colMap;
		ResultColumnList columList = insrtNode.getTargetColumnList();
		String[] columnNames = null;
		if (columList != null) {
			columnNames = columList.getColumnNames();
		}
		ResultSetNode resultSetNode = insrtNode.getResultSetNode();
		if (resultSetNode instanceof RowResultSetNode) {
			RowResultSetNode rowSetNode = (RowResultSetNode) resultSetNode;
			parseInsertParams(colMap, columnNames, rowSetNode);
		} else if (resultSetNode instanceof RowsResultSetNode) {
			throw new SQLSyntaxErrorException("insert multi rows not supported");

		} else {
			parsInf.fromQryNode = resultSetNode;
		}
		parsInf.tableName = targetTable;
		return parsInf;
	}

	private static void parseInsertParams(Map<String, String> colMap,
			String[] columnNames, RowResultSetNode rowSetNode) {
		ResultColumnList colList = rowSetNode.getResultColumns();
		int size = columnNames.length;
		for (int i = 0; i < size; i++) {
			ValueNode expNode = colList.get(i).getExpression();
			if (expNode instanceof ConstantNode) {
				Object value = ((ConstantNode) expNode).getValue();
				if (value != null) {
					String colVale = value.toString();
					colMap.put(columnNames[i].toUpperCase(), colVale);
				}

				// System.out.println(columnNames[i] + " " + colVale);
			} else {
				colMap.put(columnNames[i].toUpperCase(), "?");
				System.out.println("todo column value class:"
						+ expNode.getClass().getCanonicalName());
			}

		}
	}
}
