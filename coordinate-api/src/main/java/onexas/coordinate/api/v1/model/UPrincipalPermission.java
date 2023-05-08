package onexas.coordinate.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.model.PrincipalPermission;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UPrincipalPermission extends PrincipalPermission{
	private static final long serialVersionUID = 1L;
	
	@Override
	@Schema(required = true)
	public String getTarget() {
		return super.getTarget();
	}
	
	@Override
	@Schema(required = true)
	public String getAction() {
		return super.getAction();
	}
}
