package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AuthenticationToken implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String token;

	protected String account;

	protected String domain;

	protected String clientIp;

	protected String aliasUid;

	protected Long timeoutAt;

	protected String displayName;

	public String getToken() {
		return token;
	}

	public String getAccount() {
		return account;
	}

	public String getClientIp() {
		return clientIp;
	}

	public Long getTimeoutAt() {
		return timeoutAt;
	}

	public String getDomain() {
		return domain;
	}

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

	public void setToken(String token) {
		this.token = token;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public void setTimeoutAt(Long timeoutAt) {
		this.timeoutAt = timeoutAt;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
