package org.hx.rainbow.common.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.util.JsonUtil;

import com.alibaba.fastjson.serializer.SerializerFeature;

public class RainbowContext{
	
	public RainbowContext(){
		
	}
	public RainbowContext(String serviceName,String methodName){
		this.service = serviceName;
		this.method = methodName;
	}
	
	public String toJson(SerializerFeature feature){
		return JsonUtil.getInstance().object2JSON(this, feature );
	}
	
	public String toJson(){
		return JsonUtil.getInstance().object2JSON(this, SerializerFeature.WriteDateUseDateFormat);
	}

	/**
	 * 服务名称
	 */
	private String service;
	/**
	 * 方法名称
	 */
	private String method;
	/**
	 * 服务返回消息
	 */
	private String msg;
	/**
	 * 排序
	 */
	private String orderBy;
	/**
	 * 偏移量
	 */
	private int page = 1;
	/**
	 * 每页显示记录条数
	 */
	private int limit = 10;
	/**
	 * 总共多少条
	 */
	private int total = 0;
	
	/**
	 * 服务状态
	 */
	private boolean success = true;
	
	private String sessionId;
	
	/**
	 * 属性
	 */
	private Map<String,Object> attr = new HashMap<String,Object>();
	
	/**
	 * 返回数据
	 */
	private List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
	

		
	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Map<String, Object> getAttr() {
		return attr;
	}

	public void setAttr(Map<String, Object> attr) {
		if(attr == null){
			return;
		}
		this.attr = attr;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void addAttr(String key,Object value) {
		this.attr.put(key, value);
	}
	
	public Object removeAttr(String key) {
		return this.attr.remove(key);
	}
	
	public void clearAttr() {
		 this.attr.clear();
	}
	
	public Object getAttr(String key) {
		return this.attr.get(key);
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void addRow(Map<String, Object> data) {
		this.rows.add(data);
	}
	public void addRows(List<Map<String, Object>> dataList) {
		this.rows.addAll(dataList);
	}
	
	public List<Map<String, Object>> getRows() {
		return rows;
	}
	
	public Map<String, Object> getRow(int index) {
		return rows.get(index);
	}

	public void setRows(List<Map<String, Object>> rows) {
		if(rows == null){
			return;
		}
		this.rows = rows;
	}
	
	public void clearRows() {
		rows.clear();
	}

	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}


	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}

