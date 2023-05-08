package onexas.axes.web;

import java.io.Serializable;
import java.util.Locale;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.Jsons;

@Component(Env.NS_BEAN + "axes.SessionContext")
@Scope(scopeName = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionContext implements Serializable {
	private static final long serialVersionUID = 1L;

	Locale preferredLocale;

	// this object is no serializable
	transient Authentication authentication;
	String authenticationJson;

	public Locale getPreferredLocale() {
		return preferredLocale;
	}

	public void setPreferredLocale(Locale preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public void setAuthentication(Authentication auth) {
		authentication = auth;
		authenticationJson = auth == null ? null : Jsons.jsonify(auth);
	}

	public Authentication getAuthentication() {
		if(authentication==null) {
			if(authenticationJson!=null) {
				authentication = Jsons.objectify(authenticationJson, Authentication.class);
			}
		}
		return authentication;
	}
}
