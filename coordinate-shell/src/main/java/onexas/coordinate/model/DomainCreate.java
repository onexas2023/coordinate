package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class DomainCreate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;
	protected String name;
	protected String description;
	protected String provider;
	protected Boolean disabled;
	protected String configYaml;

	@NotNull
	@Schema(required = true)
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getProvider() {
		return provider;
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

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
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

	public DomainCreate withCode(String code) {
		this.code = code;
		return this;
	}

	public DomainCreate withName(String name) {
		this.name = name;
		return this;
	}

	public DomainCreate withProvider(String provider) {
		this.provider = provider;
		return this;
	}

	public DomainCreate withDescription(String description) {
		this.description = description;
		return this;
	}

	public DomainCreate withConfigYaml(String configYaml) {
		this.configYaml = configYaml;
		return this;
	}

	public DomainCreate withDisabled(Boolean disabled) {
		this.disabled = disabled;
		return this;
	}
}
