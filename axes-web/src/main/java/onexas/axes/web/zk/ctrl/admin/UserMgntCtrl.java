package onexas.axes.web.zk.ctrl.admin;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.ctrl.admin.user.UserEvent;
import onexas.axes.web.zk.ctrl.admin.user.UserEventType;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.AUser;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserMgntCtrl extends CtrlBase {
	
	@Wire
	Include veditorInc;

	protected void beforeComposeChildren()throws Exception {
		mainComp.setAttribute(Constants.ATTR_PAGE_CONTROLLER, this);
		mainComp.setAttribute(Constants.ATTR_PAGE_CONTAINER, mainComp);
	}
	
	protected void afterCompose() throws Exception {
		mainComp.addEventListener("onNew", (evt)->{
			doNew();
		});
		
		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});
	}

	private void doNew() {
		veditorInc.setDynamicProperty(Constants.ARG_EDITING_OBJ, null);
		veditorInc.setSrc("~@/axes/admin/user/userCreator.zul");
		workspace.publish(new UserEvent(UserEventType.START_TO_CREATE, null));
	}

	private void doWorkspaceEvent(Event evt) {
		if(evt instanceof UserEvent) {
			AUser user = ((UserEvent)evt).getUser();
			switch(((UserEvent)evt).getType()) {
			case CREATED:
			case DELETED:
			case UPDATED:
			case SELECTED:
				veditorInc.setDynamicProperty(Constants.ARG_EDITING_OBJ, user);
				veditorInc.setSrc("~@/axes/admin/user/userEditor.zul");
				break;
			default:
			}
		}
	}


	

}
