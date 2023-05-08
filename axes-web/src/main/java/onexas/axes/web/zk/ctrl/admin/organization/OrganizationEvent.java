package onexas.axes.web.zk.ctrl.admin.organization;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.AOrganization;
/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	OrganizationEventType type;
	AOrganization organization;
	
	public OrganizationEvent(OrganizationEventType type, AOrganization organization) {
		super("onOrganizationEvent");
		this.type = type;
		this.organization = organization;
	}

	public AOrganization getOrganization() {
		return organization;
	}

	public void setOrganization(AOrganization organization) {
		this.organization = organization;
	}

	public OrganizationEventType getType() {
		return type;
	}
	

}