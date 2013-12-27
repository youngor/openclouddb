package org.opencloudb.parser;

import java.sql.SQLSyntaxErrorException;

import junit.framework.Assert;

import org.junit.Test;
import org.opencloudb.mpp.DDLParsInf;
import org.opencloudb.mpp.DDLSQLAnalyser;
import org.opencloudb.parser.SQLParserDelegate;

import com.akiban.sql.parser.QueryTreeNode;

public class TestDDLSQLAnalyser {
	@Test
	public void testSQL() throws SQLSyntaxErrorException {
		String sql = null;
		QueryTreeNode ast = null;
		DDLParsInf parsInf = null;

		sql = "CREATE TABLE Persons ( Id_P int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
		parsInf = DDLSQLAnalyser.analyse(ast);
		Assert.assertEquals("Persons".toUpperCase(), parsInf.tableName);
		
//		sql = "CREATE TABLE \"Persons\" ( \"Id_P\" int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))";
//		ast = SQLParserDelegate.parse(sql, SQLParserDelegate.DEFAULT_CHARSET);
//		parsInf = DDLSQLAnalyser.analyse(ast);
//		Assert.assertEquals("Persons".toUpperCase(), parsInf.tableName);
		
	
	}
	
}
