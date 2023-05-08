package onexas.axes.web.zk.ctrl.admin.role;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.ARole;
/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	RoleEventType type;
	ARole role;
	
	public RoleEvent(RoleEventType type, ARole role) {
		super("onRoleEvent");
		this.type = type;
		this.role = role;
	}

	public ARole getRole() {
		return role;
	}

	public void setRole(ARole role) {
		this.role = role;
	}

	public RoleEventType getType() {
		return type;
	}
	

}