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

import java.sql.SQLNonTransientException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.config.model.TableConfig;
import org.opencloudb.config.model.rule.RuleAlgorithm;
import org.opencloudb.config.model.rule.RuleConfig;
import org.opencloudb.mpp.ColumnRoutePair;
import org.opencloudb.mpp.DDLParsInf;
import org.opencloudb.mpp.DDLSQLAnalyser;
import org.opencloudb.mpp.DeleteParsInf;
import org.opencloudb.mpp.DeleteSQLAnalyser;
import org.opencloudb.mpp.InsertParsInf;
import org.opencloudb.mpp.InsertSQLAnalyser;
import org.opencloudb.mpp.JoinRel;
import org.opencloudb.mpp.SelectParseInf;
import org.opencloudb.mpp.SelectSQLAnalyser;
import org.opencloudb.mpp.ShardingParseInfo;
import org.opencloudb.mpp.UpdateParsInf;
import org.opencloudb.mpp.UpdateSQLAnalyser;
import org.opencloudb.mysql.nio.handler.FetchStoreNodeOfChildTableHandler;
import org.opencloudb.parser.SQLParserDelegate;
import org.opencloudb.server.parser.ServerParse;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.CursorNode;
import com.akiban.sql.parser.DDLStatementNode;
import com.akiban.sql.parser.NodeTypes;
import com.akiban.sql.parser.QueryTreeNode;
import com.akiban.sql.parser.ResultSetNode;
import com.akiban.sql.parser.SelectNode;
import com.akiban.sql.unparser.NodeToString;

/**
 * @author mycat
 * @author mycat
 */
public final class ServerRouterUtil {
	private static final Logger LOGGER = Logger
			.getLogger(ServerRouterUtil.class);

	public static RouteResultset route(SchemaConfig schema, int sqlType,
			String stmt, String charset, Object info)
			throws SQLNonTransientException {
		stmt = stmt.trim();
		stmt = removeSchema(stmt, schema.getName());
		RouteResultset rrs = new RouteResultset(stmt, sqlType);

		// 检查schema是否含有拆分库
		if (schema.isNoSharding()) {
			return routeToSingleNode(rrs, schema.getDataNode(), stmt);
		}
		// 判断是否是show tables 之类的语句
		if (sqlType == ServerParse.SHOW) {
			return analyseShowSQL(schema, rrs, stmt);
		}

		// 判断是否是 select @@.. 之类的语句
		if (sqlType == ServerParse.SELECT && stmt.contains("@@")) {
			return analyseDoubleAtSgin(schema, rrs, stmt);
		}

		// 判断是否是元数据SQL，如describe table
		int ind = stmt.indexOf(' ');
		String firstToken = stmt.substring(0, ind).toLowerCase();
		if ("describe".startsWith(firstToken)) {
			return analyseDescrSQL(schema, rrs, stmt, ind + 1);
		}

		// 生成和展开AST
		QueryTreeNode ast = SQLParserDelegate.parse(stmt,
				charset == null ? "utf-8" : charset);
		// @micmiu 简单模糊判断SQL是否包含sequence
		if (stmt.toUpperCase().indexOf(" MYCATSEQ_") != -1) {
			try {
				NodeToString strHandler = new NodeToString();
				// 如果存在sequence 转化sequence为实际数值
				stmt = strHandler.toString(ast);
				rrs = new RouteResultset(stmt, sqlType);
				QueryTreeNode ast2 = SQLParserDelegate.parse(stmt,
						charset == null ? "utf-8" : charset);
				ast = ast2;
			} catch (StandardException e) {
				LOGGER.error(e);
			}
		}

		// Select SQL
		if (ast.getNodeType() == NodeTypes.CURSOR_NODE) {
			ResultSetNode rsNode = ((CursorNode) ast).getResultSetNode();
			if (rsNode instanceof SelectNode) {
				if (((SelectNode) rsNode).getFromList().isEmpty()) {
					// 是否是系统相关的语句，select charaset等
					return routeToSingleNode(rrs, schema.getRandomDataNode(),
							stmt);
				}
			}
			// 标准的SELECT表的操作
			SelectParseInf parsInf = new SelectParseInf();
			parsInf.ctx = new ShardingParseInfo();
			SelectSQLAnalyser.analyse(parsInf, ast);
			return tryRouteForTables(ast, true, rrs, schema, parsInf.ctx, stmt);

		} else if (ast.getNodeType() == NodeTypes.INSERT_NODE) {
			InsertParsInf parsInf = InsertSQLAnalyser.analyse(ast);
			if (parsInf.columnPairMap.isEmpty()) {
				String inf = "not supported inserq sql (columns not provided),"
						+ stmt;
				LOGGER.warn(inf);
				throw new SQLNonTransientException(inf);
			} else if (parsInf.fromQryNode != null) {
				String inf = "not supported inserq sql (multi insert)," + stmt;
				LOGGER.warn(inf);
				throw new SQLNonTransientException(inf);
			}
			TableConfig tc = getTableConfig(schema, parsInf.tableName);
			Set<ColumnRoutePair> col2Val = null;
			String partColumn = null;
			// for partition table ,partion column must provided
			if (tc.getTableType() != TableConfig.TYPE_GLOBAL_TABLE) {
				if (tc.isChildTable()) {

					String joinKeyVal = parsInf.columnPairMap.get(tc
							.getJoinKey());
					if (joinKeyVal == null) {
						String inf = "joinKey not provided :" + tc.getJoinKey()
								+ "," + stmt;
						LOGGER.warn(inf);
						throw new SQLNonTransientException(inf);
					}
					// only has one parent level and ER parent key is parent
					// table's partition key
					if (tc.isSecondLevel()
							&& tc.getParentTC().getPartitionColumn()
									.equals(tc.getParentKey())) { // using
																	// parent
																	// rule to
																	// find
																	// datanode
						Set<ColumnRoutePair> parentColVal = new HashSet<ColumnRoutePair>(
								1);
						ColumnRoutePair pair = new ColumnRoutePair(joinKeyVal);
						parentColVal.add(pair);
						Set<String> dataNodeSet = ruleCalculate(
								tc.getParentTC(), parentColVal);
						if (dataNodeSet.isEmpty() || dataNodeSet.size() > 1) {
							throw new SQLNonTransientException(
									"parent key can't find  valid datanode ,expect 1 but found: "
											+ dataNodeSet.size());
						}
						String dn = dataNodeSet.iterator().next();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("found partion node (using parent partion rule directly) for child table to insert  "
									+ dn + " sql :" + stmt);
						}
						return routeToSingleNode(rrs, dn, stmt);
					}
					String findRootTBSql = tc.getLocateRTableKeySql()
							+ joinKeyVal;
					FetchStoreNodeOfChildTableHandler fetchHandler = new FetchStoreNodeOfChildTableHandler();
					String dn = fetchHandler.execute(findRootTBSql, tc
							.getRootParent().getDataNodes());
					if (dn == null) {
						throw new SQLNonTransientException(
								"can't find (root) parent sharding node for sql:"
										+ stmt);
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("found partion node for child table to insert "
								+ dn + " sql :" + stmt);
					}
					return routeToSingleNode(rrs, dn, stmt);
				}
				partColumn = tc.getPartitionColumn();
				if (partColumn != null) {
					col2Val = new HashSet<ColumnRoutePair>(1);
					String sharindVal = parsInf.columnPairMap.get(partColumn);
					if (sharindVal != null) {
						col2Val.add(new ColumnRoutePair(sharindVal));
					} else {// must provide sharding_id when insert
						String inf = "bad insert sql (sharding column:"
								+ partColumn + " not provided," + stmt;
						LOGGER.warn(inf);
						throw new SQLNonTransientException(inf);
					}
				}
			}
			return tryRouteForTable(ast, schema, rrs, false, stmt, tc, col2Val);
		} else if (ast.getNodeType() == NodeTypes.UPDATE_NODE) {
			// todo ,child and parent tables relation column can't be updated
			UpdateParsInf parsInf = UpdateSQLAnalyser.analyse(ast);
			// check if sharding columns is updated
			TableConfig tc = getTableConfig(schema, parsInf.tableName);
			if (parsInf.columnPairMap.containsKey(tc.getPartitionColumn())) {
				throw new SQLNonTransientException(
						"partion key can't be updated " + parsInf.tableName
								+ "->" + tc.getPartitionColumn());
			}
			if (parsInf.ctx == null) {// no where condtion
				return tryRouteForTable(ast, schema, rrs, false, stmt, tc, null);

			} else if (tc.getTableType() == TableConfig.TYPE_GLOBAL_TABLE) {
				if (parsInf.ctx.tablesAndCondtions.size() > 1) {
					throw new SQLNonTransientException(
							"global table not supported multi table related update "
									+ parsInf.tableName);
				}

				return tryRouteForTables(ast, false, rrs, schema, parsInf.ctx,
						stmt);
			} else {
				return tryRouteForTables(ast, false, rrs, schema, parsInf.ctx,
						stmt);
			}

		} else if (ast.getNodeType() == NodeTypes.DELETE_NODE) {
			DeleteParsInf parsInf = DeleteSQLAnalyser.analyse(ast);
			if (parsInf.ctx != null) {
				return tryRouteForTables(ast, false, rrs, schema, parsInf.ctx,
						stmt);
			} else {
				// no where condtion
				TableConfig tc = getTableConfig(schema, parsInf.tableName);
				return tryRouteForTable(ast, schema, rrs, false, stmt, tc, null);
			}

		} else if (ast instanceof DDLStatementNode) {
			DDLParsInf parsInf = DDLSQLAnalyser.analyse(ast);
			TableConfig tc = getTableConfig(schema, parsInf.tableName);
			return routeToMultiNode(false, ast, rrs, tc.getDataNodes(), stmt);

		} else {
			LOGGER.info("TODO ,support sql type "
					+ ast.getClass().getCanonicalName() + " ," + stmt);
			return rrs;
		}

	}

	private static int[] getSpecPos(String upStmt, int start) {
		String token1 = " FROM ";
		String token2 = " IN ";
		int tabInd1 = upStmt.indexOf(token1, start);
		int tabInd2 = upStmt.indexOf(token2, start);
		if (tabInd1 > 0) {
			if (tabInd2 < 0) {
				return new int[] { tabInd1, token1.length() };
			}
			return (tabInd1 < tabInd2) ? new int[] { tabInd1, token1.length() }
					: new int[] { tabInd2, token2.length() };
		} else {
			return new int[] { tabInd2, token2.length() };
		}
	}

	private static TableConfig getTableConfig(SchemaConfig schema,
			String tableName) throws SQLNonTransientException {
		TableConfig tc = schema.getTables().get(tableName);
		if (tc == null) {
			String msg = "can't find table define in schema ,table:"
					+ tableName + " schema:" + schema.getName();
			LOGGER.warn(msg);
			throw new SQLNonTransientException(msg);
		}
		return tc;
	}

	private static int getSpecEndPos(String upStmt, int start) {
		int tabInd = upStmt.indexOf(" LIKE ", start);
		if (tabInd < 0) {
			tabInd = upStmt.indexOf(" WHERE ", start);
		}
		if (tabInd < 0) {
			return upStmt.length();
		}
		return tabInd;
	}

	private static RouteResultset analyseDoubleAtSgin(SchemaConfig schema,
			RouteResultset rrs, String stmt) throws SQLSyntaxErrorException {
		String upStmt = stmt.toUpperCase();

		int atSginInd = upStmt.indexOf(" @@");
		if (atSginInd > 0) {
			return routeToMultiNode(false, null, rrs,
					schema.getMetaDataNodes(), stmt);
		}

		return routeToSingleNode(rrs, schema.getRandomDataNode(), stmt);
	}

	private static RouteResultset analyseShowSQL(SchemaConfig schema,
			RouteResultset rrs, String stmt) throws SQLSyntaxErrorException {
		String upStmt = stmt.toUpperCase();
		int tabInd = upStmt.indexOf(" TABLES");
		if (tabInd > 0) {// show tables
			int[] nextPost = getSpecPos(upStmt, 0);
			if (nextPost[0] > 0) {// remove db info
				int end = getSpecEndPos(upStmt, tabInd);
				if (upStmt.indexOf(" FULL") > 0) {
					stmt = "SHOW FULL TABLES" + stmt.substring(end);
				} else {
					stmt = "SHOW TABLES" + stmt.substring(end);
				}
			}
			return routeToMultiNode(false, null, rrs,
					schema.getMetaDataNodes(), stmt);
		}
		// show index or column
		int[] indx = getSpecPos(upStmt, 0);
		if (indx[0] > 0) {
			// has table
			int[] repPos = { indx[0] + indx[1], 0 };
			String tableName = getTableName(stmt, repPos);
			// IN DB pattern
			int[] indx2 = getSpecPos(upStmt, indx[0] + indx[1] + 1);
			if (indx2[0] > 0) {// find LIKE OR WHERE
				repPos[1] = getSpecEndPos(upStmt, indx2[0] + indx2[1]);

			}
			stmt = stmt.substring(0, indx[0]) + " FROM " + tableName
					+ stmt.substring(repPos[1]);
			MetaRouter.routeForTableMeta(rrs, schema, tableName, stmt);
			return rrs;

		}
		return routeToSingleNode(rrs, schema.getRandomDataNode(), stmt);
	}

	private static String getTableName(String stmt, int[] repPos) {
		int startPos = repPos[0];
		int secInd = stmt.indexOf(' ', startPos + 1);
		if (secInd < 0) {
			secInd = stmt.length();
		}
		repPos[1] = secInd;
		String tableName = stmt.substring(startPos, secInd).trim();
		int ind2 = tableName.indexOf('.');
		if (ind2 > 0) {
			tableName = tableName.substring(ind2 + 1);
		}
		return tableName;
	}

	private static RouteResultset analyseDescrSQL(SchemaConfig schema,
			RouteResultset rrs, String stmt, int ind) {
		int[] repPos = { ind, 0 };
		String tableName = getTableName(stmt, repPos);
		stmt = stmt.substring(0, ind) + tableName + stmt.substring(repPos[1]);
		MetaRouter.routeForTableMeta(rrs, schema, tableName, stmt);
		return rrs;
	}

	private static RouteResultset tryRouteForTable(QueryTreeNode ast,
			SchemaConfig schema, RouteResultset rrs, boolean isSelect,
			String sql, TableConfig tc, Set<ColumnRoutePair> col2Val)
			throws SQLNonTransientException {

		if (tc.getTableType() == TableConfig.TYPE_GLOBAL_TABLE && isSelect) {
			return routeToSingleNode(rrs, tc.getRandomDataNode(), sql);
		}

		// no partion define or no where condtion for this table or no
		// partion column condtions
		if (col2Val == null || col2Val.isEmpty()) {
			if (tc.isRuleRequired()) {
				throw new IllegalArgumentException("route rule for table "
						+ tc.getName() + " is required: " + sql);
			}
			// all datanode of this table should route
			return routeToMultiNode(isSelect, ast, rrs, tc.getDataNodes(), sql);
		}
		// match table with where condtion of partion colum values
		Set<String> dataNodeSet = ruleCalculate(tc, col2Val);
		return routeToMultiNode(isSelect, ast, rrs, dataNodeSet, sql);
	}

	private static RouteResultset tryRouteForTables(QueryTreeNode ast,
			boolean isSelect, RouteResultset rrs, SchemaConfig schema,
			ShardingParseInfo ctx, String sql) throws SQLNonTransientException {
		Map<String, TableConfig> tables = schema.getTables();
		Map<String, Map<String, Set<ColumnRoutePair>>> tbCondMap = ctx.tablesAndCondtions;
		if (tbCondMap.size() == 1) {
			Map.Entry<String, Map<String, Set<ColumnRoutePair>>> entry = tbCondMap
					.entrySet().iterator().next();
			TableConfig tc = getTableConfig(schema, entry.getKey());
			return tryRouteForTable(ast, schema, rrs, isSelect, sql, tc, entry
					.getValue().get(tc.getPartitionColumn()));
		} else if (!ctx.joinList.isEmpty()) {
			for (JoinRel joinRel : ctx.joinList) {
				TableConfig rootc = schema.getJoinRel2TableMap().get(
						joinRel.joinSQLExp);
				if (rootc == null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("can't find join relation in schema "
								+ schema.getName() + " :" + joinRel.joinSQLExp
								+ " maybe global table join");
					}

				} else {
					if (rootc.getName().equals(joinRel.tableA)) {
						// table a is root table
						tbCondMap.remove(joinRel.tableB);
					} else if (rootc.getName().equals(joinRel.tableB)) {
						// table B is root table
						tbCondMap.remove(joinRel.tableA);
					} else if (tbCondMap.containsKey(rootc.getName())) {
						// contains root table in sql ,then remove all child
						tbCondMap.remove(joinRel.tableA);
						tbCondMap.remove(joinRel.tableB);
					} else {// both there A and B are not root table，remove any
							// one
						tbCondMap.remove(joinRel.tableA);
					}

				}
			}
		}

		if (tbCondMap.size() > 1) {
			LOGGER.warn("multi route tables found in this sql ,tables:"
					+ Arrays.toString(tbCondMap.keySet().toArray()) + " sql:"
					+ sql);
			Set<String> curRNodeSet = new LinkedHashSet<String>();
			String curTableName = null;
			Map<String, ArrayList<String>> globalTableDataNodesMap = new LinkedHashMap<String, ArrayList<String>>();
			for (Entry<String, Map<String, Set<ColumnRoutePair>>> e : tbCondMap
					.entrySet()) {
				String tableName = e.getKey();
				Map<String, Set<ColumnRoutePair>> col2ValMap = e.getValue();
				TableConfig tc = tables.get(tableName);
				if (tc == null) {
					String msg = "can't find table define in schema "
							+ tableName + " schema:" + schema.getName();
					LOGGER.warn(msg);
					throw new SQLNonTransientException(msg);
				} else if (tc.getTableType() == TableConfig.TYPE_GLOBAL_TABLE) {
					// add to globalTablelist
					globalTableDataNodesMap
							.put(tc.getName(), tc.getDataNodes());
					continue;
				}
				Collection<String> newDataNodes = null;
				String partColmn = tc.getPartitionColumn();
				Set<ColumnRoutePair> col2Val = partColmn == null ? null
						: col2ValMap.get(partColmn);
				if (col2Val == null || col2Val.isEmpty()) {
					if (tc.isRuleRequired()) {
						throw new IllegalArgumentException(
								"route rule for table " + tableName
										+ " is required: " + sql);
					}
					newDataNodes = tc.getDataNodes();

				} else {
					// match table with where condtion of partion colum values
					newDataNodes = ruleCalculate(tc, col2Val);
				}
				if (curRNodeSet.isEmpty()) {
					curTableName = tc.getName();
					curRNodeSet.addAll(newDataNodes);
				} else {
					if (!checkIfValidMultiTableRoute(curRNodeSet, newDataNodes)) {
						String errMsg = "invalid route in sql, " + curTableName
								+ " route to :"
								+ Arrays.toString(curRNodeSet.toArray())
								+ " ,but " + tc.getName() + " to "
								+ Arrays.toString(newDataNodes.toArray())
								+ " sql:" + sql;
						LOGGER.warn(errMsg);
						throw new SQLNonTransientException(errMsg);
					}
				}

			}
			// judge if global table contains all dataNodes of other tables
			if (!globalTableDataNodesMap.isEmpty()) {
				for (Map.Entry<String, ArrayList<String>> entry : globalTableDataNodesMap
						.entrySet()) {
					if (!entry.getValue().containsAll(curRNodeSet)) {
						String errMsg = "invalid route in sql, " + curTableName
								+ " route to :"
								+ Arrays.toString(curRNodeSet.toArray())
								+ " ,but " + entry.getKey() + " to "
								+ Arrays.toString(entry.getValue().toArray())
								+ " sql:" + sql;
						LOGGER.warn(errMsg);
						throw new SQLNonTransientException(errMsg);
					}
				}
			}
			return routeToMultiNode(isSelect, ast, rrs, curRNodeSet, sql);
		} else {// only one table
			Map.Entry<String, Map<String, Set<ColumnRoutePair>>> entry = tbCondMap
					.entrySet().iterator().next();
			TableConfig tc = getTableConfig(schema, entry.getKey());
			return tryRouteForTable(ast, schema, rrs, isSelect, sql, tc, entry
					.getValue().get(tc.getPartitionColumn()));
		}
	}

	private static boolean checkIfValidMultiTableRoute(Set<String> curRNodeSet,
			Collection<String> newNodeSet) {

		if (curRNodeSet.size() != newNodeSet.size()) {
			return false;
		} else {
			for (String dataNode : newNodeSet) {
				if (!curRNodeSet.contains(dataNode)) {
					return false;
				}
			}
		}
		return true;

	}

	private static RouteResultset routeToSingleNode(RouteResultset rrs,
			String dataNode, String stmt) {
		if (dataNode == null) {
			return rrs;
		}
		RouteResultsetNode[] nodes = new RouteResultsetNode[1];
		nodes[0] = new RouteResultsetNode(dataNode, rrs.getSqlType(), stmt);
		rrs.setNodes(nodes);
		return rrs;
	}

	private static RouteResultset routeToMultiNode(boolean isSelect,
			QueryTreeNode ast, RouteResultset rrs,
			Collection<String> dataNodes, String stmt)
			throws SQLSyntaxErrorException {
		if (isSelect) {
			String sql = SelectSQLAnalyser.analyseMergeInf(rrs, ast, true);
			if (sql != null) {
				stmt = sql;
			}
		}
		RouteResultsetNode[] nodes = new RouteResultsetNode[dataNodes.size()];
		int i = 0;
		for (String dataNode : dataNodes) {

			nodes[i++] = new RouteResultsetNode(dataNode, rrs.getSqlType(),
					stmt);
		}
		rrs.setNodes(nodes);

		return rrs;
	}

	private static class MetaRouter {
		public static void routeForTableMeta(RouteResultset rrs,
				SchemaConfig schema, String tableName, String sql) {
			String dataNode = getMetaReadDataNode(schema, tableName);
			RouteResultsetNode[] nodes = new RouteResultsetNode[1];
			nodes[0] = new RouteResultsetNode(dataNode, rrs.getSqlType(), sql);
			rrs.setNodes(nodes);
		}

		private static String getMetaReadDataNode(SchemaConfig schema,
				String table) {
			// Table名字被转化为大写的，存储在schema
			table = table.toUpperCase();
			String dataNode = null;
			Map<String, TableConfig> tables = schema.getTables();
			TableConfig tc;
			if (tables != null && (tc = tables.get(table)) != null) {
				dataNode = tc.getRandomDataNode();
			}
			return dataNode;
		}
	}

	private static String removeSchema(String stmt, String schema) {
		final String upStmt = stmt.toUpperCase();
		final String upSchema = schema.toUpperCase() + ".";
		int strtPos = 0;
		int indx = 0;
		boolean flag = false;
		indx = upStmt.indexOf(upSchema, strtPos);
		if (indx < 0) {
			StringBuilder sb = new StringBuilder("`").append(
					schema.toUpperCase()).append("`.");
			indx = upStmt.indexOf(sb.toString(), strtPos);
			flag = true;
			if (indx < 0) {
				return stmt;
			}
		}
		StringBuilder sb = new StringBuilder();
		while (indx > 0) {
			sb.append(stmt.substring(strtPos, indx));
			strtPos = indx + upSchema.length();
			if (flag) {
				strtPos += 2;
			}
			indx = upStmt.indexOf(upSchema, strtPos);
		}
		sb.append(stmt.substring(strtPos));
		return sb.toString();
	}

	/**
	 * @return dataNodeIndex -&gt; [partitionKeysValueTuple+]
	 */
	private static Set<String> ruleCalculate(TableConfig tc,
			Set<ColumnRoutePair> colRoutePairSet) {
		Set<String> routeNodeSet = new LinkedHashSet<String>();
		String col = tc.getRule().getColumn();
		RuleConfig rule = tc.getRule();
		RuleAlgorithm algorithm = rule.getRuleAlgorithm();
		for (ColumnRoutePair colPair : colRoutePairSet) {
			Integer nodeIndx = algorithm.calculate(colPair.colValue);
			if (nodeIndx == null) {
				throw new IllegalArgumentException(
						"can't find datanode for sharding column:" + col
								+ " val:" + colPair.colValue);
			} else {
				String dataNode = tc.getDataNodes().get(nodeIndx);
				routeNodeSet.add(dataNode);
				colPair.setNodeId(nodeIndx);
			}
		}
		return routeNodeSet;
	}

}
