package onexas.coordinate.api.v1.admin.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ADomainProviderFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	String providerCode;

	String configYamlTemplate;

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getConfigYamlTemplate() {
		return configYamlTemplate;
	}

	public void setConfigYamlTemplate(String configYamlTemplate) {
		this.configYamlTemplate = configYamlTemplate;
	}

}
