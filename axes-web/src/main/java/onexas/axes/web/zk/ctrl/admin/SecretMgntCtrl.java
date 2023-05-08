package onexas.axes.web.zk.ctrl.admin;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.ctrl.admin.secret.SecretEvent;
import onexas.axes.web.zk.ctrl.admin.secret.SecretEventType;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.ASecret;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretMgntCtrl extends CtrlBase {
	
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
		veditorInc.setSrc("~@/axes/admin/secret/secretCreator.zul");
		workspace.publish(new SecretEvent(SecretEventType.START_TO_CREATE, null));
	}

	private void doWorkspaceEvent(Event evt) {
		if(evt instanceof SecretEvent) {
			ASecret secret = ((SecretEvent)evt).getSecret();
			switch(((SecretEvent)evt).getType()) {
			case CREATED:
			case DELETED:
			case UPDATED:
			case SELECTED:
				veditorInc.setDynamicProperty(Constants.ARG_EDITING_OBJ, secret);
				veditorInc.setSrc("~@/axes/admin/secret/secretEditor.zul");
				break;
			default:
			}
		}
	}


	

}
