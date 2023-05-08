package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.Constants;
import onexas.coordinate.model.OrganizationUserRelationType;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UOrganizationUserRelation implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String aliasUid;
	protected OrganizationUserRelationType type;

	public UOrganizationUserRelation() {
	}

	public String getAliasUid() {
		return aliasUid;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + OrganizationUserRelationType.SCHEMA_NAME)
	public OrganizationUserRelationType getType() {
		return type;
	}

	public void setType(OrganizationUserRelationType type) {
		this.type = type;
	}
}
