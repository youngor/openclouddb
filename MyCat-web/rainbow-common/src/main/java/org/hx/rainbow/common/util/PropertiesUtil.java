package org.hx.rainbow.common.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 读取.properties配置文件的内容至Map中。
 * @author huangxin
 *
 */
public class PropertiesUtil {


	protected final static Log logger = LogFactory.getLog(PropertiesUtil.class);
	
	public static Map<String,Object> map = new HashMap<String,Object>();
	
	private static PropertiesUtil propertiesUtil = null;
	private PropertiesUtil(){
		
	}
	
	public static PropertiesUtil getInstance(){
		if(propertiesUtil == null){
			synchronized (PropertiesUtil.class) {
				if(propertiesUtil == null){
					propertiesUtil =  new PropertiesUtil();
				}
			}
		}
		return propertiesUtil;
	}
	

	
	/**
	 * 读取.properties配置文件的内容至Map中
	 * @param propertiesFile
	 * @return
	 */
	public  Map<String,Object> read(String propertiesFile) {
		
		@SuppressWarnings("unchecked")
		Map<String,Object> maps = (Map<String, Object>) map.get(propertiesFile);
		if(maps == null){
			maps = new HashMap<String,Object>();
			ResourceBundle rb = ResourceBundle.getBundle(propertiesFile);
			Enumeration<String> enu = rb.getKeys();
			while (enu.hasMoreElements()) {
				String obj = enu.nextElement();
				Object objv = rb.getObject(obj);
				maps.put(obj, objv);
			}
			map.put(propertiesFile, maps);
		}
		return maps;
	}
	


}