package onexas.axes.web.zk.ctrl.admin.role;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.RoleWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Messageboxes;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminRoleApi;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleInfoEditorCtrl extends CtrlBase {

	@Wire
	Label vuid;
	
	@Wire
	Label vcode;

	@Wire
	Textbox vname;

	@Wire
	Textbox vdescription;

	ARole selectedRole;

	protected void afterCompose() throws Exception {

		selectedRole = (ARole) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onDelete", (evt) -> {
			doDelete();
		});

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshEditor();
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

		if (selectedRole != null) {
			vuid.setValue(selectedRole.getUid());
			vcode.setValue(selectedRole.getCode());
			vname.setValue(selectedRole.getName());
			vdescription.setValue(selectedRole.getDescription());
		} else {
			vuid.setValue("");
			vcode.setValue("");
			vname.setValue("");
			vdescription.setValue("");
		}
	}

	private void doSave() {
		if (selectedRole == null) {
			return;
		}
		String name = Zks.trimValue(vname);
		String description = Zks.trimValue(vdescription);

		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(RoleWrap.class, "name", name, vname);
		validator.validate(RoleWrap.class, "description", description, vdescription);

		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
		ARole role = api.updateRole(selectedRole.getUid(), new ARoleUpdate().name(name).description(description));
		workspace.publish(new RoleEvent(RoleEventType.UPDATED, role));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", role.getName()));
	}

	private void doDelete() {
		if (selectedRole == null) {
			return;
		}
		final ARole role = selectedRole;
		Messageboxes.showConfirmationBox(null,
				Zks.getLabelWithArg("axes.msg.confirmDelete", selectedRole.getName()), (evt) -> {
					Zks.showBusyThenRun(() -> {
						CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
						api.deleteRole(role.getUid(), false);
						workspace.publish(new RoleEvent(RoleEventType.DELETED, role));
						Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyDeleted", role.getName()));
					});
				}, null);
	}
}
