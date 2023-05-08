package onexas.axes.web.zk.ctrl.admin.organization;

import java.util.Set;

import org.zkoss.zk.ui.event.Event;

import onexas.axes.web.model.OrganizationWrap;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationSelectionEvent extends Event {
	private static final long serialVersionUID = 1L;

	String requestId;
	Set<OrganizationWrap> selectedOrganizations;

	public OrganizationSelectionEvent(String requestId, Set<OrganizationWrap> selectedOrganizations) {
		super("onOrganizationSelection");
		this.requestId = requestId;
		this.selectedOrganizations = selectedOrganizations;
	}

	public String getRequestId() {
		return requestId;
	}

	public Set<OrganizationWrap> getSelectedOrganizations() {
		return selectedOrganizations;
	}

}
