package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class HookUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HookUpdate withDescription(String description) {
		this.description = description;
		return this;
	}
}