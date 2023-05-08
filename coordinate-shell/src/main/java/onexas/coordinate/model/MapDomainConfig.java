package onexas.coordinate.model;

import java.util.LinkedHashMap;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.common.util.Yamls;

/**
 * 
 * @author Dennis Chen
 *
 */

public class MapDomainConfig extends LinkedHashMap<String, Object> implements DomainConfig {

	private static final long serialVersionUID = 1L;

	public <T> T toObject(Class<T> clz) {
		return Yamls.objectify(toYaml(), clz);
	}
	
	public BetterPropertySource toPropertySource() throws BadConfigurationException {
		return Yamls.toPropertySource(toYaml());
	}
	
	public String toYaml(){
		return Yamls.yamlify(this);
	}

	public MapDomainConfig with(String key, Object val) {
		put(key, val);
		return this;
	}
}
