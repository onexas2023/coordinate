package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class RoleCreate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;

	protected String name;

	protected String description;

	@NotNull
	@Schema(required = true)
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

	public RoleCreate withCode(String code) {
		this.code = code;
		return this;
	}

	public RoleCreate withName(String name) {
		this.name = name;
		return this;
	}

	public RoleCreate withDescription(String description) {
		this.description = description;
		return this;
	}
}