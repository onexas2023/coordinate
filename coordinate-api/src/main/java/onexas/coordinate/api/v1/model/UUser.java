package onexas.coordinate.api.v1.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UUser implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String aliasUid;
	protected String displayName;
	protected String domain;

	public String getAliasUid() {
		return aliasUid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDomain() {
		return domain;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
