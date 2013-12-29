package org.opencloudb.mpp;

import java.util.List;

import org.opencloudb.route.RouteParseInf;

import com.akiban.sql.parser.GroupByList;
import com.akiban.sql.parser.NumericConstantNode;
import com.akiban.sql.parser.OrderByList;
import com.akiban.sql.parser.ResultColumn;

public class SelectParseInf extends RouteParseInf {
	public ShardingParseInfo ctx;
    public OrderByList orderByList;
    public GroupByList groupByList;
    public List<ResultColumn> aggregatColumns; 
    public NumericConstantNode offsetNode;
    public NumericConstantNode offCountNode;
	public void clear() {
		super.clear();
		orderByList=null;
		groupByList=null;
		aggregatColumns=null;
		if (ctx != null) {
			ctx.clear();
		}
	}
}
