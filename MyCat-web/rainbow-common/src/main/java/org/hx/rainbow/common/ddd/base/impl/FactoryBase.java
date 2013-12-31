package org.hx.rainbow.common.ddd.base.impl;



import java.util.Map;

import org.apache.log4j.Logger;
import org.hx.rainbow.common.ddd.factory.EntityFactory;
import org.hx.rainbow.common.ddd.model.Entity;
import org.hx.rainbow.common.util.JavaBeanUtil;

public abstract class FactoryBase {
	private static final Logger logger = Logger.getLogger(FactoryBase.class);
	
	public <T extends Entity<?>> T createEntity(Class<T> clazz,Map<String,Object> dataMap){
		T t = null;
		try {
			t = EntityFactory.getInstance().getEntity(clazz);
			if(dataMap != null){
				JavaBeanUtil.map2bean(t, dataMap, true);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			e.printStackTrace();
		} 
		return t;
	}
}
