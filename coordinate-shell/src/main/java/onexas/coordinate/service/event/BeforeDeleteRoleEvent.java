package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.Role;

/**
 * 
 * @author Dennis Chen
 *
 */
public class BeforeDeleteRoleEvent extends ApplicationEvent<Role> {
	private static final long serialVersionUID = 1L;

	public BeforeDeleteRoleEvent(Role role) {
		super(role);
	}

}
