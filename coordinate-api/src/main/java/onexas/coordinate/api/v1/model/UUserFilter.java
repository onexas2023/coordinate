package onexas.coordinate.api.v1.model;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UUserFilter extends PageFilter {
	private static final long serialVersionUID = 1L;

	protected String criteria;

	@NotNull
	@Schema(required = true)
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
