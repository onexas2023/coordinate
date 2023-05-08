package onexas.coordinate.api.websocket;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import onexas.coordinate.common.lang.Streams;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public abstract class AbstractRemoteBrokerWebSocketHandler implements WebSocketHandler {

	private static final Logger logger = LoggerFactory.getLogger(AbstractRemoteBrokerWebSocketHandler.class);

	WebSocketSession remoteSession;

	AtomicBoolean clientClosed = new AtomicBoolean(false);

	protected abstract void connectToRemote(WebSocketSession clientSession, WebSocketHandler remoteBroker)
			throws Exception;

	protected WebSocketMessage<?> decorateRemoteMessage(WebSocketMessage<?> message) {
		return message;
	}

	protected WebSocketMessage<?> decorateClientMessage(WebSocketMessage<?> message) {
		return message;
	}

	public void afterConnectionEstablished(WebSocketSession clientSession) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("client-session {}:{}", clientSession.getId(), clientSession.getRemoteAddress());
		}
		
		Object sync = this;

		WebSocketHandler remoteBroker = new WebSocketHandler() {

			@Override
			public boolean supportsPartialMessages() {
				return false;
			}

			@Override
			public void handleTransportError(WebSocketSession remoteSession, Throwable exception) throws Exception {
				if(exception instanceof SSLException) {
					logger.error("remote-session {}, transport error {}:{}", remoteSession.getId(),
							exception.getClass(), exception.getMessage(), exception);
				}else {
					logger.warn("remote-session {}, transport error {}:{}", remoteSession.getId(),
						exception.getClass(), exception.getMessage());
				}
				if (logger.isDebugEnabled()) {
					logger.debug(Strings.format("remote-session {}, transport error {}", remoteSession.getId(),
							exception.getMessage()), exception);
				}
				synchronized (sync) {
					try {
						remoteSession.close(CloseStatus.SERVER_ERROR);
					}catch(Exception x) {
					}
					try {
						clientSession.close(CloseStatus.SERVER_ERROR);
					}catch(Exception x) {}
				}
			}

			@Override
			public void handleMessage(WebSocketSession remoteSession, WebSocketMessage<?> message) throws Exception {
				if (logger.isDebugEnabled()) {
					Object payload = message.getPayload();
					if (message instanceof BinaryMessage) {
						payload = new String(Streams.toByteArray(((BinaryMessage) message).getPayload()), Strings.UTF8);
					}
					logger.debug("remote-session {}, send message to client:[{}]", remoteSession.getId(), payload);
				}
				message = decorateRemoteMessage(message);
				if (message == null) {
					return;
				}
				synchronized (sync) {
					clientSession.sendMessage(message);
				}
			}

			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				if (logger.isDebugEnabled()) {
					logger.debug("remote-session {}, connect to client {}:{}", session.getId(), clientSession.getId(), clientSession.getRemoteAddress());
				}
				synchronized (sync) {
					if (clientClosed.get()) {
						session.close(CloseStatus.GOING_AWAY);
						return;
					}
					remoteSession = session;
				}
			}

			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
				if (logger.isDebugEnabled()) {
					logger.debug("remote-session {}, close connection {}", session.getId(), status);
				}
				synchronized (sync) {
					remoteSession = null;
					clientSession.close(status);
				}

			}
		};

		connectToRemote(clientSession, remoteBroker);
	}

	public synchronized void handleMessage(WebSocketSession clientSession, WebSocketMessage<?> message)
			throws Exception {
		if (logger.isDebugEnabled()) {
			Object payload = message.getPayload();
			if (message instanceof BinaryMessage) {
				payload = new String(Streams.toByteArray(((BinaryMessage) message).getPayload()), Strings.UTF8);
			}
			logger.debug("client-session {}, send message to pod:[{}]", clientSession.getId(), payload);
		}

		message = decorateClientMessage(message);
		if (message == null) {
			return;
		}
		synchronized (this) {
			if (remoteSession != null) {
				remoteSession.sendMessage(message);
			}
		}
	}

	@Override
	public synchronized void afterConnectionClosed(WebSocketSession clientSession, CloseStatus status)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("client-session {}, close connection {}", clientSession.getId(), status);
		}
		synchronized (this) {
			clientClosed.set(true);
			if (remoteSession != null) {
				remoteSession.close(status);
				remoteSession = null;
			}
		}
	}

	@Override
	public void handleTransportError(WebSocketSession clientSession, Throwable exception) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(Strings.format("client-session {}, transport error {}", clientSession.getId(),
					exception.getMessage()), exception);
		}
		synchronized (this) {
			if (remoteSession != null) {
				remoteSession.close(CloseStatus.SERVER_ERROR);
			}
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
