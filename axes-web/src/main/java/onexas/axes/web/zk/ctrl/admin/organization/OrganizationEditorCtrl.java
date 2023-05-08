package onexas.axes.web.zk.ctrl.admin.organization;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.component.SingleTabbox;
import onexas.axes.web.zk.component.SingleTabpanel;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.AOrganization;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationEditorCtrl extends CtrlBase {

	@Wire
	Component vhintbox;
	
	@Wire
	Tab vorganizationInfo;

	@Wire
	SingleTabbox vtabbox;
	
	@Wire
	SingleTabpanel vorganizationInfoEditor;

	@Wire
	SingleTabpanel vorganizationUsersEditor;
	
	
	AOrganization selectedOrganization;

	protected void afterCompose() throws Exception {

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof OrganizationEvent) {
			AOrganization organization = ((OrganizationEvent) evt).getOrganization();
			switch (((OrganizationEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedOrganization = organization;
				refreshEditor();
				break;
			case DELETED:
				selectedOrganization = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {
		vorganizationInfoEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedOrganization);
		vorganizationUsersEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedOrganization);
		
		if (selectedOrganization == null) {
			vorganizationInfo.setLabel("");
			vhintbox.setVisible(true);
			vtabbox.setVisible(false);
			vtabbox.clearInclude();
		}else {
			vorganizationInfo.setLabel(selectedOrganization.getName());
			vhintbox.setVisible(false);
			vtabbox.setVisible(true);
			vtabbox.invalidate();
		}
	}
}
