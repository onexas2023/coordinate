package onexas.coordinate.api.v1.admin.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ADomainConfigCheck implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String provider;
	protected String configYaml;

	@NotNull
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getConfigYaml() {
		return configYaml;
	}

	public void setConfigYaml(String configYaml) {
		this.configYaml = configYaml;
	}

	public ADomainConfigCheck withProvider(String provider) {
		this.provider = provider;
		return this;
	}

	public ADomainConfigCheck withConfigYaml(String configYaml) {
		this.configYaml = configYaml;
		return this;
	}
}
