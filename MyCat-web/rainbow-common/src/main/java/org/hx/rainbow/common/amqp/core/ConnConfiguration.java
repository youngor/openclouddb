//package org.hx.rainbow.common.amqp.core;
//
//import org.hx.rainbow.common.core.SpringApplicationContext;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//
//public class ConnConfiguration {
//	private volatile static ConnConfiguration configuration= null;
//	public final CachingConnectionFactory connectionFactory;
//	
//	private ConnConfiguration(){
//		CachingConnectionFactory connectionFactory = SpringApplicationContext.getBean(CachingConnectionFactory.class);
//		if(connectionFactory == null){
//			this.connectionFactory = new CachingConnectionFactory();
//		}else{
//			this.connectionFactory = connectionFactory;
//		}
//	}
//	
//	public static ConnConfiguration getInstance(){
//		if(configuration == null){
//			synchronized (ConnConfiguration.class) {
//				if(configuration == null){
//					configuration = new ConnConfiguration();
//				}
//			}
//		}
//		return configuration;
//	}
//	
//	public CachingConnectionFactory getConnectionFactory(){
//		return this.connectionFactory;
//	}
//}
