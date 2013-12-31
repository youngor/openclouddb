package org.hx.rainbow.common.ddd.factory;


import org.apache.log4j.Logger;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.ddd.annotation.Aggergation;
import org.hx.rainbow.common.ddd.base.impl.RepositoryBase;
import org.hx.rainbow.common.util.CglibUitl;

/**
 * 仓储实现工厂
 * 
 * @author huangxin
 * 
 */
public class RepositoryFactory {
	private static final Logger logger = Logger.getLogger(RepositoryFactory.class);
	
	private volatile static RepositoryFactory repositoryFactory = null;
	public static final String DEFAULT_REPOSITORY_NAME = "defaultRepository";
	
	private RepositoryFactory(){
		
	}

	public static RepositoryFactory getInstance(){
		if(repositoryFactory == null){
			synchronized (RepositoryFactory.class) {
				if(repositoryFactory == null){
					repositoryFactory = new RepositoryFactory();
				}
			}
		}
		return repositoryFactory;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  <T extends RepositoryBase<?, ?>> T getRepository(Class clazz) {
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
			String beanName = clazz.getSimpleName() + "Repository";
			
			t = (T) SpringApplicationContext.getBean(beanName);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex.getCause());
			ex.printStackTrace();
		}
		return t;
	}
}