package onexas.axes.web.zk.ctrl.admin.user;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.component.SingleTabbox;
import onexas.axes.web.zk.component.SingleTabpanel;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.AUser;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserEditorCtrl extends CtrlBase {

	@Wire
	Component vhintbox;
	
	@Wire
	Tab vuserInfo;

	@Wire
	SingleTabbox vtabbox;
	
	@Wire
	SingleTabpanel vuserInfoEditor;

	@Wire
	SingleTabpanel vuserRolesEditor;
	
	AUser selectedUser;

	protected void afterCompose() throws Exception {

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof UserEvent) {
			AUser user = ((UserEvent) evt).getUser();
			switch (((UserEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedUser = user;
				refreshEditor();
				break;
			case DELETED:
				selectedUser = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {
		vuserInfoEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedUser);
		vuserRolesEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedUser);
		if (selectedUser == null) {
			vuserInfo.setLabel("");
			vhintbox.setVisible(true);
			vtabbox.setVisible(false);
			vtabbox.clearInclude();
		}else {
			vuserInfo.setLabel(selectedUser.getDisplayName());
			vhintbox.setVisible(false);
			vtabbox.setVisible(true);
			vtabbox.invalidate();
		}
	}
}
