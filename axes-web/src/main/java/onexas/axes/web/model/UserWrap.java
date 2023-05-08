package onexas.axes.web.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import onexas.coordinate.api.v1.sdk.model.AUser;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserWrap {

	public final AUser delegatee;

	public UserWrap(AUser delegatee) {
		this.delegatee = delegatee;
	}

	@NotNull
	@Size(min=2, max=128)
	@Pattern(regexp = "(^[a-z])[a-z0-9@\\_\\-\\.]*", message = "l:axes.validation.accountFormatInvalidate")
	public String getAccount() {
		return delegatee.getAccount();
	}

	@NotNull
	public Boolean getDisabled() {
		return delegatee.getDisabled();
	}

	@NotNull
	@Size(min=1, max=128)
	public String getDisplayName() {
		return delegatee.getDisplayName();
	}
	
	@NotNull
	public String getDomain() {
		return delegatee.getDomain();
	}

	@Size(max=256)
	public String getEmail() {
		return delegatee.getEmail();
	}

	@NotNull
	public String getUid() {
		return delegatee.getUid();
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
		UserWrap other = (UserWrap) obj;
		if (!delegatee.getUid().equals(other.getUid()))
			return false;
		return true;
	}

}
