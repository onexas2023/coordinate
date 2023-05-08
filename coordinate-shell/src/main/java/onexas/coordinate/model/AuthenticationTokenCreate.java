package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AuthenticationTokenCreate implements Serializable {

	private static final long serialVersionUID = 1L;
	String account;
	String domain;
	String clientIp;
	String aliasUid;
	String displayName;

	@NotNull
	public String getAccount() {
		return account;
	}

	@NotNull
	public String getDomain() {
		return domain;
	}

	public String getClientIp() {
		return clientIp;
	}

	@NotNull
	public String getAliasUid() {
		return aliasUid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public AuthenticationTokenCreate withAccount(String account) {
		this.account = account;
		return this;
	}

	public AuthenticationTokenCreate withDomain(String domain) {
		this.domain = domain;
		return this;
	}
	
	public AuthenticationTokenCreate withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public AuthenticationTokenCreate withClientIp(String clientIp) {
		this.clientIp = clientIp;
		return this;
	}

	public AuthenticationTokenCreate withAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
		return this;
	}
}
