package onexas.coordinate.common.app;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;

import onexas.coordinate.common.lang.Classes;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Locales;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 * 
 */
class SimpleConfig extends AbstractConfig {
	private ConfigurationProvider cfgProvider;
	private Config parent;
	private String name;
	private URL url;

	private static AtomicInteger nonameId = new AtomicInteger();

	private Map<String, Object> properties = new HashMap<>();
	private Configuration lastCfg;

	SimpleConfig(Configuration cfg, Config parent, URL url) {
		this(new SimpleConfigurationProvider(cfg), parent, url);
	}

	SimpleConfig(String name, Configuration cfg, Config parent, URL url) {
		this.name = name;
		this.cfgProvider = new SimpleConfigurationProvider(cfg);
		this.parent = parent;
		this.url = url;
	}

	SimpleConfig(ConfigurationProvider cfgProvider, Config parent, URL url) {
		this.cfgProvider = cfgProvider;
		this.parent = parent;
		this.url = url;
		getName();// init name
	}

	public interface ConfigurationProvider {
		public Configuration get();

		public long lastModified();
	}

	private static class SimpleConfigurationProvider implements ConfigurationProvider {
		Configuration conf;

		public SimpleConfigurationProvider(Configuration conf) {
			this.conf = conf;
		}

		@Override
		public Configuration get() {
			return conf;
		}

		@Override
		public long lastModified() {
			return 0;
		}
	}

	private Configuration getCfg() {
		Configuration cfg = cfgProvider.get();
		if (cfg != lastCfg) {
			lastCfg = cfg;
			// keep the last cfg, apply properties if it is different.
			for (String k : properties.keySet()) {
				lastCfg.addProperty(k, properties.get(k));
			}
		}
		return cfg;
	}

	public URL getUrl() {
		return url;
	}

	public Config getParent() {
		return parent;
	}

	public String getName() {
		if (name == null) {
			name = getCfg().getString("[@name]", null);
			if (name == null) {
				name = Strings.format("zechconf{}", nonameId.getAndIncrement());
			}
		}
		return name;
	}

	@SuppressWarnings("unchecked")
	public Set<String> getDepends() {
		String depends = getCfg().getString("[@depends]");
		if (depends != null) {
			Set<String> ds = new LinkedHashSet<String>();
			String[] ss = depends.split(",");
			for (String s : ss) {
				s = s.trim();
				if (!Strings.isEmpty(s)) {
					ds.add(s);
				}
			}
			return ds;
		}
		return java.util.Collections.EMPTY_SET;
	}

	@Override
	public String getString(String key, String defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getString(key);
		}
		if (parent != null) {
			return parent.getString(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Boolean getBoolean(String key, Boolean defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getBoolean(key);
		}
		if (parent != null) {
			return parent.getBoolean(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Byte getByte(String key, Byte defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getByte(key);
		}
		if (parent != null) {
			return parent.getByte(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getDouble(key);
		}
		if (parent != null) {
			return parent.getDouble(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Float getFloat(String key, Float defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getFloat(key);
		}
		if (parent != null) {
			return parent.getFloat(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getInteger(key, defaultValue);
		}
		if (parent != null) {
			return parent.getInteger(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Long getLong(String key, Long defaultValue) {
		if (getCfg().containsKey(key)) {
			return getCfg().getLong(key);
		}
		if (parent != null) {
			return parent.getLong(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public List<String> getStringList(String key, List<String> defaultValue) {
		List<String> agg = null;
		if (parent != null && parent.contains(key)) {
			agg = new LinkedList<String>();
			agg.addAll(parent.getStringList(key));
		}

		if (getCfg().containsKey(key)) {
			if (agg == null) {
				agg = new LinkedList<String>();
			}
			String[] v = getCfg().getStringArray(key);
			if (v != null && v.length > 0) {
				agg.addAll(Collections.asList(v));
			}
		}
		return agg == null ? defaultValue : agg;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClass(String key, Class defaultValue, ClassLoader loader) throws ClassNotFoundException {
		if (getCfg().containsKey(key)) {
			return Classes.forNameByThread(loader, getCfg().getString(key));
		}
		if (parent != null) {
			return parent.getClass(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Locale getLocale(String key, Locale defaultValue) {
		if (getCfg().containsKey(key)) {
			return Locales.getLocale(getCfg().getString(key));
		}
		if (parent != null) {
			return parent.getLocale(key, defaultValue);
		}
		return defaultValue;
	}

	@Override
	public boolean contains(String key) {
		if (getCfg().containsKey(key)) {
			return true;
		}
		if (parent != null) {
			return parent.contains(key);
		}
		return false;
	}

	void setParent(Config parent) {
		this.parent = parent;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("@").append(url == null ? "" : url);
		if (parent != null) {
			sb.append("{\n").append(parent.toString()).append("}");
		}
		return sb.toString();
	}

	@Override
	public List<Config> getSubConfigList(String key, String namePath, boolean mergeEmptyName) {
		LinkedHashMap<String, Config> aggMap = new LinkedHashMap<String, Config>();
		if (parent != null) {
			List<Config> parentSubList = parent.getSubConfigList(key, namePath, mergeEmptyName);
			for (Config c : parentSubList) {
				String name = c.getString(namePath);
				if (Strings.isBlank(name)) {
					name = "";
				}
				if (name.equals(SUB_CONFIG_NAME_UID_KEY) || (Strings.isBlank(name) && !mergeEmptyName)) {
					name = Strings.randomUid();
				}
				mergeSubConfig(key, name, aggMap, c);
			}
		}

		Configuration cfg = getCfg();
		if (cfg instanceof HierarchicalConfiguration) {
			List<HierarchicalConfiguration> subConfigs = ((HierarchicalConfiguration) cfg).configurationsAt(key);
			for (HierarchicalConfiguration sub : subConfigs) {
				String name = sub.getString(namePath);
				if (Strings.isBlank(name)) {
					name = "";
				}
				if (name.equals(SUB_CONFIG_NAME_UID_KEY) || (Strings.isBlank(name) && !mergeEmptyName)) {
					name = Strings.randomUid();
				}
				Config c = new SimpleConfig(new StringBuilder(key).append("[").append(name).append("]").toString(),
						sub, null, getUrl());
				mergeSubConfig(key, name, aggMap, c);
			}
		}
		return new ArrayList<Config>(aggMap.values());
	}

	private void mergeSubConfig(String key, String name, LinkedHashMap<String, Config> aggMap, Config c) {
		boolean replace = c.getBoolean("[@replace]", false);
		if (replace) {
			aggMap.put(name, c);
			return;
		}

		Config existed = aggMap.get(name);
		if (existed == null) {
			aggMap.put(name, c);
			return;
		} else if (existed instanceof AggregatedConfig) {
			((AggregatedConfig) existed).insertConfig(c);// the later one should
															// be search fisrt
															// in sub config
		} else {
			AggregatedConfig agg = new AggregatedConfig(new StringBuilder(key).append("[").append(name).append("]")
					.toString(), null);// sub config doesn't has parent
			agg.setInvertList(true);// invert the string result, so it follow
									// the definition list (FIFO) cause we
									// insert the sequence for override other
									// prop
			agg.insertConfig(existed);
			agg.insertConfig(c);
			aggMap.put(name, agg);
		}
	}

	public void setProperty(String key, Object value) {
		getCfg().setProperty(key, value);
		properties.put(key, value);
	}

	@Override
	public long lastModified() {
		return Math.max(cfgProvider.lastModified(), parent == null ? 0 : parent.lastModified());
	}

}
