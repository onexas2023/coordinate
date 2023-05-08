package onexas.coordinate.service.jms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.jms.QueueJmsTemplate;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "JobEventSender")
@Profile(Env.PROFILE_QUEUE)
public class JobQueueEventSender {
	
	static final String QUEUE_NAME = "coordinate.jobEvent.queue";
	
	@Autowired
	QueueJmsTemplate queueJmsTemplate;

	public void sendQueue(JobQueueEventMessage message) {
		// Caused by: javax.jms.JMSException: Failed to build body from content.
		// Serializable class not available to broker. Reason:
		// java.lang.ClassNotFoundException: Forbidden class
		// onexas.coordinate.service.jms.JobEventMessage! This class is not trusted to
		// be serialized as ObjectMessage payload. Please take a look at
		// http://activemq.apache.org/objectmessage.html for more information on how to
		// configure trusted classes.

		// don't use object drectly, it needs extra conf on active mq.
//		queueJmsTemplate.convertAndSend("coordinate.job.queue", message);

		try {
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(message);

				queueJmsTemplate.convertAndSend(QUEUE_NAME, os.toByteArray());
			}
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}