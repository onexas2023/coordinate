package onexas.axes.web.zk.ctrl.admin.user;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.AUser;
/**
 * 
 * @author Dennis Chen
 *
 */
public class UserEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	UserEventType type;
	AUser user;
	
	public UserEvent(UserEventType type, AUser user) {
		super("onUserEvent");
		this.type = type;
		this.user = user;
	}

	public AUser getUser() {
		return user;
	}

	public void setUser(AUser user) {
		this.user = user;
	}

	public UserEventType getType() {
		return type;
	}
	

}