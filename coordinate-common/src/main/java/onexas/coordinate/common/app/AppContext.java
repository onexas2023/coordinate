package onexas.coordinate.common.app;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import onexas.coordinate.common.app.DefinitionValueConfig.DefinitionValueResolver;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(AppContext.BEAN_NAME)
@Configuration
public class AppContext
		implements ApplicationContextAware, ApplicationListener<ContextClosedEvent>, EmbeddedValueResolverAware {

	public static final String BEAN_NAME = Env.NS_BEAN + "AppContext";
	public static final String TASK_EXECUTOR_NAME = Env.NS_BEAN + "coordinateSharedTaskExecutor";
	public static final String TASK_SCHEDULER_NAME = Env.NS_BEAN + "coordinateSharedTaskScheduler";
	public static final String CACHE_MANAGER_NAME = Env.NS_BEAN + "coordinateCacheManager";

	private static final Logger logger = LoggerFactory.getLogger(AppContext.class);

	@Value("${coordinate.shutdownTimeout}")
	int shutdownTimeout;

	@Autowired
	private Environment env;

	private ConversionService conversionService;

	private StringValueResolver embeddedValueResolver;

	private static ApplicationContext applicationContext;
	private Config config;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		synchronized (AppContext.class) {
			if (applicationContext != null) {
				logger.warn("application context is setting more than once");
			}
			applicationContext = context;
			config = initConfig();

			conversionService = DefaultConversionService.getSharedInstance();
		}
	}

	public static Object getBean(String beanName) {
		if (applicationContext == null) {
			throw new IllegalStateException(
					"config is no ready yet, try to add dependency on app context by annotating @DependsOn(AppContext.BEAN_NAME)");
		}
		return applicationContext.getBean(beanName);
	}

	public static <T> T getBean(Class<T> clz) {
		if (applicationContext == null) {
			throw new IllegalStateException(
					"config is no ready yet, try to add dependency on app context by annotating @DependsOn(AppContext.BEAN_NAME)");
		}
		return applicationContext.getBean(clz);
	}

	public static Object bean(String beanName) {
		return getBean(beanName);
	}

	public static <T> T bean(Class<T> clz) {
		return getBean(clz);
	}

	public static Object getBeanIfAny(String beanName) {
		if (applicationContext != null) {
			try {
				return applicationContext.getBean(beanName);
			} catch (NoSuchBeanDefinitionException x) {
			}
		}
		return null;
	}

	public static <T> T getBeanIfAny(Class<T> clz) {
		if (applicationContext != null) {
			try {
				return applicationContext.getBean(clz);
			} catch (NoSuchBeanDefinitionException x) {
			}
		}
		return null;
	}

	public static Object beanIfAny(String beanName) {
		return getBeanIfAny(beanName);
	}

	public static <T> T beanIfAny(Class<T> clz) {
		return getBeanIfAny(clz);
	}

	public Config getConfig() {
		return config;
	}

	private Config initConfig() {
		try {
			Config cfg = null;
			// load in binary config
			cfg = Configs.loadInheritedXML("coordinate-config.xml", cfg);
			// load the customer's config at the last
			String userCfgName = cfg.getString("app.userConfigName", "coordinate-userconfig.xml");
			cfg = Configs.loadUserConfig(userCfgName, cfg);

			cfg = new DefinitionValueConfig(cfg, new DefinitionValueResolver() {
				@Override
				public <T> T resolve(String value, Class<T> clz) {
					return resolveDefinitionValue(value, clz);
				}
			});

			return cfg;
		} catch (Exception x) {
			throw new IllegalStateException(x.getMessage(), x);
		}
	}

	public static AppContext instance() {
		return getBean(AppContext.class);
	}

	public static Config config() {
		return getBean(AppContext.class).getConfig();
	}

	public static ThreadPoolTaskExecutor executor() {
		return (ThreadPoolTaskExecutor) getBean(TASK_EXECUTOR_NAME);
	}

	public static ThreadPoolTaskScheduler scheduler() {
		return (ThreadPoolTaskScheduler) getBean(TASK_SCHEDULER_NAME);
	}

	@Bean(name = { TASK_EXECUTOR_NAME, "taskExecutor" })
	public ThreadPoolTaskExecutor taskExecutor() {
		int core = Runtime.getRuntime().availableProcessors();
		int schedulerCorePoolFactor = Integer.parseInt(env.getProperty("coordinate.taskExecutorCorePoolFactor", "20"));
		int schedulerMaxPoolFactor = Integer.parseInt(env.getProperty("coordinate.taskExecutorMaxPoolFactor", "40"));
		String executorCorePoolSize = env.getProperty("coordinate.taskExecutorCorePoolSize",
				Integer.toString(core * schedulerCorePoolFactor));
		String executorMaxPoolSize = env.getProperty("coordinate.taskExecutorMaxPoolSize",
				Integer.toString(core * schedulerMaxPoolFactor));
		String executorQueueCapacity = env.getProperty("coordinate.taskExecutorQueueCapacity", "2000");

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Integer.parseInt(executorCorePoolSize));
		executor.setMaxPoolSize(Integer.parseInt(executorMaxPoolSize));
		executor.setQueueCapacity(Integer.parseInt(executorQueueCapacity));
		executor.setThreadNamePrefix("coordinate-task-executor-");

		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setAwaitTerminationSeconds(shutdownTimeout);
		executor.initialize();

		logger.info("taskExecutor:{corePool:{}, maxPool:{}, queueCapacity:{}}", executorCorePoolSize,
				executorMaxPoolSize, executorQueueCapacity);

		return executor;
	}

	// ScheduledAnnotationBeanPostProcessor : More than one TaskScheduler bean
	// exists within the context, and none is named 'taskScheduler'. Mark one of
	// them as primary or name it 'taskScheduler' (possibly as an alias); or
	// implement the SchedulingConfigurer interface and call
	// ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks()
	// callback: [onexas.coordinateSharedTaskScheduler, defaultSockJsTaskScheduler]
	@Bean(name = { TASK_SCHEDULER_NAME, "taskScheduler" })
	public ThreadPoolTaskScheduler taskScheduler() {
		int core = Runtime.getRuntime().availableProcessors();
		int schedulerCorePoolFactor = Integer.parseInt(env.getProperty("coordinate.taskSchedulerCorePoolFactor", "20"));
		String schedulerCorePoolSize = env.getProperty("coordinate.taskSchedulerCorePoolSize",
				Integer.toString(core * schedulerCorePoolFactor));

		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(Integer.parseInt(schedulerCorePoolSize));
		scheduler.setThreadNamePrefix("coordinate-task-scheduler-");

		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setAwaitTerminationSeconds(shutdownTimeout);
		scheduler.initialize();

		logger.info("taskScheduler:{poolSize:{}}", schedulerCorePoolSize);

		return scheduler;
	}

	@Bean(name = { CACHE_MANAGER_NAME, "cacheManager" })
	public CacheManager cacheManager() {
		List<String> cacheNames = config().getStringList("coordinateCommon.cacheNames.name", Collections.emptyList());

		logger.info("cacheManager: {cacheNames: {}}", cacheNames);

		if (env.acceptsProfiles(Profiles.of(CachingConfiguration.PROFILE_DISABLE_CACHING))) {
			logger.info("caching is disabled");
		}

		return new ConcurrentMapCacheManager(cacheNames.toArray(new String[cacheNames.size()]));
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		// spring's executor are shutdown automatically
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		embeddedValueResolver = resolver;
	}

	public String resolveDefinitionValue(String value) {
		if (value != null) {
			String expr = value.trim();
			if (expr.startsWith("${") && expr.endsWith("}")) {
				return embeddedValueResolver.resolveStringValue(expr);
			}
		}
		return value;
	}

	public <T> T resolveDefinitionValue(String value, Class<T> clz) {
		return convert(resolveDefinitionValue(value), clz);
	}

	public <T> T convert(Object src, Class<T> clz) {
		if (src == null) {
			return null;
		}
		return conversionService.convert(src, clz);
	}

}