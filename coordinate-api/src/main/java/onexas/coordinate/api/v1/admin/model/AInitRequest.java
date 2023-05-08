package onexas.coordinate.api.v1.admin.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AInitRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String secret;

	@Schema(description = "the system secret for init", required = true)
	@NotNull
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
