package onexas.coordinate.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.Constants;

/**
 * 
 * @author Dennis Chen
 *
 */

public class OrganizationUserRelation implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String userUid;
	protected OrganizationUserRelationType type;

	public OrganizationUserRelation() {
	}

	public OrganizationUserRelation(String userUid, OrganizationUserRelationType type) {
		super();
		this.userUid = userUid;
		this.type = type;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + OrganizationUserRelationType.SCHEMA_NAME)
	public OrganizationUserRelationType getType() {
		return type;
	}

	public void setType(OrganizationUserRelationType type) {
		this.type = type;
	}
}
