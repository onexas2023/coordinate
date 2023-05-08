package onexas.axes.web.zk.ctrl.admin.domain;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Tab;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.component.SingleTabbox;
import onexas.axes.web.zk.component.SingleTabpanel;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.coordinate.api.v1.sdk.model.ADomain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainEditorCtrl extends CtrlBase {

	@Wire
	Component vhintbox;
	
	@Wire
	Tab vdomainInfo;

	@Wire
	SingleTabbox vtabbox;
	
	@Wire
	SingleTabpanel vdomainInfoEditor;

	@Wire
	SingleTabpanel vdomainUsersEditor;
	
	
	ADomain selectedDomain;

	protected void afterCompose() throws Exception {

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof DomainEvent) {
			ADomain domain = ((DomainEvent) evt).getDomain();
			switch (((DomainEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedDomain = domain;
				refreshEditor();
				break;
			case DELETED:
				selectedDomain = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {
		vdomainInfoEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedDomain);
		vdomainUsersEditor.setDynamicProperty(Constants.ARG_EDITING_OBJ, selectedDomain);
		
		if (selectedDomain == null) {
			vdomainInfo.setLabel("");
			vhintbox.setVisible(true);
			vtabbox.setVisible(false);
			vtabbox.clearInclude();
		}else {
			vdomainInfo.setLabel(selectedDomain.getName());
			vhintbox.setVisible(false);
			vtabbox.setVisible(true);
			vtabbox.invalidate();
		}
	}
}
