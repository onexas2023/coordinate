package onexas.axes.web.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import onexas.coordinate.api.v1.sdk.model.AOrganization;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationWrap {

	public final AOrganization delegatee;
	
	public OrganizationWrap(AOrganization delegatee) {
		this.delegatee = delegatee;
	}

	@NotNull
	@Size(min=1, max=128)
	@Pattern(regexp = "[a-z]([-a-z0-9]*[a-z0-9])?", message = "l:axes.validation.codeFormatInvalidate")
	public String getCode() {
		return delegatee.getCode();
	}

	@Size(max=256)
	public String getDescription() {
		return delegatee.getDescription();
	}

	@NotNull
	@Size(min=1, max=128)
	public String getName() {
		return delegatee.getName();
	}

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
		OrganizationWrap other = (OrganizationWrap) obj;
		if (!delegatee.getUid().equals(other.getUid()))
			return false;
		return true;
	}
}
