package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class SecretCreate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;

	protected String description;

	protected String content;

	@NotNull
	@Schema(required = true)
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getContent() {
		return content;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public SecretCreate withCode(String code) {
		this.code = code;
		return this;
	}

	public SecretCreate withDescription(String description) {
		this.description = description;
		return this;
	}

	public SecretCreate withContent(String content) {
		this.content = content;
		return this;
	}

}