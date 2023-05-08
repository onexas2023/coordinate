package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Organization;

/**
 * 
 * @author Dennis Chen
 *
 */
public class BeforeDeleteOrganizationEvent extends ApplicationEvent<Organization> {
	private static final long serialVersionUID = 1L;

	public BeforeDeleteOrganizationEvent(Organization organization) {
		super(organization);
	}

}
