package onexas.coordinate.api;

import java.util.LinkedList;
import java.util.List;

import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 *
 */
public class GrantPermissions {

	List<GrantPermission> permissions;

	boolean matchAll;

	public List<GrantPermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<GrantPermission> permissions) {
		this.permissions = permissions;
	}

	public boolean isMatchAll() {
		return matchAll;
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}

	public GrantPermissions withPermissions(GrantPermission... permissions) {
		this.permissions = Collections.asList(permissions);
		return this;
	}

	public GrantPermissions withMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
		return this;
	}

	public void addToPermissions(GrantPermission permission) {
		if (permissions == null) {
			permissions = new LinkedList<>();
		}
		permissions.add(permission);
	}
}
