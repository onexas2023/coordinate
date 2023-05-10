package onexas.coordinate.api.v1.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Authentication implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String token;

	String displayName;

	String aliasUid;
	
	String domain;

	List<UPrincipalPermission> permissions;

	public String getToken() {
		return token;
	}

	public List<UPrincipalPermission> getPermissions() {
		return permissions;
	}

	public String getAliasUid() {
		return aliasUid;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setPermissions(List<UPrincipalPermission> permissions) {
		this.permissions = permissions;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
}
