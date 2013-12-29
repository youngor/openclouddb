package org.opencloudb.parser;

import java.sql.SQLSyntaxErrorException;

import junit.framework.Assert;

import org.junit.Test;
import org.opencloudb.mpp.DeleteParsInf;
import org.opencloudb.mpp.DeleteSQLAnalyser;
import org.opencloudb.parser.SQLParserDelegate;

import com.akiban.sql.parser.QueryTreeNode;

public class TestDeleteSQLAnalyser {
	@Test
	public void testSQL() throws SQLSyntaxErrorException {
		String sql = null;
		QueryTreeNode ast = null;
		DeleteParsInf parsInf = null;

		sql = "delete from A";
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
		parsInf = DeleteSQLAnalyser.analyse(ast);
		Assert.assertEquals("A".toUpperCase(), parsInf.tableName);
		Assert.assertNull("should no where condiont", parsInf.ctx);

		sql = "delete from A where A.id=10000";
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
		parsInf = DeleteSQLAnalyser.analyse(ast);
		Assert.assertEquals("A".toUpperCase(), parsInf.tableName);
		Assert.assertEquals(1, parsInf.ctx.tablesAndCondtions.size());

	}

}
