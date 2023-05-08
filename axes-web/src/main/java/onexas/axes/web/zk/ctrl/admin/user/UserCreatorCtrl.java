package onexas.axes.web.zk.ctrl.admin.user;

import org.apache.logging.log4j.util.Strings;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.UserWrap;
import onexas.axes.web.model.ValidationFields;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminUserApi;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserCreate;
import onexas.coordinate.api.v1.sdk.model.UDomain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserCreatorCtrl extends CtrlBase {

	@Wire
	Textbox vaccount;
	
	@Wire
	Textbox vdomain;
	
	@Wire
	Textbox vdisplayName;
	
	@Wire
	Textbox vpassword;
	
	@Wire
	Textbox vpwdagain;
	
	@Wire
	Checkbox vdisabled;
	
	@Wire
	Textbox vemail;


	protected void afterCompose() throws Exception {
		
		for(UDomain domain:workspace.getMetainfo().getDomains()) {
			if(Constants.DOMAIN_LOCAL.equals(domain.getCode())){				
				vdomain.setValue(domain.getName());
			}
		}
		
		mainComp.addEventListener("onSave", (evt)->{
			doSave();
		});
		mainComp.addEventListener("onCancel", (evt)->{
			doCancel();
		});
	}
	
	private void doCancel() {
		workspace.publish(new UserEvent(UserEventType.SELECTED, null));
	}

	private void doSave() {
		String account = Zks.trimValue(vaccount);
		String displayName = Zks.trimValue(vdisplayName);
		String password = Zks.trimValue(vpassword);
		String pwdagain = Zks.trimValue(vpwdagain);
		String email = Zks.trimValue(vemail);
		boolean disabled = vdisabled.isChecked();
		
		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(UserWrap.class, "account", account, vaccount);
		validator.validate(UserWrap.class, "displayName", displayName, vdisplayName);
		validator.validate(UserWrap.class, "email", email, vemail);
		if (validator.validate(!Strings.isBlank(password), vpassword, Zks.getLabel("axes.msg.blankField"))) {
			validator.validate(ValidationFields.class, "password", password, vpassword);
		}
		if(!validator.isValid()) {
			return;
		}
		validator.validate(password.equals(pwdagain), vpwdagain, Zks.getLabel("axes.validation.passwordAgainInvalidate"));
		if(!validator.isValid()) {
			return;
		}
		
		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());
		AUser user = api.createUser(new AUserCreate().account(account).displayName(displayName).password(password).email(email).disabled(disabled));
		workspace.publish(new UserEvent(UserEventType.CREATED, user));
		
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyCreated", user.getDisplayName()));
	}
}
