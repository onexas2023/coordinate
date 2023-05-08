package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UserUpdate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String displayName;

	protected String email;

	protected Boolean disabled;

	protected String password;

	@Size(min = 4, max = 64)
	public String getPassword() {
		return password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public UserUpdate withEmail(String email) {
		this.email = email;
		return this;
	}

	public UserUpdate withDisabled(Boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public UserUpdate withPassword(String password) {
		this.password = password;
		return this;
	}

	public UserUpdate withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

}