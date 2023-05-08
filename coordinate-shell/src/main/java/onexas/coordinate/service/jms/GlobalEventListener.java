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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.jms.CoordinateJmsConfiguration;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "GlobalEventListener")
@Profile(Env.PROFILE_QUEUE)
public class GlobalEventListener {

	private static final Logger logger = LoggerFactory.getLogger(GlobalEventListener.class);

	@Autowired
	ApplicationEventPublisher eventPublisher;

	@JmsListener(destination = GlobalEventSender.TOPIC_NAME,
			containerFactory = CoordinateJmsConfiguration.TOPIC_LISTENER_FACTORY)
	public void receiveQueueMessage(final Message message) throws JMSException {
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
				
				if (obj instanceof GlobalEventMessage) {
					eventPublisher.publishEvent(((GlobalEventMessage)obj).getEvent());
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