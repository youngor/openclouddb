package org.hx.rainbow.common.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringApplicationContext.context = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext()
			throws BeansException {
		return context;
	}

	public static Object getBean(String beanId) {
		if (beanId == null || beanId.length() == 0) {
			return null;
		}
		Object object = null;
		object = context.getBean(beanId);
		return object;
	}
	
	public static  <T> T getBean(Class<T> clazz) {
		if (clazz == null ) {
			return null;
		}
		return context.<T>getBean(clazz);
	}
	
	
	
}
