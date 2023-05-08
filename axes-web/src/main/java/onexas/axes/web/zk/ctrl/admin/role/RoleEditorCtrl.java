package onexas.axes.web.zk.ctrl.admin.role;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.component.SingleTabbox;
import onexas.axes.web.zk.component.SingleTabpanel;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.ARole;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleEditorCtrl extends CtrlBase {

	@Wire
	Component vhintbox;
	
	@Wire
	Tab vroleInfo;

	@Wire
	SingleTabbox vtabbox;
	
	@Wire
	SingleTabpanel vroleInfoEditor;

	@Wire
	SingleTabpanel vroleUsersEditor;
	
	@Wire
	SingleTabpanel vrolePermissionsEditor;
	
	ARole selectedRole;

	protected void afterCompose() throws Exception {

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof RoleEvent) {
			ARole role = ((RoleEvent) evt).getRole();
			switch (((RoleEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedRole = role;
				refreshEditor();
				break;
			case DELETED:
				selectedRole = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {
		vroleInfoEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedRole);
		vroleUsersEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedRole);
		vrolePermissionsEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedRole);
		
		if (selectedRole == null) {
			vroleInfo.setLabel("");
			vhintbox.setVisible(true);
			vtabbox.setVisible(false);
			vtabbox.clearInclude();
		}else {
			vroleInfo.setLabel(selectedRole.getName());
			vhintbox.setVisible(false);
			vtabbox.setVisible(true);
			vtabbox.invalidate();
		}
	}
}
