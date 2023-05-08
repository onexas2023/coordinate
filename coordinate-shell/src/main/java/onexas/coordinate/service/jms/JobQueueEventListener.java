package onexas.coordinate.service.jms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.util.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.jms.CoordinateJmsConfiguration;
import onexas.coordinate.service.JobExService;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "JobEventListener")
//only job node can get job message
@Profile({ Env.PROFILE_JOB_NODE} )
public class JobQueueEventListener {

	private static final Logger logger = LoggerFactory.getLogger(JobQueueEventListener.class);

	@Autowired
	JobExService service;

	@JmsListener(destination = JobQueueEventSender.QUEUE_NAME,
			containerFactory = CoordinateJmsConfiguration.QUEUE_LISTENER_FACTORY)
	public void receiveQueueMessage(final Message message) throws JMSException {
		/*if (message instanceof ObjectMessage) {
			ObjectMessage objectMessage = (ObjectMessage) message;
			Object obj = objectMessage.getObject();
			if (obj instanceof JobEventMessage) {
				service.onReceive((JobEventMessage) obj);
			} else {
				logger.debug("unsupported message object type " + obj == null ? null : obj.getClass().getName());
			}
		} else */
		if (message instanceof BytesMessage) {
			BytesMessage bytesMessage = (BytesMessage) message;

			Object obj;
			try {
				byte[] data;
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					byte[] buffer = new byte[1024];
					int r;
					while ((r = bytesMessage.readBytes(buffer)) >= 0) {
						os.write(buffer, 0, r);
					}
					data = os.toByteArray();
				}

				try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
					ObjectInputStream ois = new ObjectInputStream(is);
					obj = ois.readObject();
				}
				
				if (obj instanceof JobQueueEventMessage) {
					service.onReceive((JobQueueEventMessage) obj);
				} else {
					logger.warn("unsupported message object type " + obj.getClass().getName());
				}
				
			} catch (IOException x) {
				//a wrong message?
				logger.warn(x.getMessage());
			} catch (ClassNotFoundException x) {
				logger.error(x.getMessage(), x);
			}
		} else {
			logger.warn("unsupported message type " + message.getClass().getName());
		}
	}
}