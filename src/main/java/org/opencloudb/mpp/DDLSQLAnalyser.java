package org.opencloudb.mpp;

import java.sql.SQLSyntaxErrorException;

import com.akiban.sql.parser.AlterTableNode;
import com.akiban.sql.parser.CreateIndexNode;
import com.akiban.sql.parser.CreateTableNode;
import com.akiban.sql.parser.DDLStatementNode;
import com.akiban.sql.parser.DropIndexNode;
import com.akiban.sql.parser.DropTableNode;
import com.akiban.sql.parser.QueryTreeNode;

/**
 * DDL sql analyser
 * 
 * @author wuzhih
 * 
 */

public class DDLSQLAnalyser {

	public static DDLParsInf analyse(QueryTreeNode ast)
			throws SQLSyntaxErrorException {
		DDLParsInf parsInf=new DDLParsInf();
		DDLStatementNode ddlNode = (DDLStatementNode) ast;
		String tableName=null;
		if(ddlNode instanceof CreateTableNode)
		{
			tableName=((CreateTableNode)ddlNode).getObjectName().getTableName();
		}else if(ddlNode instanceof AlterTableNode)
		{
			tableName=((AlterTableNode)ddlNode).getObjectName().getTableName();
		}else if(ddlNode instanceof DropTableNode)
		{
			tableName=((DropTableNode)ddlNode).getObjectName().getTableName();
		}else if(ddlNode instanceof CreateIndexNode)
		{
			tableName=((CreateIndexNode)ddlNode).getObjectName().getTableName();
		}else if(ddlNode instanceof DropIndexNode)
		{
			tableName=((DropIndexNode)ddlNode).getObjectName().getTableName();
		}else
		{
			throw new SQLSyntaxErrorException("stmt not supported yet: "+ddlNode.getClass().getSimpleName());
		}
		parsInf.tableName=tableName.toUpperCase();
		return parsInf;
	}
}
