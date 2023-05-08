package onexas.coordinate.common.app;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import onexas.coordinate.common.lang.Strings;

/**
 * The nested implementation of configuration
 * @author Dennis Chen
 * 
 */
class AggregatedConfig extends AbstractConfig {
	
	private List<Config> aggregatedList = new LinkedList<Config>(); 
	private Config parent;
	private String name;
	AggregatedConfig(Config parent) {
		this.parent = parent;
	}
	AggregatedConfig(String name,Config parent) {
		this.name = name;
		this.parent = parent;
	}
	
	public String getName(){
		if(name!=null){
			return name;
		}
		StringBuilder sb = new StringBuilder();
		for(Config c:aggregatedList){
			if(sb.length()>0){
				sb.append(", ");
			}
			sb.append(c.getName());
		}
		
		return sb.toString();
	}
	
	public Config getParent(){
		return parent;
	}
	
	void addConfig(Config conf){
		aggregatedList.add(conf);
	}
	
	void insertConfig(Config conf){
		aggregatedList.add(0,conf);
	}
	
	int size(){
		return aggregatedList.size();
	}
	
	Config getConfig(int i){
		return aggregatedList.get(i);
	}
	
	@Override
	public boolean contains(String key) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return true;
			}
		}
		if(parent!=null){
			return parent.contains(key);
		}
		return false;
	}

	@Override
	public String getString(String key, String defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getString(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getString(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Boolean getBoolean(String key, Boolean defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getBoolean(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getBoolean(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Byte getByte(String key, Byte defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getByte(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getByte(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Double getDouble(String key, Double defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getDouble(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getDouble(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Float getFloat(String key, Float defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getFloat(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getFloat(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Integer getInteger(String key, Integer defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getInteger(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getInteger(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public Long getLong(String key, Long defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getLong(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getLong(key,defaultValue);
		}
		return defaultValue;
	}

	@Override
	public List<String> getStringList(String key, List<String> defaultValue) {
		List<String> agg = null;
		
		if(parent!=null && parent.contains(key)){
			agg = new LinkedList<String>();
			agg.addAll(parent.getStringList(key));
		}
		
		if(invertList){
			int i = aggregatedList.size()-1;
			for(;i>=0;i--){
				Config conf = aggregatedList.get(i);
				if(conf.contains(key)){
					if(agg==null){
						agg = new LinkedList<String>();
					}
					agg.addAll(conf.getStringList(key));
				}
			}
		}else{
			for(Config conf:aggregatedList){
				if(conf.contains(key)){
					if(agg==null){
						agg = new LinkedList<String>();
					}
					agg.addAll(conf.getStringList(key));
				}
			}
		}
		return agg == null ? defaultValue : agg;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public Class getClass(String key,Class defaultValue,ClassLoader loader) throws ClassNotFoundException{
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getClass(key,defaultValue,loader);
			}
		}
		if(parent!=null){
			return parent.getClass(key,defaultValue,loader);
		}
		return defaultValue;
	}

	@Override
	public Locale getLocale(String key, Locale defaultValue) {
		for(Config conf:aggregatedList){
			if(conf.contains(key)){
				return conf.getLocale(key,defaultValue);
			}
		}
		if(parent!=null){
			return parent.getLocale(key,defaultValue);
		}
		return defaultValue;
	}

	void setParent(Config parent) {
		this.parent = parent;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(Config conf:aggregatedList){
			if(sb.length()>1){
				sb.append(",");
			}
			sb.append(conf.toString());
		}
		sb.append("]");
		if(parent!=null){
			sb.append("{\n").append(parent.toString()).append("}");
		}		
		return sb.toString();
	}	
	
	@Override
	public List<Config> getSubConfigList(String key,String namePath,boolean mergeEmptyName) {
		LinkedHashMap<String,Config> aggMap = new LinkedHashMap<String, Config>();
		if(parent!=null){
			List<Config> parentSubList = parent.getSubConfigList(key,namePath,mergeEmptyName);
			for(Config c:parentSubList){
				String name = c.getString(namePath);
				if(Strings.isBlank(name)){
					name = "";
				}
				if(name.equals(SUB_CONFIG_NAME_UID_KEY) || (Strings.isBlank(name) && !mergeEmptyName)){
					name = Strings.randomUid();
				}
				mergeSubConfig(key,name,aggMap,c);
			}
		}
		if(invertList){
			int i = aggregatedList.size()-1;
			for(;i>=0;i--){
				Config conf = aggregatedList.get(i);
				List<Config> aggSubList = conf.getSubConfigList(key,namePath,mergeEmptyName);
				for(Config c:aggSubList){
					String name = c.getString(namePath);
					if(Strings.isBlank(name)){
						name = "";
					}
					if(name.equals(SUB_CONFIG_NAME_UID_KEY) || (Strings.isBlank(name) && !mergeEmptyName)){
						name = Strings.randomUid();
					}
					mergeSubConfig(key,name,aggMap,c);
				}
			}
		}else{
			for(Config conf:aggregatedList){
				List<Config> aggSubList = conf.getSubConfigList(key,namePath,mergeEmptyName);
				for(Config c:aggSubList){
					String name = c.getString(namePath);
					if(Strings.isBlank(name)){
						name = "";
					}
					if(name.equals(SUB_CONFIG_NAME_UID_KEY) || (Strings.isBlank(name) && !mergeEmptyName)){
						name = Strings.randomUid();
					}
					mergeSubConfig(key,name,aggMap,c);
				}
			}
		}
		return new ArrayList<Config>(aggMap.values());
	}

	private void mergeSubConfig(String key,String name, LinkedHashMap<String, Config> aggMap, Config c) {
		boolean replace = c.getBoolean("[@replace]",false);
		if(replace){
			aggMap.put(name, c);
			return;
		}
		
		Config existed = aggMap.get(name);
		if(existed==null){
			aggMap.put(name, c);
			return;
		}else if(existed instanceof AggregatedConfig){
			((AggregatedConfig)existed).insertConfig(c);
		}else{
			AggregatedConfig agg = new AggregatedConfig(new StringBuilder(key).append("[").append(name).append("]").toString(),null);//sub config doesn't has parent
			agg.setInvertList(true);//invert the string result, so it follow the definition list (FIFO) cause we insert the sequence for override other prop
			agg.insertConfig(existed);
			agg.insertConfig(c);
			aggMap.put(name, agg);
		}
	}

	
	private boolean invertList = false;
	
	void setInvertList(boolean invert) {
		invertList = invert;
	}
	@Override
	public long lastModified() {
		long lm = parent == null ? 0 : parent.lastModified();
		for(Config cfg:aggregatedList){
			lm = Math.max(lm, cfg.lastModified());
		}
		return lm;
	}	
}
