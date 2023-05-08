package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class DomainUpdate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;
	protected String description;
	protected Boolean disabled;
	protected String configYaml;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConfigYaml() {
		return configYaml;
	}

	public void setConfigYaml(String configYaml) {
		this.configYaml = configYaml;
	}


	public DomainUpdate withName(String name) {
		this.name = name;
		return this;
	}

	public DomainUpdate withDescription(String description) {
		this.description = description;
		return this;
	}

	public DomainUpdate withConfigYaml(String configYaml) {
		this.configYaml = configYaml;
		return this;
	}

	public DomainUpdate withDisabled(Boolean disabled) {
		this.disabled = disabled;
		return this;
	}
}
