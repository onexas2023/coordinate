package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Domain implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String LOCAL = "local";

	protected String code;
	protected String name;
	protected String provider;
	protected String description;
	protected Boolean disabled;
	protected Long createdDateTime;
	

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public String getProvider() {
		return provider;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setProvider(String provider) {
		this.provider = provider;
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

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

}
