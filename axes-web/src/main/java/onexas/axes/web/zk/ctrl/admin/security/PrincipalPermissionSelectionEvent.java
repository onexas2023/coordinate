package onexas.axes.web.zk.ctrl.admin.security;

import java.util.Set;

import org.zkoss.zk.ui.event.Event;

import onexas.axes.web.model.PrincipalPermissionWrap;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PrincipalPermissionSelectionEvent extends Event {
	private static final long serialVersionUID = 1L;

	String requestId;
	Set<PrincipalPermissionWrap> selectedPrincipalPermissions;

	public PrincipalPermissionSelectionEvent(String requestId, Set<PrincipalPermissionWrap> selectedPrincipalPermissions) {
		super("onPrincipalPermissionSelection");
		this.requestId = requestId;
		this.selectedPrincipalPermissions = selectedPrincipalPermissions;
	}

	public String getRequestId() {
		return requestId;
	}

	public Set<PrincipalPermissionWrap> getSelectedPrincipalPermissions() {
		return selectedPrincipalPermissions;
	}

}
