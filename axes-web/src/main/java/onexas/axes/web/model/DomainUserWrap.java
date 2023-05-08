package onexas.axes.web.model;

import onexas.coordinate.api.v1.sdk.model.ADomainUser;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainUserWrap {

	public final ADomainUser delegatee;

	public DomainUserWrap(ADomainUser delegatee) {
		this.delegatee = delegatee;
	}

	public String getAccount() {
		return delegatee.getAccount();
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

	public String getIdentity() {
		return delegatee.getIdentity();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegatee.getIdentity().hashCode();
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
		DomainUserWrap other = (DomainUserWrap) obj;
		if (!delegatee.getIdentity().equals(other.getIdentity()))
			return false;
		return true;
	}

}
