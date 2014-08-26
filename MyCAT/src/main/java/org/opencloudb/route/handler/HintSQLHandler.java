package org.opencloudb.route.handler;

import org.opencloudb.cache.LayerCachePool;
import org.opencloudb.config.model.SchemaConfig;
import org.opencloudb.config.model.SystemConfig;
import org.opencloudb.parser.SQLParserDelegate;
import org.opencloudb.route.RouteResultset;
import org.opencloudb.route.RouteResultsetNode;
import org.opencloudb.route.ServerRouterUtil;

import com.foundationdb.sql.parser.NodeTypes;
import com.foundationdb.sql.parser.QueryTreeNode;

import java.sql.SQLNonTransientException;

/**
 * 处理注释中 类型为sql的情况 （按照 注释中的sql做路由解析，而不是实际的sql）
 */
public class HintSQLHandler implements HintHandler {


    @Override
    public RouteResultset route(SystemConfig sysConfig,
                                SchemaConfig schema, int sqlType, String realSQL, String charset,
                                Object info, LayerCachePool cachePool,String hintSQLValue)
            throws SQLNonTransientException {

        RouteResultset rrs = ServerRouterUtil.route(sysConfig, schema, sqlType, hintSQLValue,
                charset, info, cachePool);
        // 替换RRS中的SQL执行
        RouteResultsetNode[] oldRsNodes = rrs.getNodes();
        RouteResultsetNode[] newRrsNodes = new RouteResultsetNode[oldRsNodes.length];
        for (int i = 0; i < newRrsNodes.length; i++) {
            newRrsNodes[i] = new RouteResultsetNode(
                    oldRsNodes[i].getName(),
                    oldRsNodes[i].getSqlType(), realSQL);
        }
        rrs.setNodes(newRrsNodes);
        
        //判断是否为调用存储过程的SQL语句
     	QueryTreeNode ast = SQLParserDelegate.parse(realSQL, charset == null ? "utf-8" : charset);
     	if (ast.getNodeType() == NodeTypes.CALL_STATEMENT_NODE)
     	{
     		rrs.setCallStatement(true);
     	}
     	
        return rrs;
    }
}
