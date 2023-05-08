package onexas.axes.web.zk.ctrl.admin.role;

import java.util.Set;

import org.zkoss.zk.ui.event.Event;

import onexas.axes.web.model.RoleWrap;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleSelectionEvent extends Event {
	private static final long serialVersionUID = 1L;

	String requestId;
	Set<RoleWrap> selectedRoles;

	public RoleSelectionEvent(String requestId, Set<RoleWrap> selectedRoles) {
		super("onRoleSelection");
		this.requestId = requestId;
		this.selectedRoles = selectedRoles;
	}

	public String getRequestId() {
		return requestId;
	}

	public Set<RoleWrap> getSelectedRoles() {
		return selectedRoles;
	}

}
