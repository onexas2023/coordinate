package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class OrganizationUpdate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;

	protected String name;

	protected String description;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OrganizationUpdate withCode(String code) {
		this.code = code;
		return this;
	}

	public OrganizationUpdate withName(String name) {
		this.name = name;
		return this;
	}

	public OrganizationUpdate withDescription(String description) {
		this.description = description;
		return this;
	}

}