package onexas.coordinate.common.app;

import java.util.List;
import java.util.Locale;

import onexas.coordinate.common.lang.Strings;

/**
 * The nested implementation of configuration
 * 
 * @author Dennis Chen
 * 
 */
abstract class AbstractConfig implements Config {

	@Override
	public String getString(String key) {
		return getString(key, null);
	}

	@Override
	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	@Override
	public Byte getByte(String key) {
		return getByte(key, null);
	}

	@Override
	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	@Override
	public Float getFloat(String key) {
		return getFloat(key, null);
	}

	@Override
	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	@Override
	public Long getLong(String key) {
		return getLong(key, null);
	}

	@Override
	public List<String> getStringList(String key) {
		return getStringList(key, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClass(String key) throws ClassNotFoundException {
		return getClass(key, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClass(String key, Class defaultValue) throws ClassNotFoundException {
		return getClass(key, defaultValue, getClass().getClassLoader());
	}

	@Override
	public Locale getLocale(String key) {
		return getLocale(key, null);
	}

	@Override
	public Config getSubConfig(String key) {
		List<Config> cfgs = getSubConfigList(key);
		if (cfgs == null || cfgs.size() == 0) {
			return null;
		} else if (cfgs.size() == 1) {
			return cfgs.get(0);
		} else {
			StringBuilder longname = new StringBuilder();
			for (Config cfg : cfgs) {
				String name = cfg.getName();
				if (name != null) {
					longname.append(name);
				}
			}
			// sub config doesn't has parent
			AggregatedConfig aggCfg = new AggregatedConfig(longname.toString(), null);
			aggCfg.setInvertList(true);
			for (Config cfg : cfgs) {
				aggCfg.addConfig(cfg);
			}
			return aggCfg;
		}
	}

	@Override
	public List<Config> getSubConfigList(String key) {
		return getSubConfigList(key, "[@name]", true);
	}
	
	@Override
	public List<Config> getSubConfigList(String key, boolean mergeEmptyName) {
		return getSubConfigList(key, "[@name]", mergeEmptyName);
	}

	@Override
	public Long getMillisecond(String key) {
		return getMillisecond(key, null);
	}

	@Override
	public Long getMillisecond(String key, String defaultValue) {
		String timeExpr = getString(key, defaultValue);
		return Strings.parseMillisecond(timeExpr);
	}

	static final public Config EMPTY_CONFIG = new AbstractConfig() {

		@SuppressWarnings("unchecked")
		@Override
		public List<Config> getSubConfigList(String key, String namePattern, boolean mergeEmptyName) {
			return java.util.Collections.EMPTY_LIST;
		}

		@Override
		public List<String> getStringList(String key, List<String> defaultValue) {
			return defaultValue;
		}

		@Override
		public String getString(String key, String defaultValue) {
			return defaultValue;
		}

		@Override
		public String getName() {
			return "empty";
		}

		@Override
		public Long getLong(String key, Long defaultValue) {
			return defaultValue;
		}

		@Override
		public Locale getLocale(String key, Locale defaultValue) {
			return defaultValue;
		}

		@Override
		public Integer getInteger(String key, Integer defaultValue) {
			return defaultValue;
		}

		@Override
		public Float getFloat(String key, Float defaultValue) {
			return defaultValue;
		}

		@Override
		public Double getDouble(String key, Double defaultValue) {
			return defaultValue;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Class getClass(String key, Class defaultValue, ClassLoader loader) throws ClassNotFoundException {
			return defaultValue;
		}

		@Override
		public Byte getByte(String key, Byte defaultValue) {
			return defaultValue;
		}

		@Override
		public Boolean getBoolean(String key, Boolean defaultValue) {
			return defaultValue;
		}

		@Override
		public boolean contains(String key) {
			return false;
		}

		@Override
		public Config getParent() {
			return null;
		}

		@Override
		public long lastModified() {
			return 0;
		}
	};
}
