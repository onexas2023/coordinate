package onexas.coordinate.model;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.util.BetterPropertySource;
import onexas.coordinate.common.util.Yamls;

/**
 * 
 * @author Dennis Chen
 *
 */

public class YamlDomainConfig implements DomainConfig {

	private static final long serialVersionUID = 1L;

	String yaml;
	
	public YamlDomainConfig(String yaml) {
		this.yaml = yaml;
	}
	
	public <T> T toObject(Class<T> clz) {
		return Yamls.objectify(toYaml(), clz);
	}
	
	public BetterPropertySource toPropertySource() throws BadConfigurationException {
		return Yamls.toPropertySource(toYaml());
	}
	
	public String toYaml(){
		return yaml;
	}
}
