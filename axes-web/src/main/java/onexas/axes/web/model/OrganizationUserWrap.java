package onexas.axes.web.model;

import onexas.coordinate.api.v1.sdk.model.AOrganizationUser;
import onexas.coordinate.api.v1.sdk.model.OrganizationUserRelationType;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationUserWrap {

	public final AOrganizationUser delegatee;

	public OrganizationUserWrap(AOrganizationUser delegatee) {
		this.delegatee = delegatee;
	}

	public String getAccount() {
		return delegatee.getAccount();
	}

	public Boolean getDisabled() {
		return delegatee.getDisabled();
	}

	public String getDisplayName() {
		return delegatee.getDisplayName();
	}
	
	public String getDomain() {
		return delegatee.getDomain();
	}

	public String getEmail() {
		return delegatee.getEmail();
	}

	public String getUid() {
		return delegatee.getUid();
	}
	
	public OrganizationUserRelationType getRelationType() {
		return delegatee.getRelationType();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegatee.getUid().hashCode();
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
		OrganizationUserWrap other = (OrganizationUserWrap) obj;
		if (!delegatee.getUid().equals(other.getUid()))
			return false;
		return true;
	}

}
