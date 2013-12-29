package com.talent.balance.common;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/**
 * 对第三方json操作库的一些简单封装
 * 
 * @filename: JsonWrap.java
 * @copyright: Copyright (c)2010
 * @company: talent
 * @author: 谭耀武
 * @version: 1.0
 * @create time: 2010-4-11 上午09:38:53
 * @record <table cellPadding="3" cellSpacing="0" style="width:600px">
 *         <thead style="font-weight:bold;background-color:#e3e197">
 *         <tr>
 *         <td>date</td>
 *         <td>author</td>
 *         <td>version</td>
 *         <td>description</td>
 *         </tr>
 *         </thead> <tbody style="background-color:#ffffeb">
 *         <tr>
 *         <td>2010-4-11</td>
 *         <td>谭耀武</td>
 *         <td>1.0</td>
 *         <td>create</td>
 *         </tr>
 *         </tbody>
 *         </table>
 */
public abstract class JsonWrap {
	private static Logger log = LoggerFactory.getLogger(JsonWrap.class);

	
	private static SerializeConfig mapping = new SerializeConfig();
	static {
		mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
		mapping.put(java.sql.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
		mapping.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Time.class, new SimpleDateFormatSerializer("HH:mm:ss"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//		intArr.getClass().isArray();
		//		JsonWrap.jsonStringToBean_2("y", String.class);

		int[] intArr3 = new int[] { 4, 2 };

		String jsonString = JsonWrap.toJson(intArr3);
		System.out.println(jsonString);

		int[] xx = JsonWrap.toBean(jsonString, int[].class);
		System.out.println(xx);
	}

	/**
	 * 将json字符串，转换成java bean
	 * @param <T>
	 * @param jsonString
	 * @param t
	 * @return
	 */
	public static <T> T toBean(String jsonString, Class<T> tt) {
		try {
			if (StringUtils.isBlank(jsonString)){
				return null;
			}
			
			T t = JSON.parseObject(jsonString, tt);
			return t;
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 * 将对象转换成json字符串
	 * 
	 * @param bean
	 * @return
	 */
	public static String toJson(Object bean) {
		try {
			return JSON.toJSONString(bean, mapping, SerializerFeature.DisableCircularReferenceDetect);
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 */
	public JsonWrap() {

	}

}
