//package org.hx.rainbow.common.amqp.core;
//
//import org.hx.rainbow.common.amqp.async.HelloWorldHandler;
//import org.hx.rainbow.common.core.SpringApplicationContext;
//import org.springframework.amqp.core.AnonymousQueue;
//import org.springframework.amqp.core.MessageListener;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.UniquelyNamedQueue;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
//
//public class AmqpUtil {
//	private final RabbitAdmin rabbitAdmin;
//	private volatile static AmqpUtil amqpUtil = null;
//
//	private AmqpUtil() {
//		this.rabbitAdmin = SpringApplicationContext.getBean(RabbitAdmin.class);
//	}
//
//	public static AmqpUtil getInstance() {
//		if (amqpUtil == null) {
//			synchronized (AmqpUtil.class) {
//				if (amqpUtil == null) {
//					amqpUtil = new AmqpUtil();
//				}
//			}
//		}
//		return amqpUtil;
//	}
//	
//	public RabbitAdmin getRabbitAdmin(){
//		return  this.rabbitAdmin;
//	}
//	
//	public ConnectionFactory getConnectionFactory(){
//		return  getTemplate().getConnectionFactory();
//	}
//	
//	public RabbitTemplate getTemplate(){
//		return  this.rabbitAdmin.getRabbitTemplate();
//	}
//	
//	public void createQueue(String queueName){
//		Queue queue = new Queue(queueName);
//		this.rabbitAdmin.declareQueue(queue);
//	}
//	
//	public void createAnonymousQueue(){
//		Queue queue = new AnonymousQueue();
//		this.rabbitAdmin.declareQueue(queue);
//	}
//	
//	public void createUniqueQueue(){
//		Queue queue = new UniquelyNamedQueue();
//		this.rabbitAdmin.declareQueue(queue);
//	}
//	
//	
//	public void send(Queue queue,String msg){
//		getTemplate().convertAndSend(queue.getName(), msg);
//	}
//
//	
//	public String receive(String queueName){
//		return (String)getTemplate().receiveAndConvert(queueName);
//	}
//	
//	public void asyncReceive(String queueName,MessageListener messageListener){
//		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//		container.setConnectionFactory(getConnectionFactory());
//		container.setQueueNames(queueName);
//		container.setMessageListener(messageListener);
//	}
//}
