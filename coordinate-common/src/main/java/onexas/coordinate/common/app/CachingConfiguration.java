package onexas.coordinate.common.app;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 
 * @author Dennis Chen
 *
 */
@Profile("!" + CachingConfiguration.PROFILE_DISABLE_CACHING)
@EnableCaching
@Configuration
public class CachingConfiguration {
	public static final String PROFILE_DISABLE_CACHING = "disable-caching";
}