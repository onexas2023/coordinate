package onexas.axes.web.zk.ctrl.admin.organization;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.OrganizationWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Messageboxes;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminOrganizationApi;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationInfoEditorCtrl extends CtrlBase {

	@Wire
	Label vuid;
	
	@Wire
	Label vcode;

	@Wire
	Textbox vname;

	@Wire
	Textbox vdescription;

	AOrganization selectedOrganization;

	protected void afterCompose() throws Exception {

		selectedOrganization = (AOrganization) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

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

		if (selectedOrganization != null) {
			vuid.setValue(selectedOrganization.getUid());
			vcode.setValue(selectedOrganization.getCode());
			vname.setValue(selectedOrganization.getName());
			vdescription.setValue(selectedOrganization.getDescription());
		} else {
			vuid.setValue("");
			vcode.setValue("");
			vname.setValue("");
			vdescription.setValue("");
		}
	}

	private void doSave() {
		if (selectedOrganization == null) {
			return;
		}
		String name = Zks.trimValue(vname);
		String description = Zks.trimValue(vdescription);

		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(OrganizationWrap.class, "name", name, vname);
		validator.validate(OrganizationWrap.class, "description", description, vdescription);

		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());
		AOrganization organization = api.updateOrganization(selectedOrganization.getUid(), new AOrganizationUpdate().name(name).description(description));
		workspace.publish(new OrganizationEvent(OrganizationEventType.UPDATED, organization));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", organization.getName()));
	}

	private void doDelete() {
		if (selectedOrganization == null) {
			return;
		}
		final AOrganization organization = selectedOrganization;
		Messageboxes.showConfirmationBox(null,
				Zks.getLabelWithArg("axes.msg.confirmDelete", selectedOrganization.getName()), (evt) -> {
					Zks.showBusyThenRun(() -> {
						CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());
						api.deleteOrganization(organization.getUid(), false);
						workspace.publish(new OrganizationEvent(OrganizationEventType.DELETED, organization));
						Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyDeleted", organization.getName()));
					});
				}, null);
	}
}
