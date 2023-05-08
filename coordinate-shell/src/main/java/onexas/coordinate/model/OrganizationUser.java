package onexas.coordinate.model;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.Constants;

/**
 * 
 * @author Dennis Chen
 *
 */

public class OrganizationUser extends User {

	private static final long serialVersionUID = 1L;

	protected OrganizationUserRelationType relationType;

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + OrganizationUserRelationType.SCHEMA_NAME)
	public OrganizationUserRelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(OrganizationUserRelationType relationType) {
		this.relationType = relationType;
	}
}
