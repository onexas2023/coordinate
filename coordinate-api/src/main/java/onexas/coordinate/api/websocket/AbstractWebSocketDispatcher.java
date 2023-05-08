package onexas.coordinate.api.websocket;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.service.InfoCacheService;

/**
 * 
 * @author Dennis Chen
 *
 */
public abstract class AbstractWebSocketDispatcher implements WebSocketHandler {
	
	public static final String BASE_URI = "/websocket";

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSocketDispatcher.class);

	Map<String, WebSocketHandler> handlers = Collections.synchronizedMap(new LinkedHashMap<>());
	
	protected abstract String getEntryUri();
	protected abstract WebSocketHandler createHandler(WebSocketSession clientSession, String connectionInfo);

	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		URI uri = session.getUri();

		String prefix = getEntryUri() + "/";

		String path = uri.getPath();
		if (!path.startsWith(prefix)) {
			logger.debug("wrong path prefix {}", path);
			session.close(CloseStatus.NOT_ACCEPTABLE);
			return;
		}
		String token = path.substring(prefix.length());

		String info = AppContext.bean(InfoCacheService.class).acquire(token, true);
		if (info == null) {
			logger.debug("connection info not found by token  {}", token);
			session.close(CloseStatus.BAD_DATA);
			return;
		}

		try {
			WebSocketHandler delegator = createHandler(session, info);

			handlers.put(session.getId(), delegator);

			delegator.afterConnectionEstablished(session);

		} catch (RuntimeException x) {
			logger.error(x.getMessage(), x);
			session.close(CloseStatus.SERVER_ERROR);
			return;
		}
	}

	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		WebSocketHandler handler = handlers.get(session.getId());
		if (handler == null) {
			session.close(CloseStatus.GOING_AWAY);
		} else {
			handler.handleMessage(session, message);
		}
	}

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		WebSocketHandler handler = handlers.get(session.getId());
		if (handler == null) {
			session.close(CloseStatus.GOING_AWAY);
		} else {
			handler.handleTransportError(session, exception);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		WebSocketHandler handler = handlers.get(session.getId());
		if (handler != null) {
			handlers.remove(session.getId());
			handler.afterConnectionClosed(session, status);
		}
	}

	public boolean supportsPartialMessages() {
		return false;
	}
}
