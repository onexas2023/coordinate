package onexas.axes.web;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;
import org.zkoss.zk.ui.http.WebManager;

import onexas.coordinate.common.app.AppContext;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration
@DependsOn(AppContext.BEAN_NAME)
public class WebConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

	public static final String UPDATE_URI = "/zkau";
	public static final String DESKTOP_CACHE_KEY_PREFIX = "axes.desktopCache.";

	// original zk layout servlet (only for war files)
	@Bean
	public ServletRegistrationBean<HttpServlet> zkLoader() {
		final String[] mappings = { "*.zul" };
		ServletRegistrationBean<HttpServlet> reg = new ServletRegistrationBean<HttpServlet>(new DHtmlLayoutServlet(),
				mappings);
		reg.setInitParameters(Collections.singletonMap("update-uri", UPDATE_URI));
		reg.setLoadOnStartup(0);
		logger.info("ZK-Springboot: ServletRegistrationBean for DHtmlLayoutServlet with url pattern "
				+ Arrays.asList(mappings));
		return reg;
	}

	@Bean
	public ServletRegistrationBean<HttpServlet> zkAuEngine() {
		return new ServletRegistrationBean<HttpServlet>(new DHtmlUpdateServlet(), UPDATE_URI + "/*");
	}

	@Bean
	public HttpSessionListener zkHttpSessionListener() {
		return new HttpSessionListener() {

			private WebManager webManager;

			@Override
			public void contextInitialized(ServletContextEvent sce) {
				final ServletContext ctx = sce.getServletContext();
				if (WebManager.getWebManagerIfAny(ctx) == null) {
					webManager = new WebManager(ctx, UPDATE_URI);
				} else {
					throw new IllegalStateException(
							"ZK WebManager already exists. Could not initialize via Spring Boot configuration.");
				}
			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				if (webManager != null) {
					webManager.destroy();
				}
			}

			public void sessionDestroyed(HttpSessionEvent evt) {
				HttpSession session = evt.getSession();
				webManager.getWebApp().removeAttribute(DESKTOP_CACHE_KEY_PREFIX + session.getId());
				super.sessionDestroyed(evt);
			}
		};
	}

	@Bean
	public FilterRegistrationBean<Filter> zkLocalThreadLocalFilter() {
		FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
		reg.setFilter(new LocaleThreadLocalFilter());
		reg.addUrlPatterns(UPDATE_URI, "*.zul");
		return reg;
	}
	
	@Bean
	public FilterRegistrationBean<Filter> zkReuqestUriFilter() {
		FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
		reg.setFilter(new RequestUriFilter());
		reg.addUrlPatterns("/*");
		return reg;
	}
}