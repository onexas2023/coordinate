package onexas.axes.web.zk.ctrl.admin.secret;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.component.SingleTabbox;
import onexas.axes.web.zk.component.SingleTabpanel;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.ASecret;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretEditorCtrl extends CtrlBase {

	@Wire
	Component vhintbox;

	@Wire
	Tab vsecretInfo;

	@Wire
	SingleTabbox vtabbox;

	@Wire
	SingleTabpanel vsecretInfoEditor;

	@Wire
	SingleTabpanel vsecretRolesEditor;

	ASecret selectedSecret;

	protected void afterCompose() throws Exception {

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof SecretEvent) {
			ASecret secret = ((SecretEvent) evt).getSecret();
			switch (((SecretEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedSecret = secret;
				refreshEditor();
				break;
			case DELETED:
				selectedSecret = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {
		vsecretInfoEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedSecret);
		vsecretRolesEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedSecret);
		if (selectedSecret == null) {
			vsecretInfo.setLabel("");
			vhintbox.setVisible(true);
			vtabbox.setVisible(false);
			vtabbox.clearInclude();
		} else {
			vsecretInfo.setLabel(selectedSecret.getCode());
			vhintbox.setVisible(false);
			vtabbox.setVisible(true);
			vtabbox.invalidate();
		}
	}
}
