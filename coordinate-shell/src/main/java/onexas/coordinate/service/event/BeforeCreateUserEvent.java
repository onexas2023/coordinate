package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.User;

/**
 * 
 * @author Dennis Chen
 *
 */
public class BeforeCreateUserEvent extends ApplicationEvent<User> {
	private static final long serialVersionUID = 1L;

	public BeforeCreateUserEvent(User user) {
		super(user);
	}
}
