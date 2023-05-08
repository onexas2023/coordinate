package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UserOrganizationRelation implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String organizationUid;
	protected OrganizationUserRelationType type;

	public UserOrganizationRelation() {
	}

	public UserOrganizationRelation(String organizationUid, OrganizationUserRelationType type) {
		super();
		this.organizationUid = organizationUid;
		this.type = type;
	}

	public String getOrganizationUid() {
		return organizationUid;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

	public OrganizationUserRelationType getType() {
		return type;
	}

	public void setType(OrganizationUserRelationType type) {
		this.type = type;
	}
}
