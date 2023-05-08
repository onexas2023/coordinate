package onexas.axes.web.zk.ctrl.my;

import org.apache.logging.log4j.util.Strings;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.api.invoker.ApiException;
import onexas.axes.web.model.ValidationFields;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateProfileApi;
import onexas.coordinate.api.v1.sdk.model.UPasswordUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class MyPasswordCtrl extends CtrlBase {

	@Wire
	Textbox voldPassword;
	
	@Wire
	Textbox vpassword;
	
	@Wire
	Textbox vpasswordAgain;

	protected void afterCompose() throws Exception {

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});

		refreshEditor();
	}

	private void refreshEditor() {
		voldPassword.setValue("");
		vpassword.setValue("");
		vpasswordAgain.setValue("");
	}

	private void doSave() {
		
		String oldPassword = Zks.trimValue(voldPassword);
		String password = Zks.trimValue(vpassword);
		String passwordAgain = Zks.trimValue(vpasswordAgain);

		ComponentValidator validator = new ComponentValidator(mainComp);
		
		validator.validate(!Strings.isBlank(oldPassword), voldPassword, Zks.getLabel("axes.msg.blankField"));
		
		if (validator.validate(!Strings.isBlank(password), vpassword, Zks.getLabel("axes.msg.blankField"))) {
			validator.validate(ValidationFields.class, "password", password, vpassword);
		}
		if(!validator.isValid()) {
			return;
		}
		validator.validate(password.equals(passwordAgain), vpasswordAgain, Zks.getLabel("axes.validation.passwordAgainInvalidate"));
		if(!validator.isValid()) {
			return;
		}
		
		CoordinateProfileApi api = new CoordinateProfileApi(workspace.getApiClient());
		try {
			api.updatePassword(new UPasswordUpdate().oldPassword(oldPassword).newPassword(password));
			Zks.showClientNotification(
				Zks.getLabelWithArg("axes.msg.notifyUpdated", Zks.getLabel("axes.user.password")));
		}catch(ApiException x) {
			if(x.getCode()==403) {
				Zks.showClientWarning(Zks.getLabel("axes.msg.passwordUpdateNoPermission"));
			}else {
				throw x;
			}
		}

		refreshEditor();
	}

}
