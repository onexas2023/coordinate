package onexas.axes.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "axes.RequestContext")
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {
	Map<String, Object> attributes;

	public Object getAttribute(String key) {
		return attributes == null ? null : attributes.get(key);
	}

	public boolean hasAttribute(String key) {
		return attributes == null ? false : attributes.containsKey(key);
	}

	public void setAttribute(String key, Object value) {
		if (attributes == null) {
			attributes = new LinkedHashMap<>();
		}
		attributes.put(key, value);
	}

	public void removeAttribute(String key) {
		if (attributes != null) {
			attributes.remove(key);
		}
	}
}
