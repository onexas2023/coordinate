package onexas.coordinate.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UUserOrganizationFilter extends PageFilter {
	private static final long serialVersionUID = 1L;
	
	@Override
	@Schema(hidden = true)
	public Boolean getMatchAny() {
		return super.getMatchAny();
	}
}
