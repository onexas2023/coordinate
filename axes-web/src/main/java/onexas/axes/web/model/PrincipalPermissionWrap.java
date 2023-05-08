package onexas.axes.web.model;
/**
 * 
 * @author Dennis Chen
 *
 */

import onexas.coordinate.api.v1.sdk.model.APrincipalPermission;

public class PrincipalPermissionWrap {

	public final APrincipalPermission deletatee;

	public PrincipalPermissionWrap(APrincipalPermission deletatee) {
		this.deletatee = deletatee;
	}

	public String getAction() {
		return deletatee.getAction();
	}

	public String getTarget() {
		return deletatee.getTarget();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getAction() == null) ? 0 : getAction().hashCode());
		result = prime * result + ((getTarget() == null) ? 0 : getTarget().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrincipalPermissionWrap other = (PrincipalPermissionWrap) obj;
		if (getAction() == null) {
			return false;
		} else if (!getAction().equals(other.getAction()))
			return false;
		if (getTarget() == null) {
			return false;
		} else if (!getTarget().equals(other.getTarget()))
			return false;
		return true;
	}

}
