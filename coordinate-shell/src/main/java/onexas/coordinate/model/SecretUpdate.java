package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class SecretUpdate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String description;

	protected String content;

	public String getDescription() {
		return description;
	}

	public String getContent() {
		return content;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public SecretUpdate withDescription(String description) {
		this.description = description;
		return this;
	}

	public SecretUpdate withContent(String content) {
		this.content = content;
		return this;
	}

}