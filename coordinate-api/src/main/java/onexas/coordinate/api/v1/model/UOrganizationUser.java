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

public class UOrganizationUser implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String aliasUid;
	protected String displayName;
	protected String domain;
	protected OrganizationUserRelationType relationType;

	public String getAliasUid() {
		return aliasUid;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDomain() {
		return domain;
	}

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + OrganizationUserRelationType.SCHEMA_NAME)
	public OrganizationUserRelationType getRelationType() {
		return relationType;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setRelationType(OrganizationUserRelationType relationType) {
		this.relationType = relationType;
	}

}
