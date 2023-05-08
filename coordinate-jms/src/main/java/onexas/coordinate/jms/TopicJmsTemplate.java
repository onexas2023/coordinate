package onexas.coordinate.jms;

import javax.jms.ConnectionFactory;

import org.springframework.jms.core.JmsTemplate;

/**
 * a simple jms template extension for class autowire without qualifier
 * @author Dennis Chen
 *
 */
public class TopicJmsTemplate extends JmsTemplate{

	public TopicJmsTemplate() {
		super();
	}

	public TopicJmsTemplate(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}	
}
