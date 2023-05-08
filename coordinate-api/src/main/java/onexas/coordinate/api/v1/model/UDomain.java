package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;
	protected String name;

	@Schema(description = "The domain code")
	public String getCode() {
		return code;
	}

	@Schema(description = "The domain name")
	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

}
