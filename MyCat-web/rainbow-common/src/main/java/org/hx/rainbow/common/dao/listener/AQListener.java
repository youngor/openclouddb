//package org.hx.rainbow.common.dao.listener;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//
//import oracle.jdbc.pool.OracleDataSource;
//import oracle.jms.AQjmsFactory;
//
//import org.apache.log4j.Logger;
//import org.hx.rainbow.common.aqjsm.OracleAqQueueFactoryBean;
//import org.hx.rainbow.common.aqjsm.OracleJsonMessageListenerContainer;
//import org.hx.rainbow.common.core.SpringApplicationContext;
//import org.hx.rainbow.common.exception.SysException;
//import org.hx.rainbow.common.util.PropertiesUtil;
//import org.springframework.beans.MutablePropertyValues;
//import org.springframework.beans.factory.config.ConstructorArgumentValues;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.jms.core.JmsTemplate;
//
//public class AQListener implements ServletContextListener{
//	public static final Logger logger = Logger.getLogger(AQListener.class);
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
//		
//		try{
//		OracleDataSource sinoerpDS = (OracleDataSource)SpringApplicationContext.getBean("sinoerpDS");
//		instanceAQ(beanFactory,sinoerpDS,jdbc,"sinoerp");
//		}catch (Exception e) {
// 			logger.error("sinoerp aq启动失败!原因:"+e.getMessage());
//		}
//		try{
//			OracleDataSource sinogzDS = (OracleDataSource)SpringApplicationContext.getBean("sinogzDS");
//			instanceAQ(beanFactory,sinogzDS,jdbc,"sinogz");
//		}catch (Exception e) {
// 			logger.error("sinogz aq启动失败!原因:"+e.getMessage());
//		}
//		try{
//			OracleDataSource sinologDS = (OracleDataSource)SpringApplicationContext.getBean("sinologDS");
//			instanceAQ(beanFactory,sinologDS,jdbc,"sinolog");
//		}catch (Exception e) {
// 			logger.error("sinolog aq启动失败!原因:"+e.getMessage());
//		}
//	}
//	
//	private void instanceAQ(DefaultListableBeanFactory beanFactory,OracleDataSource dbSource,Map<String, Object> jdbc,String dbName){
//		try{
//				logger.info("开启启动[" + dbName +"]AQ服务");
//				initAQjmsFacoryBean(beanFactory,dbSource,dbName);
//				initQueueBean(beanFactory,jdbc,dbName);
//				initJsmTemplateBean(beanFactory,dbName);
//				initListenerBean(beanFactory,dbName);
//				initContainerBean(beanFactory,dbName);
//				OracleJsonMessageListenerContainer container = (OracleJsonMessageListenerContainer)beanFactory.getBean(dbName+"jmsContainer");
//				container.start();
//				logger.info("[" + dbName +"]AQ服务启动成功");
//		}catch (Exception e) {
//			throw new SysException(dbName+e.getMessage());
//		}
//	}
//	
//	
//	private void initAQjmsFacoryBean(DefaultListableBeanFactory beanFactory,OracleDataSource dbSource,String dbName){
//		GenericBeanDefinition aqjmsFacoryDef = new GenericBeanDefinition();
//		aqjmsFacoryDef.setBeanClass(AQjmsFactory.class);
//		aqjmsFacoryDef.setFactoryMethodName("getQueueConnectionFactory");
//		ConstructorArgumentValues values = new ConstructorArgumentValues();
//		values.addIndexedArgumentValue(0, dbSource);
//		aqjmsFacoryDef.setConstructorArgumentValues(values);
//		beanFactory.registerBeanDefinition(dbName+"jmsQueueCF",aqjmsFacoryDef);
//	}
//	
//	private void initQueueBean(DefaultListableBeanFactory beanFactory,Map<String, Object> jdbc,String dbName){
//		GenericBeanDefinition queueDef = new GenericBeanDefinition();
//		Map<String, Object> original = new HashMap<String, Object>();
//		original.put("connectionFactory", beanFactory.getBean(dbName+"jmsQueueCF"));
//		original.put("oracleQueueName", "spl_queue");
//		original.put("oracleQueueUser", jdbc.get(dbName + ".jdbc.username"));
//		queueDef.setBeanClass(OracleAqQueueFactoryBean.class);
//		queueDef.setPropertyValues(new MutablePropertyValues(original));
//		beanFactory.registerBeanDefinition(dbName+"splQueue",queueDef);
//	}
//	
//	private void initJsmTemplateBean(DefaultListableBeanFactory beanFactory,String dbName){
//		GenericBeanDefinition jsmTemplateDef = new GenericBeanDefinition();
//		Map<String, Object> original = new HashMap<String, Object>();
//		original.put("connectionFactory", beanFactory.getBean(dbName + "jmsQueueCF"));
//		original.put("defaultDestination", beanFactory.getBean(dbName+"splQueue"));
//		jsmTemplateDef.setBeanClass(JmsTemplate.class);
//		jsmTemplateDef.setPropertyValues(new MutablePropertyValues(original));
//		beanFactory.registerBeanDefinition(dbName+"jmsTemplate",jsmTemplateDef);		
//	}
//	
//	private void initContainerBean(DefaultListableBeanFactory beanFactory,String dbName){
//		GenericBeanDefinition containerDef = new GenericBeanDefinition();
//		Map<String, Object> original = new HashMap<String, Object>();
//		original.put("connectionFactory", beanFactory.getBean(dbName + "jmsQueueCF"));
//		original.put("destination",  beanFactory.getBean(dbName+"splQueue"));
//		original.put("messageListener", beanFactory.getBean(dbName+"MessageListener"));
//		original.put("sessionTransacted", true);
//		containerDef.setPropertyValues(new MutablePropertyValues(original));
//		containerDef.setBeanClass(OracleJsonMessageListenerContainer.class);
//		beanFactory.registerBeanDefinition(dbName+"jmsContainer",containerDef);		 		
//	}
//	
//	private void initListenerBean(DefaultListableBeanFactory beanFactory,String dbName){
//		GenericBeanDefinition queueDef = new GenericBeanDefinition();
//		queueDef.setBeanClass(OracleMessageListener.class);
//		beanFactory.registerBeanDefinition(dbName+"MessageListener",queueDef);	
//	}
//	
//
//
//}
