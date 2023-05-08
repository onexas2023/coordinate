package onexas.coordinate.web.springdoc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import onexas.coordinate.common.app.Env;

@Configuration(Env.NS_BEAN + "SpringDocApiBlockerConfig")
@Profile("!" + Env.PROFILE_API_DOC)
public class SpringDocApiBlockerConfig {

	@Value("${springdoc.api-root.path}")
	String apiRootPath;
	
	@Value("${springdoc.api-docs.path}")
	String apiDocsPath;

	@Value("${springdoc.swagger-ui.path}")
	String swaggerUiPath;

	@Bean
	public FilterRegistrationBean<Filter> springDocApiBlockerFilter() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new Filter() {
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		});
		
		registration.addUrlPatterns(apiDocsPath);
		registration.addUrlPatterns(swaggerUiPath);
		// static
		registration.addUrlPatterns(apiRootPath+"swagger-ui/*");
		registration.setOrder(1);
		return registration;
	}

}