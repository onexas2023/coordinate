package onexas.coordinate.common.app;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class CachedConfig extends AbstractConfig{
	
	private static final Object NULL = new Object();

	Config config;
	
	Map<String,Object> cache = Collections.newConcurrentMap();
	
	public CachedConfig(Config config) {
		this.config = config;
	}

	@Override
	public Config getParent() {
		return config;
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
		String ckey = key+".S";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getString(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(String)val;
	}

	@Override
	public Boolean getBoolean(String key, Boolean defaultValue) {
		String ckey = key+".BL";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getBoolean(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Boolean)val;
	}

	@Override
	public Byte getByte(String key, Byte defaultValue) {
		String ckey = key+".B";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getByte(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Byte)val;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
		String ckey = key+".D";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getDouble(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Double)val;
	}

	@Override
	public Float getFloat(String key, Float defaultValue) {
		String ckey = key+".F";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getFloat(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Float)val;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		String ckey = key+".I";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getInteger(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Integer)val;
	}

	@Override
	public Long getLong(String key, Long defaultValue) {
		String ckey = key+".L";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getLong(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Long)val;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getStringList(String key, List<String> defaultValue) {
		String ckey = key+".SL";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getStringList(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(List<String>)val;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClass(String key, Class defaultValue, ClassLoader loader) throws ClassNotFoundException {
		String ckey = key+".CLZ";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getClass(key,null, loader);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Class)val;
	}

	@Override
	public Locale getLocale(String key, Locale defaultValue) {
		String ckey = key+".LOC";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getLocale(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?defaultValue:(Locale)val;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Config> getSubConfigList(String key,String namePath,boolean mergeEmptyName) {
		String ckey = Strings.cat(key,".",namePath,".",mergeEmptyName,".SCL");
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getSubConfigList(key,namePath,mergeEmptyName);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val==NULL?null:(List<Config>)val;
	}

	@Override
	public Long getMillisecond(String key, String defaultValue){
		String ckey = key+".MLS";
		Object val = cache.get(ckey);
		if(val==null){
			val = config.getMillisecond(key);
			cache.put(ckey, val==null?val=NULL:val);
		}
		return val == NULL ? defaultValue == null ? null : Strings.parseMillisecond(defaultValue)
				: (Long) val;
	}

	@Override
	public String toString() {
		return config.toString();
	}

	@Override
	public long lastModified() {
		return 0;
	}
	
}
