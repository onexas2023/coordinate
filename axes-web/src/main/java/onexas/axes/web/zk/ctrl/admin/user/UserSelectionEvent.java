package onexas.axes.web.zk.ctrl.admin.user;

import java.util.Set;

import org.zkoss.zk.ui.event.Event;

import onexas.axes.web.model.UserWrap;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserSelectionEvent extends Event {
	private static final long serialVersionUID = 1L;

	String requestId;
	Set<UserWrap> selectedUsers;

	public UserSelectionEvent(String requestId, Set<UserWrap> selectedUsers) {
		super("onUserSelection");
		this.requestId = requestId;
		this.selectedUsers = selectedUsers;
	}

	public String getRequestId() {
		return requestId;
	}

	public Set<UserWrap> getSelectedUsers() {
		return selectedUsers;
	}

}
