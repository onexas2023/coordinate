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

public class UUserOrganization implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;

	protected String name;

	protected String description;

	protected OrganizationUserRelationType relationType;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + OrganizationUserRelationType.SCHEMA_NAME)
	public OrganizationUserRelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(OrganizationUserRelationType relationType) {
		this.relationType = relationType;
	}

}
