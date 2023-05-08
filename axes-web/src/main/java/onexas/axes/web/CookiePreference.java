package onexas.axes.web;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Base58;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "axes.CookiePreference")
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CookiePreference {
	private static final String PREF_NAME = "axes.pref";
	
	public static final int DEFAULT_PAGE_SIZE = 20;

	@Autowired
	HttpServletRequest request;

	@Autowired
	HttpServletResponse response;

	AtomicBoolean init = new AtomicBoolean(false);

	Preference preference;

	public Locale getLocale() {
		readFromRequest();
		return preference.getLocale();
	}

	public void setLocale(Locale locale) {
		readFromRequest();
		this.preference.setLocale(locale);
	}

	public String getDomain() {
		readFromRequest();
		return preference.getDomain();
	}

	public void setDomain(String domain) {
		readFromRequest();
		this.preference.setDomain(domain);
	}

	public Integer getPageSize() {
		readFromRequest();
		return preference.getPageSize();
	}

	public void setPageSize(Integer pageSize) {
		readFromRequest();
		this.preference.setPageSize(pageSize);
	}

	public void readFromRequest() {
		if (init.compareAndSet(false, true)) {

			Cookie[] cks = request.getCookies();
			if (cks != null) {
				for (Cookie ck : cks) {
					try {
						String nm = ck.getName();
						String val = ck.getValue();
						if (PREF_NAME.equals(nm)) {
							preference = Jsons.objectify(new String(Base58.doDecode(val), Strings.UTF8),
									Preference.class);
							break;
						}
					} catch (Exception x) {
						// do nothing
					}
				}
			}
		}

		stuffDefault();
	}
	
	private void stuffDefault() {
		if (preference == null) {
			preference = new Preference();
		}
		if (preference.locale == null) {
			preference.locale = Locale.getDefault();
		}
		if (preference.domain == null) {
			preference.domain = Constants.DOMAIN_LOCAL;
		}
		if (preference.pageSize == null) {
			preference.pageSize = DEFAULT_PAGE_SIZE;
		}
	}

	public void writeToResponse() {
		if (preference != null) {
			String j = Jsons.jsonify(preference);
			Cookie cookie = new Cookie(PREF_NAME, Base58.doEncode(j.getBytes(Strings.UTF8)));
			cookie.setPath("/");
			cookie.setMaxAge(Integer.MAX_VALUE);
			response.addCookie(cookie);
		}
	}

	private static class Preference {
		Locale locale;
		String domain;
		Integer pageSize;

		public Locale getLocale() {
			return locale;
		}

		public void setLocale(Locale locale) {
			this.locale = locale;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Integer getPageSize() {
			return pageSize;
		}

		public void setPageSize(Integer pageSize) {
			this.pageSize = pageSize;
		}
	}

}
