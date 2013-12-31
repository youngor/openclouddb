package org.hx.rainbow.common.ddd.factory;

import org.apache.log4j.Logger;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.ddd.annotation.Aggergation;
import org.hx.rainbow.common.ddd.base.impl.FactoryBase;
import org.hx.rainbow.common.util.CglibUitl;

/**
 * 工厂管理
 * @author huangxin
 *
 */
public class FactoryManager {
	private static final Logger logger = Logger.getLogger(RepositoryFactory.class);
	private volatile static FactoryManager factoryManager = null;
	private FactoryManager(){
		
	}

	public static FactoryManager getInstance(){
		if(factoryManager == null){
			synchronized (FactoryManager.class) {
				if(factoryManager == null){
					factoryManager = new FactoryManager();
				}
			}
		}
		return factoryManager;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends FactoryBase> T getFactory(Class clazz){
		if(clazz == null) return null;
		T t = null;
		try {
			if(CglibUitl.getInstance().isCglib(clazz)){
				clazz = clazz.getSuperclass();
			}
			if(clazz.isAnnotationPresent(Aggergation.class)){
				Aggergation aggergation = (Aggergation) clazz.getAnnotation(Aggergation.class);
				Class<?> root = aggergation.root();
				if(root != null){
					clazz = root;
				}
			}
			String beanName = clazz.getSimpleName() + "Factory";
			
			t = (T) SpringApplicationContext.getBean(beanName);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			ex.printStackTrace();
		}
		return t;
	}
}
