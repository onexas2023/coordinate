package onexas.axes.web.zk.ctrl.admin.organization;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.OrganizationWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminOrganizationApi;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationCreate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationCreatorCtrl extends CtrlBase {

	@Wire
	Textbox vcode;
	
	@Wire
	Textbox vname;
	
	@Wire
	Textbox vdescription;
	


	protected void afterCompose() throws Exception {
		
		mainComp.addEventListener("onSave", (evt)->{
			doSave();
		});
		mainComp.addEventListener("onCancel", (evt)->{
			doCancel();
		});
	}
	
	private void doCancel() {
		workspace.publish(new OrganizationEvent(OrganizationEventType.SELECTED, null));
	}

	private void doSave() {
		String code = Zks.trimValue(vcode);
		String name = Zks.trimValue(vname);
		String description = Zks.trimValue(vdescription);
		
		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(OrganizationWrap.class, "code", code, vcode);
		validator.validate(OrganizationWrap.class, "name", name, vname);
		validator.validate(OrganizationWrap.class, "description", description, vdescription);
		if(!validator.isValid()) {
			return;
		}
		
		CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());
		AOrganization organization = api.createOrganization(new AOrganizationCreate().code(code).name(name).description(description));
		workspace.publish(new OrganizationEvent(OrganizationEventType.CREATED, organization));
		
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyCreated", organization.getName()));
	}
}
