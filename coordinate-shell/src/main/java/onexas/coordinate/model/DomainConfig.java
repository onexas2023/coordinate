package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.err.BadConfigurationException;
import onexas.coordinate.common.util.BetterPropertySource;

/**
 * 
 * @author Dennis Chen
 *
 */

public interface DomainConfig extends Serializable {

	BetterPropertySource toPropertySource() throws BadConfigurationException;
	<T> T toObject(Class<T> clz);
	String toYaml();
}
