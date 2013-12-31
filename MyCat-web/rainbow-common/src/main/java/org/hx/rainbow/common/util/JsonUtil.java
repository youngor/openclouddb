package org.hx.rainbow.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtil implements java.io.Serializable {

	private static final long serialVersionUID = -8872078079583695100L;
	private volatile static JsonUtil jsonUtil = null;

	private JsonUtil() {
	}

	public static JsonUtil getInstance() {
		if (jsonUtil == null) {
			synchronized (JsonUtil.class) {
				if (jsonUtil == null) {
					jsonUtil = new JsonUtil();
				}
			}
		}
		return jsonUtil;
	}


	public String object2JSON(Object obj,SerializerFeature serializerFeature) {
		if(obj == null){
			return "{}";
		}
		return JSON.toJSONString(obj,serializerFeature);
	}
	
	public String object2JSON(Object obj) {
		if(obj == null){
			return "{}";
		}
		return JSON.toJSONString(obj,SerializerFeature.WriteDateUseDateFormat);
	}
	

	public <T>  T json2Object(String json,Class<T> clazz) {
		if(json == null || json.isEmpty()){
			return null;
		}
		return JSON.parseObject(json, clazz);
	}
	
	public <T> T json2Reference(String json, TypeReference<T> reference){
		if(json == null || json.isEmpty()){
			return null;
		}
		return JSON.parseObject(json, reference);
	}
}