package onexas.coordinate.service.event;



import onexas.coordinate.common.app.ApplicationEvent;
import onexas.coordinate.model.User;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DisabledUserEvent extends ApplicationEvent<User> {
	private static final long serialVersionUID = 1L;

	long createdDateTime;
	
	public DisabledUserEvent(User user) {
		super(user);
		createdDateTime = System.currentTimeMillis();
	}
	
	public long getCreatedDateTime() {
		return createdDateTime;
	}

}
