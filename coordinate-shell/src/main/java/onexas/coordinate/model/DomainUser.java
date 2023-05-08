package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class DomainUser implements Serializable {

	private static final long serialVersionUID = 1L;
	String identity;
	String account;
	String displayName;
	String email;
	String domain;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getIdentity() {
		return identity;
	}

	public String getAccount() {
		return account;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public DomainUser withIdentity(String identity) {
		this.identity = identity;
		return this;
	}

	public DomainUser withAccount(String account) {
		this.account = account;
		return this;
	}

	public DomainUser withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public DomainUser withEmail(String email) {
		this.email = email;
		return this;
	}

	public DomainUser withDomain(String domain) {
		this.domain = domain;
		return this;

	}

}