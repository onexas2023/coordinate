package onexas.axes.web.zk.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.zkoss.web.util.resource.ClassWebResource;

import onexas.axes.web.WebConfiguration;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration
public class ZkZulEntryConfiguration implements WebMvcConfigurer {
	private static final Logger logger = LoggerFactory.getLogger(ZkZulEntryConfiguration.class);

	final String VIEW_SUFFIX = ".zul";

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		String prefix = WebConfiguration.UPDATE_URI + ClassWebResource.PATH_PREFIX + "/entry/";
		logger.info("ZK-InternalResourceViewResolver are resolving view 'example' to '{}example{}'", prefix, VIEW_SUFFIX);
		InternalResourceViewResolver resolver = new InternalResourceViewResolver(prefix, VIEW_SUFFIX);
		resolver.setOrder(Ordered.LOWEST_PRECEDENCE);
		registry.viewResolver(resolver);
	}
}