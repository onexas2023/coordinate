package onexas.coordinate.common.app;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The boot to define to load basic coordinate-beans.xml in all spring boot related
 * project and also private properties file
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateBootApplication")
@ImportResource({ "classpath*:coordinate-beans.xml" })
@PropertySource("classpath:coordinate-common.properties")
@EnableScheduling
@EnableAsync
// set spring.flyway.enabled doesn't work in my current evn, has to exclude FlywayAutoConfiguration. in @SpringBootApplication
@SpringBootApplication(exclude = {
		FlywayAutoConfiguration.class/*
										 * DataSourceAutoConfiguration.class,
										 * DataSourceTransactionManagerAutoConfiguration.class,
										 * HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class,
										 * ValidationAutoConfiguration.class
										 */ })
@DependsOn(AppContext.BEAN_NAME)
public class CoordinateBootApplication {

	private static final Logger logger = LoggerFactory.getLogger(CoordinateBootApplication.class);

	private static final AtomicBoolean cpDump = new AtomicBoolean(false);

	@Autowired
	private Environment env;

	@Value("${coordinate.node}")
	private String node;

	public CoordinateBootApplication() {
		boolean dumpClasspaths = Boolean.TRUE.equals(Boolean.valueOf(System.getProperty("dumpClasspaths")));
		if (dumpClasspaths && cpDump.compareAndSet(false, true)) {
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URL[] urls = ((URLClassLoader) cl).getURLs();
			for (URL url : urls) {
				System.out.println(">>Classpath : " + url.getFile());
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}

	@Bean(Env.NS_BEAN + "dumpClasspaths")
	public CommandLineRunner dumpClasspaths(ApplicationContext appContext) {
		final boolean dumpClasspaths = Boolean.TRUE
				.equals(env.getProperty("coordinate.debug.dumpClasspaths", Boolean.class));

		return args -> {
			if (dumpClasspaths) {
				ClassLoader cl = ClassLoader.getSystemClassLoader();

				URL[] urls = ((URLClassLoader) cl).getURLs();
				for (URL url : urls) {
					logger.info(">>Classpath : " + url.getFile());
				}
			}
		};
	}

	@Bean(Env.NS_BEAN + "dumpBean")
	public CommandLineRunner dumpBean(ApplicationContext appContext) {
		final boolean dumpBean = Boolean.TRUE.equals(env.getProperty("coordinate.debug.dumpBeans", Boolean.class));

		return args -> {
			if (dumpBean) {
				String[] beans = appContext.getBeanDefinitionNames();
				Arrays.stream(beans).sorted().forEach(bean -> {
					logger.info(">>Bean : " + bean);
				});
			}
		};
	}

	public static Properties getDefaultProperties() {
		Properties props = new Properties();
		props.setProperty("spring.config.name", "application,boot,boot.local");
		return props;
	}

	@EventListener
	public void onApplicationReady(ApplicationReadyEvent evt) {
		if (logger.isInfoEnabled()) {
			Config config = AppContext.config();
			List<Config> sub = config.getSubConfigList("app.versions.version");
			for (Config c : sub) {
				logger.info("Module version - {}@{}", c.getString("[@name]"), c.getString(""));
			}
			logger.info("Application {} version - {}", config.getString("app.name", "unknown"),
					config.getString("app.version", "unknown"));
			logger.info("Node {} ready", node);
		}
	}
}
