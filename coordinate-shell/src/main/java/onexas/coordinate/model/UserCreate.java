package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UserCreate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String account;

	protected String displayName;

	protected String email;

	protected Boolean disabled;

	protected String domain;

	protected String password;

	@NotNull
	@Schema(required = true)
	public String getAccount() {
		return account;
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

	public String getDomain() {
		return domain;
	}

	@NotNull
	@Size(min = 4)
	@Schema(required = true)
	public String getPassword() {
		return password;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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

	public UserCreate withAccount(String account) {
		this.account = account;
		return this;
	}

	public UserCreate withEmail(String email) {
		this.email = email;
		return this;
	}

	public UserCreate withDisabled(Boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public UserCreate withDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public UserCreate withPassword(String password) {
		this.password = password;
		return this;
	}

	public UserCreate withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

}