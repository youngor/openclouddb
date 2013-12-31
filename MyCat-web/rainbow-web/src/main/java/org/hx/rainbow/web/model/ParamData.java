package org.hx.rainbow.web.model;

import java.util.HashMap;
import java.util.Map;

public class ParamData {
	private String service;
	private String method;
	
	private int page = 1;
	private int rows = 10;
	
	/**
	 * 属性
	 */
	private Map<String,Object> attr = new HashMap<String,Object>();

	public Map<String, Object> getAttr() {
		return attr;
	}

	public void setAttr(Map<String, Object> attr) {
		this.attr = attr;
	}

	public Object get(String id) {
		return this.attr.get(id);
	}
	
	public void put(String key,Object value) {
		 this.attr.put(key, value);
	}

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

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
}
