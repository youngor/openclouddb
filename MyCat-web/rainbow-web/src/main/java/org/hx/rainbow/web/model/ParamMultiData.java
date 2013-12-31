package org.hx.rainbow.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParamMultiData {
	private String service;
	private String method;
	public List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public void addData(Map<String, Object> data){
		this.data.add(data);
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
	
	
}
