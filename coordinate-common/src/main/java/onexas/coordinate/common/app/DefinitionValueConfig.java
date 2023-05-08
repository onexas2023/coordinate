package onexas.coordinate.common.app;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Locales;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DefinitionValueConfig extends AbstractConfig {
	Config config;
	DefinitionValueResolver resolver;

	public DefinitionValueConfig(Config config, DefinitionValueResolver resolver) {
		this.config = config;
		this.resolver = resolver;
	}

	@Override
	public Config getParent() {
		return config.getParent();
	}

	@Override
	public String getName() {
		return config.getName();
	}

	@Override
	public boolean contains(String key) {
		return config.contains(key);
	}

	@Override
	public String getString(String key, String defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), String.class);
		}
		return defaultValue;
	}

	@Override
	public Boolean getBoolean(String key, Boolean defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), Boolean.class);
		}
		return defaultValue;
	}

	@Override
	public Byte getByte(String key, Byte defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), Byte.class);
		}
		return defaultValue;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), Double.class);
		}
		return defaultValue;
	}

	@Override
	public Float getFloat(String key, Float defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), Float.class);
		}
		return defaultValue;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), Integer.class);
		}
		return defaultValue;
	}

	@Override
	public Long getLong(String key, Long defaultValue) {
		if (config.contains(key)) {
			return resolver.resolve(config.getString(key), Long.class);
		}
		return defaultValue;
	}

	@Override
	public List<String> getStringList(String key, List<String> defaultValue) {
		if (config.contains(key)) {
			List<String> l = config.getStringList(key);
			if (l != null) {
				l = l.stream().map((str) -> {
					return resolver.resolve(str, String.class);
				}).collect(Collectors.toList());
			}
			return l;
		}
		return defaultValue;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClass(String key, Class defaultValue, ClassLoader loader) throws ClassNotFoundException {
		if (contains(key)) {
			String clz = getString(key);
			return Classes.forNameByThread(loader, clz);
		}
		return defaultValue;
	}

	@Override
	public Locale getLocale(String key, Locale defaultValue) {
		if (contains(key)) {
			String l = getString(key);
			return Locales.getLocale(l);
		}
		return defaultValue;
	}

	@Override
	public List<Config> getSubConfigList(String key, String namePath, boolean mergeEmptyName) {
		List<Config> l = config.getSubConfigList(key, namePath, mergeEmptyName);
		if (l != null) {
			l = l.stream().map((c) -> {
				return new DefinitionValueConfig(c, resolver);
			}).collect(Collectors.toList());
		}
		return l;
	}

	@Override
	public long lastModified() {
		return config.lastModified();
	}

	interface DefinitionValueResolver {
		public <T> T resolve(String value, Class<T> clz);
	}
}