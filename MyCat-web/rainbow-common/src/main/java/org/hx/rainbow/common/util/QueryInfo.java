package org.hx.rainbow.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询参数的封装
 * @author hx
 *
 */
public class QueryInfo<T> implements Serializable {

	private static final long serialVersionUID = 6286119125103168413L;

	private String orderBy;

	private int offset;

	private int limit;
	
	private int count;

	private Map<String,Object> param = new HashMap<String,Object>();
	
	private List<T> result = null;
	
	private QueryMode queryMode = QueryMode.BUSINESS;
	
	public  enum QueryMode {	
			/**
			 * 返回数量
			 */
		  	COUNT,
			/**
			 * 返回记录
			 */
			QUERY,
			/**
			 * 执行业务操作
			 */
			BUSINESS
	  }


	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	

	public Map<String,Object> getParam() {
		return param;
	}

	public void setParam(Map<String,Object> param) {
		this.param = param;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void setResult(List<T> list){
		this.result = list;
	}

	public List<T> getResult(){
		return this.result;
	}
	

	public void markQuery(QueryInfo.QueryMode queryMode) {
		this.queryMode = queryMode;
	}

	public QueryMode getMode() {
		return queryMode;
	}

}