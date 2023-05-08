package onexas.coordinate.web;

import java.util.List;

import javax.servlet.ServletRequestListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateWebConfiguration")
@PropertySource("classpath:coordinate-web.properties")
@DependsOn(AppContext.BEAN_NAME)
public class CoordinateWebConfiguration implements WebMvcConfigurer {

	@Value("${coordinate-web.api.cors.allowedOrigins:*}")
	String[] corsAllowedOrigins;

	@Value("${coordinate-web.api.cors.allowCredentials:false}")
	boolean corsAllowCredential;

	@Value("${coordinate-web.api.cors.allowedMethods:GET,PUT,POST,DELETE,HEAD}")
	String[] corsAllowedMethods;

	@Value("${coordinate-web.api.cors.allowedHeaders:*}")
	String[] corsAllowedHeaders;

	@Value("${coordinate-web.api.cors.exposedHeaders:#{null}}")
	String[] corsExposedHeaders;

	@Value("${coordinate-web.api.cors.maxAge:86400}")
	Long corsMaxAge;

	@Value("${coordinate-web.api.maxUploadSize:-1}")
	Long maxUploadSize;

	@Value("${coordinate-web.api.maxUploadSizePreFile:-1}")
	Long maxUploadSizePreFile;

	@Value("${coordinate-web.api.maxInMemorySize:10240}")
	Integer maxInMemorySize;

	// Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like
	// Gecko) Chrome/96.0.4664.55 Safari/537.36 Edg/96.0.1054.43
	@Value("${coordinate-web.api.modernUserAgentRegex:.*(Mozilla|AppleWebKit|Chrome|Safari|Edg|Gecko|java).*}")
	String modernUserAgentRegex;

	@Bean
	public ServletRequestListener requestContextListener() {
		return new RequestContextListener();
	}

	@Bean
	@Primary
	public ObjectMapper jacksonObjectMapper() {
		return Jsons.newObjectMapper();
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(Collections.asList(corsAllowedOrigins));
		config.setAllowCredentials(corsAllowCredential);
		config.setAllowedMethods(Collections.asList(corsAllowedMethods));
		config.setAllowedHeaders(Collections.asList(corsAllowedHeaders));

		if (corsMaxAge != null) {
			config.setMaxAge(corsMaxAge);
		}

		if (corsExposedHeaders != null) {
			config.setExposedHeaders(Collections.asList(corsExposedHeaders));
		}

		UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
		configSource.registerCorsConfiguration("/**", config);

		return new CorsFilter(configSource);
	}

	@Bean
	@Primary
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(maxUploadSize);
		multipartResolver.setMaxUploadSizePerFile(maxUploadSizePreFile);
		multipartResolver.setMaxInMemorySize(maxInMemorySize);
		return multipartResolver;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		FileDownloadMessageConverter fdmc = new FileDownloadMessageConverter();
		fdmc.setModernUserAgentRegex(modernUserAgentRegex);

		// before MappingJackson2HttpMessageConverter

		int idx = -1;
		for (HttpMessageConverter<?> c : messageConverters) {
			idx++;
			if (c instanceof MappingJackson2HttpMessageConverter) {
				break;
			}
		}

		messageConverters.add(idx >= 0 ? idx : messageConverters.size(), new ResponseMessageConverter());
		messageConverters.add(idx >= 0 ? idx : messageConverters.size(), fdmc);
	}
}