package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String uid;

	protected String account;

	protected String displayName;
	
	protected String aliasUid;

	protected String email;

	protected Boolean disabled;

	protected String domain;

	public String getUid() {
		return uid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getAccount() {
		return account;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public String getDomain() {
		return domain;
	}

	public String getAliasUid() {
		return aliasUid;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String combineAccountDomain() {
		return getAccount() + "#" + getDomain();
	}
}