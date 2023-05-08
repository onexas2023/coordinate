package onexas.axes.web;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;

import okhttp3.OkHttpClient;
import onexas.api.invoker.ApiClient;
import onexas.coordinate.api.v1.sdk.CoordinateMetainfoApi;
import onexas.coordinate.api.v1.sdk.CoordinateSettingApi;
import onexas.coordinate.api.v1.sdk.model.Authentication;
import onexas.coordinate.api.v1.sdk.model.UMetainfo;
import onexas.coordinate.api.v1.sdk.model.UPrincipalPermission;
import onexas.coordinate.api.v1.sdk.model.USetting;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.app.RefreshableConfigLoader;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Locales;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.CalendarHelper;
import onexas.coordinate.common.util.ValueI;

@Component(Env.NS_BEAN + "axes.Workspace")
@Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Workspace {

	@Value("${axes.contextName}")
	String contextName;

	@Value("${coordinate.api-base-path}")
	String apiBasePath;

	@Value("${coordinate.api-disable-ssl-verify:false}")
	Boolean apiDisableSslVerify;

	@Value("${coordinate.api-connect-timeout:30s}")
	String apiConnectTimeout;

	@Value("${coordinate.api-read-timeout:1m}")
	String apiReadTimeout;

	@Value("${coordinate.api-write-timeout:30s}")
	String apiWriteTimeout;

	@Value("${coordinate.api-proxy-host:#{null}}")
	String apiProxyHost;

	@Value("${coordinate.api-proxy-port:#{null}}")
	Integer apiProxyPort;

	@Autowired
	HttpServletRequest request;

	@Autowired
	SessionContext session;

	@Autowired
	CookiePreference cookiePreference;

	UMetainfo Metainfo;
	USetting setting;

	DateFormat preferredDateTimeTimeZoneFormat;
	DateFormat preferredDateTimeFormat;
	DateFormat preferredDateFormat;
	DateFormat preferredTimeFormat;
	TimeZone preferredTimeZone;

	private static final RefreshableConfigLoader<Set<Locale>> supportedLocales = new RefreshableConfigLoader<Set<Locale>>() {

		@Override
		protected Set<Locale> load(Config cfg) {
			Set<Locale> locales = new LinkedHashSet<Locale>();
			List<String> ls = cfg.getStringList("axes.supportedLocale", Collections.asList("zh_TW", "en_US"));
			for (String l : ls) {
				locales.add(Locales.getLocale(l));
			}
			return locales;
		}
	};

	public Set<Locale> getSupportedLocales() {
		return supportedLocales.load();
	}

	public Locale getPreferredLocale() {
		Locale locale = session.getPreferredLocale();
		if (locale != null) {
			return locale;
		}

		locale = cookiePreference.getLocale();

		Set<Locale> ls = getSupportedLocales();
		if (!ls.contains(locale)) {
			locale = ls.iterator().next();
		}

		session.setPreferredLocale(locale);

		if (!locale.equals(cookiePreference.getLocale())) {
			cookiePreference.writeToResponse();
		}
		syncZKLocale(locale);

		return locale;
	}

	private void syncZKLocale(Locale locale) {
		// sync to zk's locale
		org.zkoss.web.servlet.Charsets.setPreferredLocale(request.getSession(), locale);
		org.zkoss.util.Locales.setThreadLocal(locale);
	}

	public void setPreferredLocale(Locale locale) {
		if (!locale.equals(cookiePreference.getLocale())) {
			cookiePreference.setLocale(locale);
			cookiePreference.writeToResponse();
		}
		session.setPreferredLocale(locale);
		syncZKLocale(locale);
	}

	public boolean isAuthroized() {
		return session.getAuthentication() != null;
	}

	public ApiClient getApiClient() {
		ApiClient apiClient = new ApiClient();

		if (apiProxyHost != null && apiProxyPort != null) {
			OkHttpClient httpClient = apiClient.getHttpClient();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			Collections.stream(httpClient.networkInterceptors()).forEach((e) -> {
				builder.addNetworkInterceptor(e);
			});
			builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(apiProxyHost, apiProxyPort)));
			httpClient = builder.build();
			apiClient.setHttpClient(httpClient);
		}

		apiClient.setBasePath(apiBasePath);

		// current api client has hostnameVerifier NPE when setting verifyingSsl to true
		// or set any sslCaCert and keyManagers
		// so we can only set on the case of sslVerfiy sets to false
		if (Boolean.TRUE.equals(apiDisableSslVerify)) {
			apiClient.setVerifyingSsl(false);
		}
		Authentication auth = session.getAuthentication();
		apiClient.setConnectTimeout(Strings.parseMillisecond(apiConnectTimeout).intValue());
		apiClient.setReadTimeout(Strings.parseMillisecond(apiReadTimeout).intValue());
		apiClient.setWriteTimeout(Strings.parseMillisecond(apiWriteTimeout).intValue());

		if (auth != null) {
			apiClient.setApiKey(auth.getToken());
		}

		return apiClient;
	}

	public String getPreferredDomain() {
		String domain = cookiePreference.getDomain();
		return domain == null ? Constants.DOMAIN_LOCAL : domain;
	}

	public void setPreferredDomain(String domain) {
		if (!domain.equals(cookiePreference.getDomain())) {
			cookiePreference.setDomain(domain);
			cookiePreference.writeToResponse();
		}
	}

	public void setAuthentication(Authentication auth) {
		session.setAuthentication(auth);
	}

	public Authentication getAuthentication() {
		return session.getAuthentication();
	}

	public String getMainPage() {
		return "/" + contextName;
	}

	public String getContextName() {
		return contextName;
	}

	public String getLoginPage() {
		return "/login";
	}

	public void changePage(String page, String... args) {
		EventQueues.lookup(Constants.EVENT_QUEUE_INTERNAL).publish(new OnChangePageEvent(page, args));
		// to kick not alive subscribe after change page
		EventQueues.lookup(Constants.EVENT_QUEUE_INTERNAL).publish(new Event("onKick"));
	}

	public void changeArgs(boolean refresh, String... args) {
		EventQueues.lookup(Constants.EVENT_QUEUE_INTERNAL).publish(new OnChangeArgsEvent(refresh, args));
	}

	/**
	 * check if the permissionRequest expression match the current user's permission
	 * 
	 * @param permissionRequest the express of permission to check by
	 *                          'function:action1:action2:...'
	 * @return true if the permission check passed
	 */
	public boolean hasPermission(String permissionRequest) {
		if (Strings.isBlank(permissionRequest)) {
			return true;
		}
		Authentication auth = session.getAuthentication();
		if (auth != null) {
			List<UPrincipalPermission> ppl = auth.getPermissions();
			if (ppl == null) {
				return false;
			}
			// function:action1:action2:...;
			String[] prarr = permissionRequest.split(":");
			for (UPrincipalPermission pp : ppl) {
				if ("*".equals(pp.getTarget()) || pp.getTarget().equals(prarr[0])) {
					if ("*".equals(pp.getAction())) {
						return true;
					}
					for (int i = 1; i < prarr.length; i++) {
						if ("*".equals(prarr[i]) || pp.getAction().equals(prarr[i])) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static final class OnChangePageEvent extends Event {
		private static final long serialVersionUID = 1L;
		String page;
		String[] args;

		public OnChangePageEvent(String page, String... args) {
			super("OnChangePageEvent");
			this.page = page;
			this.args = args;
		}

		public String getPageName() {
			return page;
		}

		public String[] getArgs() {
			return args;
		}
	}

	public static final class OnChangeArgsEvent extends Event {
		private static final long serialVersionUID = 1L;
		boolean refresh;
		String[] args;

		public OnChangeArgsEvent(boolean refresh, String... args) {
			super("OnChangeArgsEvent");
			this.refresh = refresh;
			this.args = args;
		}

		public String[] getArgs() {
			return args;
		}

		public boolean isRefresh() {
			return refresh;
		}
	}

	public static final class OnSubscribeTimeoutEvent extends Event {
		private static final long serialVersionUID = 1L;

		int timeout;
		transient EventListener<Event> listener;
		transient SubscribeAlive alive;

		public OnSubscribeTimeoutEvent(int timeout, EventListener<Event> listener, SubscribeAlive alive) {
			super("OnSubscribeTimeoutEvent");
			this.timeout = timeout;
			this.listener = listener;
			this.alive = alive;
		}

		public int getTimeout() {
			return timeout;
		}

		public EventListener<Event> getListener() {
			return listener;
		}

		public SubscribeAlive getAlive() {
			return alive;
		}

	}

	public static final class OnUnsubscribeTimeoutEvent extends Event {
		private static final long serialVersionUID = 1L;

		int timeout;
		transient EventListener<Event> listener;

		public OnUnsubscribeTimeoutEvent(int timeout, EventListener<Event> listener) {
			super("OnUnsubscribeTimeoutEvent");
			this.timeout = timeout;
			this.listener = listener;
		}

		public int getTimeout() {
			return timeout;
		}

		public EventListener<Event> getListener() {
			return listener;
		}

	}

	public static final class OnTimeoutEvent extends Event {
		private static final long serialVersionUID = 1L;

		int timeout;

		public OnTimeoutEvent(int timeout) {
			super("OnTimeoutEvent");
			this.timeout = timeout;
		}

		public int getTimeout() {
			return timeout;
		}
	}

	public int getPreferedPageSize() {
		Integer pageSize = cookiePreference.getPageSize();
		return pageSize == null ? CookiePreference.DEFAULT_PAGE_SIZE : pageSize.intValue();
	}

	public void publish(Event event) {
		EventQueues.lookup(Constants.EVENT_QUEUE_WORKSPACE).publish(event);
	}

	public static interface SubscribeAlive {
		boolean alive();
	}

	public void subscribe(EventListener<Event> listener) {
		EventQueues.lookup(Constants.EVENT_QUEUE_WORKSPACE).subscribe(listener);
	}

	public void unsubscribe(EventListener<Event> listener) {
		EventQueues.lookup(Constants.EVENT_QUEUE_WORKSPACE).unsubscribe(listener);
	}

	public void subscribe(EventListener<Event> listener, SubscribeAlive alive) {
		final ValueI<EventListener<Event>> v = new ValueI<EventListener<Event>>();
		EventListener<Event> l = new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (!alive.alive()) {
					EventQueues.lookup(Constants.EVENT_QUEUE_WORKSPACE).unsubscribe(v.getValue1());
				} else {
					listener.onEvent(event);
				}
			}
		};
		v.setValue1(l);
		EventQueues.lookup(Constants.EVENT_QUEUE_WORKSPACE).subscribe(l);
	}

	public void subscribeTimout(int timeout, EventListener<Event> listener, SubscribeAlive alive) {
		EventQueues.lookup(Constants.EVENT_QUEUE_INTERNAL)
				.publish(new OnSubscribeTimeoutEvent(timeout, listener, alive));
	}

	public void unsubscribeTimeout(int timeout, EventListener<Event> listener) {
		EventQueues.lookup(Constants.EVENT_QUEUE_INTERNAL).publish(new OnUnsubscribeTimeoutEvent(timeout, listener));
	}

	public UMetainfo getMetainfo() {
		if (Metainfo == null) {
			CoordinateMetainfoApi api = new CoordinateMetainfoApi(getApiClient());
			Metainfo = api.getMetainfo();
		}
		return Metainfo;
	}

	public USetting getSetting() {
		if (setting == null) {
			CoordinateSettingApi api = new CoordinateSettingApi(getApiClient());
			setting = api.getSetting();
		}
		return setting;
	}

	public DateFormat getPreferredDateTimeFormat() {
		if (preferredDateTimeFormat != null) {
			return preferredDateTimeFormat;
		}
		SimpleDateFormat f = new SimpleDateFormat(getPreferredDateTimePattern(), getPreferredLocale());
		f.setTimeZone(getPreferredTimeZone());
		return preferredDateTimeFormat = f;
	}

	public DateFormat getPreferredDateTimeTimeZoneFormat() {
		if (preferredDateTimeTimeZoneFormat != null) {
			return preferredDateTimeTimeZoneFormat;
		}
		SimpleDateFormat f = new SimpleDateFormat(getPreferredDateTimeTimeZonePattern(), getPreferredLocale());
		f.setTimeZone(getPreferredTimeZone());
		return preferredDateTimeTimeZoneFormat = f;
	}

	public DateFormat getPreferredDateFormat() {
		if (preferredDateFormat != null) {
			return preferredDateFormat;
		}
		SimpleDateFormat f = new SimpleDateFormat(getPreferredDatePattern(), getPreferredLocale());
		f.setTimeZone(getPreferredTimeZone());
		return preferredDateFormat = f;
	}

	public String getPreferredDateTimePattern() {
		return "yyyy/MM/dd HH:mm:ss";
	}

	public String getPreferredDateTimeTimeZonePattern() {
		return getPreferredDateTimePattern() + " Z";
	}

	public String getPreferredDatePattern() {
		return "yyyy/MM/dd";
	}

	public String getPreferredTimePattern() {
		return "HH:mm:ss";
	}

	public DateFormat getPreferredTimeFormat() {
		if (preferredTimeFormat != null) {
			return preferredTimeFormat;
		}
		SimpleDateFormat f = new SimpleDateFormat(getPreferredTimePattern(), getPreferredLocale());
		f.setTimeZone(getPreferredTimeZone());
		return preferredTimeFormat = f;
	}

	public TimeZone getPreferredTimeZone() {
		if (preferredTimeZone != null) {
			return preferredTimeZone;
		}
		return preferredTimeZone = TimeZone.getDefault();
	}

	public CalendarHelper getCalendarHelper() {
		CalendarHelper helper = new CalendarHelper(getPreferredTimeZone());
		return helper;
	}
}
