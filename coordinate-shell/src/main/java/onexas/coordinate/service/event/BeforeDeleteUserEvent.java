package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.User;

/**
 * 
 * @author Dennis Chen
 *
 */
public class BeforeDeleteUserEvent extends ApplicationEvent<User> {
	private static final long serialVersionUID = 1L;

	public BeforeDeleteUserEvent(User user) {
		super(user);
	}
}
