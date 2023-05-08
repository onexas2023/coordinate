package onexas.coordinate.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UOrganizationUserFilter extends PageFilter {
	private static final long serialVersionUID = 1L;
	
	protected String criteria;

	@Schema(description = "the criteria for quering")
	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	
	@Override
	@Schema(hidden = true)
	public Boolean getMatchAny() {
		return super.getMatchAny();
	}
}
