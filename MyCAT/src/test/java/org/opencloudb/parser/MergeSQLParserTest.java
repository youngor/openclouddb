package org.opencloudb.parser;

import java.sql.SQLSyntaxErrorException;

import junit.framework.Assert;

import org.junit.Test;
import org.opencloudb.mpp.OrderCol;
import org.opencloudb.mpp.SelectParseInf;
import org.opencloudb.mpp.SelectSQLAnalyser;
import org.opencloudb.mpp.ShardingParseInfo;
import org.opencloudb.route.RouteResultset;

import com.akiban.sql.StandardException;
import com.akiban.sql.parser.QueryTreeNode;

public class MergeSQLParserTest {
	@Test
	public void testSQL() throws SQLSyntaxErrorException, StandardException {
		SelectParseInf parsInf = new SelectParseInf();
		parsInf.ctx = new ShardingParseInfo();
		String sql = null;
		QueryTreeNode ast = null;

		// test order by parse
		sql = "select o.* from Orders o   group by o.name order by o.id asc ,o.age desc limit 5,10";
		parsInf.clear();
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);

		SelectSQLAnalyser.analyse(parsInf, ast);
		RouteResultset rrs = new RouteResultset(sql,0);
		String sql2 = SelectSQLAnalyser.analyseMergeInf(rrs, ast, true);
		Assert.assertEquals(
				"SELECT o.* FROM orders AS o GROUP BY o.name ORDER BY o.id, o.age DESC LIMIT 15 OFFSET 0",
				sql2);

		Assert.assertEquals("name", rrs.getGroupByCols()[0]);
		Assert.assertEquals(Integer.valueOf(OrderCol.COL_ORDER_TYPE_ASC), rrs.getOrderByCols().get("id"));
		Assert.assertEquals(5, rrs.getLimitStart());
		Assert.assertEquals(10, rrs.getLimitSize());

		sql = "select o.name,count(o.id) as total, max(o.mx) as maxOders,sum(MOD2(29,9)),min(o.price) from Orders o   group by name";
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
		rrs = new RouteResultset(sql,0);
		SelectSQLAnalyser.analyseMergeInf(rrs, ast, false);
		Assert.assertEquals(true, rrs.isHasAggrColumn());
		Assert.assertEquals(2, rrs.getMergeCols().size());
		sql2 = SelectSQLAnalyser.analyseMergeInf(rrs, ast, false);
		
		// aggregate column should has alias in order to used in oder by clause
		sql = "select  count(*)   from orders order by count(*) desc";
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
		rrs = new RouteResultset(sql,0);
		SQLSyntaxErrorException e=null;
		try
		{
		SelectSQLAnalyser.analyseMergeInf(rrs, ast, false);
		}catch(SQLSyntaxErrorException e1)
		{
			e=e1;
		}
		Assert.assertNotNull(e);
		Assert.assertEquals(true, rrs.isHasAggrColumn());
		
		
		
		// aggregate column should has alias in order to used in oder by clause
				sql = "select  count(*)  as total from orders order by total desc";
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
		rrs = new RouteResultset(sql,0);
		SelectSQLAnalyser.analyseMergeInf(rrs, ast, false);
		Assert.assertEquals(true, rrs.isHasAggrColumn());
		Assert.assertEquals(Integer.valueOf(OrderCol.COL_ORDER_TYPE_DESC), rrs.getOrderByCols().get("total"));

	}
}
