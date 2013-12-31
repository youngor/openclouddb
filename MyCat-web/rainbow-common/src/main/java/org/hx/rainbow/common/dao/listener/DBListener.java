//package org.hx.rainbow.common.dao.listener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//
//import oracle.jdbc.pool.OracleDataSource;
//
//import org.apache.log4j.Logger;
//import org.hx.rainbow.common.core.SpringApplicationContext;
//import org.hx.rainbow.common.util.PropertiesUtil;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.springframework.beans.MutablePropertyValues;
//import org.springframework.beans.factory.config.ConstructorArgumentValues;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//
//public class DBListener implements ServletContextListener {
//	public static final Logger logger = Logger.getLogger(DBListener.class);
//
//	@Override
//	public void contextDestroyed(ServletContextEvent arg0) {
//			
//	}
//
//	@Override
//	public void contextInitialized(ServletContextEvent arg0) {
//		Map<String, Object> jdbc = PropertiesUtil.getInstance().read("jdbc");
//		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) SpringApplicationContext
//				.getApplicationContext();
//		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext
//				.getBeanFactory();	
//		try{
//			logger.info("开始连接ERP平台[sinoerp]!");
//			getInstance(beanFactory,jdbc,"sinoerp");
//			logger.info("ERP平台[sinoerp]平台数据库连接成功!");
//		}catch (Exception e) {
//			logger.error("ERP平台[sinoerp]平台数据库连接失败!原因:" + e.getMessage());
//		}
//		try{
//			logger.info("开始连接广深平台[sinogz]!");
//			getInstance(beanFactory,jdbc,"sinogz");
//			logger.info("广深平台[sinogz]数据库连接成功!");
//		}catch (Exception e) {
//			logger.error("广深平台[sinogz]平台数据库连接失败!原因:" + e.getMessage());
//		}
//		try{
//			logger.info("开始连接国控平台[sinolog]!");;
//			getInstance(beanFactory,jdbc,"sinolog");
//			logger.info("国控平台[sinolog]平台数据库连接成功!");
//		}catch (Exception e) {
//			logger.error("国控平台[sinolog]平台数据库连接失败!原因:" + e.getMessage());
//		}
//
//	}
//	
//	private void getInstance(DefaultListableBeanFactory beanFactory,Map<String, Object> jdbc,String dbName)throws Exception{
//		beanFactory.registerBeanDefinition(dbName+"DS", getDefinition(jdbc, dbName));
//		OracleDataSource dbSource = (OracleDataSource)SpringApplicationContext.getBean(dbName+"DS");
//		dbSource.getConnection();
//		
//		if(dbSource != null){
//			beanFactory.registerBeanDefinition(dbName+"sqlSessionFactory", getSqlSessionFactoryDef(dbSource));
//			Object sqlSessionFactory = SpringApplicationContext.getBean(dbName+"sqlSessionFactory");
//			beanFactory.registerBeanDefinition(dbName+"sqlSessionTemplate", getSqlSessionTemplateDef(sqlSessionFactory));
//			beanFactory.registerBeanDefinition(dbName+"transactionManager", getTransactionManagerDef(dbSource));
//		}
//	}
//
//
//
//	private GenericBeanDefinition getDefinition(Map<String, Object> jdbc,String dbName) {
//		Properties cacheProperties = new Properties();
//		cacheProperties.setProperty("MinLimit", "5");
//		cacheProperties.setProperty("MaxLimit", "30");
//		cacheProperties.setProperty("InactivityTimeout", "3000");
//		cacheProperties.setProperty("ValidateConnection", "true");
//		
//		Properties connProperties = new Properties();
//		connProperties.setProperty("oracle.jdbc.ReadTimeout", "30000");
//		
//		GenericBeanDefinition messageSourceDefinition = new GenericBeanDefinition();
//		Map<String, Object> original = new HashMap<String, Object>();
//		original.put("URL", jdbc.get(dbName + ".jdbc.url"));
//		original.put("user", jdbc.get(dbName + ".jdbc.username"));
//		original.put("password", jdbc.get(dbName + ".jdbc.password"));
//		original.put("implicitCachingEnabled", true);
//		original.put("connectionCachingEnabled", true);
//		original.put("connectionCacheProperties", cacheProperties);
//		original.put("connectionProperties", connProperties);
//		messageSourceDefinition.setBeanClass(OracleDataSource.class);
//		messageSourceDefinition.setPropertyValues(new MutablePropertyValues(
//				original));
//		return messageSourceDefinition;
//	}
//	
//	private GenericBeanDefinition getSqlSessionFactoryDef(Object dbSource) {
//		GenericBeanDefinition sessionFactoryDef = new GenericBeanDefinition();
//		Map<String, Object> paramData = new HashMap<String, Object>();
//		paramData.put("dataSource",dbSource);
//		List<String> list = new ArrayList<String>();
//		list.add("classpath:mybatis/**/*Mapper.xml");
//		paramData.put("mapperLocations", list);
//		paramData.put("typeAliasesPackage", "org.hx.rainbow.common.dao.handler");
//		sessionFactoryDef.setBeanClass(SqlSessionFactoryBean.class);
//		sessionFactoryDef.setPropertyValues(new MutablePropertyValues(paramData));
//		return sessionFactoryDef;
//	}
//	
//	private GenericBeanDefinition getSqlSessionTemplateDef(Object sqlSessionFacotry) {
//		GenericBeanDefinition sqlSessionTemplateDef = new GenericBeanDefinition();
//		ConstructorArgumentValues values = new ConstructorArgumentValues();
//		values.addIndexedArgumentValue(0, sqlSessionFacotry);
//		sqlSessionTemplateDef.setConstructorArgumentValues(values);
//		sqlSessionTemplateDef.setBeanClass(SqlSessionTemplate.class);
//		return sqlSessionTemplateDef;
//	}
//
//	private GenericBeanDefinition getTransactionManagerDef(Object dbSource) {
//		GenericBeanDefinition transactionManagerDef = new GenericBeanDefinition();
//		Map<String, Object> paramData = new HashMap<String, Object>();
//		paramData.put("dataSource", dbSource);
//		transactionManagerDef.setPropertyValues(new MutablePropertyValues(paramData));
//		transactionManagerDef.setBeanClass(DataSourceTransactionManager.class);
//		return transactionManagerDef;
//	}
//	
//}
