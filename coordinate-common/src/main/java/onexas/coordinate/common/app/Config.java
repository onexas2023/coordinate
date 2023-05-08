package onexas.coordinate.common.app;

import java.util.List;
import java.util.Locale;
/**
 * 
 * @author Dennis Chen
 *
 */
public interface Config {
	
	
	static final int DEFAULT_PRIORITY = 1000;
	
	public static final String SUB_CONFIG_NAME_UID_KEY = "_uid";
	
	public static final String PATH_NAME_ATTR = "[@name]";

	public Config getParent();
		
	public String getName();
	
	public boolean contains(String key);
	
	public String getString(String key);

	public String getString(String key, String defaultValue);

	public Boolean getBoolean(String key);

	public Boolean getBoolean(String key, Boolean defaultValue);

	public Byte getByte(String key);

	public Byte getByte(String key, Byte defaultValue);

	public Double getDouble(String key);

	public Double getDouble(String key, Double defaultValue);

	public Float getFloat(String key);

	public Float getFloat(String key, Float defaultValue);

	public Integer getInteger(String key);

	public Integer getInteger(String key, Integer defaultValue);

	public Long getLong(String key);

	public Long getLong(String key, Long defaultValue);
	
	/**
	 * Get the millisecond. 
	 * You can put long value(e.g. 18000) or time expression in the config file, e.g. 3d9h30m20s30ms.
	 * Acceptable symbol, d-Day, h-Hour, m-Minute, s-second, ms-Millsecond
	 */
	public Long getMillisecond(String key);
	/**
	 * Get the millisecond. 
	 * You can put long value(e.g. 18000) or time expression in the config file, e.g. 3d9h30m20s30ms.
	 * Acceptable symbol, d-Day, h-Hour, m-Minute, s-second, ms-Millsecond
	 */
	public Long getMillisecond(String key, String defaultValue);

	public List<String> getStringList(String key);

	public List<String> getStringList(String key, List<String> defaultValue);

	@SuppressWarnings("rawtypes")
	public Class getClass(String key) throws ClassNotFoundException;

	@SuppressWarnings("rawtypes")
	public Class getClass(String key, Class defaultValue)
			throws ClassNotFoundException;

	@SuppressWarnings("rawtypes")
	public Class getClass(String key, Class defaultValue, ClassLoader loader)
			throws ClassNotFoundException;

	public Locale getLocale(String key);

	public Locale getLocale(String key, Locale defaultValue);
	
	/**
	 * get sub config list, if any subConfig has same "name" on key attribute, it will merge them to only one sub config 
	 */
	public List<Config> getSubConfigList(String key);
	
	public List<Config> getSubConfigList(String key, boolean mergeEmptyName);
	
	public List<Config> getSubConfigList(String key, String namePath, boolean mergeEmptyName);
	
	/**
	 * Get the first sub config of key, return null if not found, if any subConfig has same "name" on key attribute, it will merge them to only one sub config 
	 */
	public Config getSubConfig(String key);
	
	/**
	 * the last modified time, return 0 if not support
	 */
	public long lastModified();

}