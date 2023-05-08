package onexas.coordinate.jms;

import java.util.concurrent.Executor;

import javax.jms.ConnectionFactory;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateJmsConfiguration")
@EnableJms
@PropertySource("classpath:coordinate-jms.properties")
public class CoordinateJmsConfiguration {

	public static final String TOPIC_LISTENER_FACTORY = Env.NS_BEAN + "topicListenerFactory";
	public static final String QUEUE_LISTENER_FACTORY = Env.NS_BEAN + "queueListenerFactory";
	public static final String TOPIC_JMS_TEMPLATE = Env.NS_BEAN + "topicJmsTemplate";
	public static final String QUEUE_JMS_TEMPLATE = Env.NS_BEAN + "queueJmsTemplate";

	@Bean(name = TOPIC_LISTENER_FACTORY)
	public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		
		factory.setTaskExecutor((Executor)AppContext.bean(AppContext.TASK_EXECUTOR_NAME));
		
		configurer.configure(factory, connectionFactory);
		// NOTE:MUST after configure
		factory.setPubSubDomain(true);

		return factory;
	}

	@Bean(name = QUEUE_LISTENER_FACTORY)
	public JmsListenerContainerFactory<?> queueListenerFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		
		factory.setTaskExecutor((Executor)AppContext.bean(AppContext.TASK_EXECUTOR_NAME));
		
		configurer.configure(factory, connectionFactory);
		// NOTE:MUST after configure
		factory.setPubSubDomain(false);

		return factory;
	}

	@Bean(name = QUEUE_JMS_TEMPLATE)
	public QueueJmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory) {
		QueueJmsTemplate template = new QueueJmsTemplate(connectionFactory);
		template.setPubSubDomain(false);
		return template;
	}

	@Bean(name = TOPIC_JMS_TEMPLATE)
	public TopicJmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory) {
		TopicJmsTemplate template = new TopicJmsTemplate(connectionFactory);
		template.setPubSubDomain(true);
		return template;
	}

}
