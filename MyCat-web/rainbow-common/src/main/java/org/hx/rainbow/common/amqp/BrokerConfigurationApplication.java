//package org.hx.rainbow.common.amqp;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Map;
//
//import org.hx.rainbow.common.amqp.async.HelloWorldHandler;
//import org.hx.rainbow.common.amqp.core.AmqpUtil;
//import org.springframework.amqp.core.AmqpAdmin;
//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageListener;
//import org.springframework.amqp.core.BindingBuilder.GenericArgumentsConfigurer;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Exchange;
//import org.springframework.amqp.core.FanoutExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.Binding.DestinationType;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//public class BrokerConfigurationApplication {
//
//	/**
//	 * An example application that only configures the AMQP broker
//	 */
//	public static void main(String[] args) throws Exception {
//		ApplicationContext context = new ClassPathXmlApplicationContext("rabbitConfiguration.xml");
//		CachingConnectionFactory connectionFactory = context.getBean(CachingConnectionFactory.class);
//		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//		
////		Exchange exchange = new DirectExchange("direct_exchange");
////		Exchange fanoutExchange = new FanoutExchange("fanout_exchange");
////		rabbitAdmin.declareExchange(fanoutExchange);
////		Queue queue = new Queue("queue_no1");
////		rabbitAdmin.declareQueue(queue);
////		Queue queue2 = new Queue("queue_no2");
////		rabbitAdmin.declareQueue(queue2);
//		
////		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("direct_exchange.queue_no1").noargs());
//		
////		rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(fanoutExchange).with("fanout_exchange.queue_no1").noargs());
////		rabbitAdmin.declareBinding(BindingBuilder.bind(queue2).to(fanoutExchange).with("fanout_exchange.queue_no2").noargs());
//		
//		
//		RabbitTemplate rabbitTemplate = rabbitAdmin.getRabbitTemplate();
//		
////		rabbitTemplate.convertAndSend("huangxin.test","黄鑫测试一个spring-amqp!");
//		rabbitTemplate.convertAndSend("fanout_exchange","", "黄鑫测试一个spring-amqp!");
//		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//		container.setConnectionFactory(connectionFactory);
//		container.setQueueNames("queue_no1");
//		container.setMessageListener(new MessageListenerAdapter(new HelloWorldHandler()));
////		AmqpUtil.getInstance().asyncReceive("queue_no1", new HandleMessage());
//		//String test = (String)rabbitTemplate.receiveAndConvert("queue_no1");
//		//System.out.println(test);
//	}
//}
//
//
