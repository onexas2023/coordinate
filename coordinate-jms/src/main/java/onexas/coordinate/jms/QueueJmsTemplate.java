package onexas.coordinate.jms;

import javax.jms.ConnectionFactory;

import org.springframework.jms.core.JmsTemplate;

/**
 * a simple jms template extension for class autowire without qualifier
 * @author Dennis Chen
 *
 */
public class QueueJmsTemplate extends JmsTemplate{

	public QueueJmsTemplate() {
		super();
	}

	public QueueJmsTemplate(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}	
}
