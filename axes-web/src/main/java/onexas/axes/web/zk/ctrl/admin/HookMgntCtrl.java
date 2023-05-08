package onexas.axes.web.zk.ctrl.admin;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.ctrl.admin.hook.HookEvent;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.AHook;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookMgntCtrl extends CtrlBase {
	
	@Wire
	Include vlistInc;

	@Wire
	Include vinfoInc;
	
	protected void beforeComposeChildren()throws Exception {
		mainComp.setAttribute(Constants.ATTR_PAGE_CONTROLLER, this);
		mainComp.setAttribute(Constants.ATTR_PAGE_CONTAINER, mainComp);
	}
	
	protected void afterCompose() throws Exception {
		
		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});
	}

	private void doWorkspaceEvent(Event evt) {
		if(evt instanceof HookEvent) {
			AHook hook = ((HookEvent)evt).getHook();
			switch(((HookEvent)evt).getType()) {
			case DELETED:
			case SELECTED:
				vinfoInc.setDynamicProperty(Constants.ARG_EDITING_OBJ, hook);
				if(hook==null) {
					vinfoInc.setVisible(false);
					vinfoInc.setSrc(null);
				}else {
					vinfoInc.setVisible(true);
					vinfoInc.setSrc("~@/axes/admin/hook/hookInfo.zul");
				}
				break;
			default:
			}
		}
	}


	

}
