package onexas.coordinate.api.v1.admin.model;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AInitDemoRequest extends AInitRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String demo;

	@Schema(description = "the demo to init", required = true, defaultValue = "")
	@NotNull
	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

}
