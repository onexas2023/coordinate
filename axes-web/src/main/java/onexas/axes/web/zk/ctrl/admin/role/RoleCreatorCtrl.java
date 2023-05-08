package onexas.axes.web.zk.ctrl.admin.role;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.RoleWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminRoleApi;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleCreate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleCreatorCtrl extends CtrlBase {

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
		workspace.publish(new RoleEvent(RoleEventType.SELECTED, null));
	}

	private void doSave() {
		String code = Zks.trimValue(vcode);
		String name = Zks.trimValue(vname);
		String description = Zks.trimValue(vdescription);
		
		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(RoleWrap.class, "code", code, vcode);
		validator.validate(RoleWrap.class, "name", name, vname);
		validator.validate(RoleWrap.class, "description", description, vdescription);
		if(!validator.isValid()) {
			return;
		}
		
		CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
		ARole role = api.createRole(new ARoleCreate().code(code).name(name).description(description));
		workspace.publish(new RoleEvent(RoleEventType.CREATED, role));
		
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyCreated", role.getName()));
	}
}
