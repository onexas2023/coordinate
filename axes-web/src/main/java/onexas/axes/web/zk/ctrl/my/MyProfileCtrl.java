package onexas.axes.web.zk.ctrl.my;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import onexas.api.invoker.ApiException;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateProfileApi;
import onexas.coordinate.api.v1.sdk.model.UProfile;
import onexas.coordinate.api.v1.sdk.model.UProfileUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class MyProfileCtrl extends CtrlBase {

	@Wire
	Textbox vdisplayName;

	@Wire
	Textbox vemail;
	
	@Wire
	Button vsave;

	protected void afterCompose() throws Exception {

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});

		refreshEditor();
	}

	private void refreshEditor() {

		CoordinateProfileApi api = new CoordinateProfileApi(workspace.getApiClient());
		UProfile profile = api.getProfile();

		vdisplayName.setValue(profile.getDisplayName());
		vemail.setValue(profile.getEmail());
		
		boolean permission = workspace.hasPermission("coordinate-profile:modify"); 
		vdisplayName.setDisabled(!permission);
		vemail.setDisabled(!permission);
		vsave.setDisabled(!permission);

	}

	private void doSave() {

		String displayName = Zks.trimValue(vdisplayName);
		String email = Zks.trimValue(vemail);

		ComponentValidator validator = new ComponentValidator(mainComp);

		validator.validate(ValidBean.class, "displayName", displayName, vdisplayName);
		validator.validate(ValidBean.class, "email", email, vemail);

		if (!validator.isValid()) {
			return;
		}

		CoordinateProfileApi api = new CoordinateProfileApi(workspace.getApiClient());
		try {
			api.updateProfile(new UProfileUpdate().displayName(displayName).email(email));
			Zks.showClientNotification(
					Zks.getLabelWithArg("axes.msg.notifyUpdated", Zks.getLabel("axes.user.profile")));
		} catch (ApiException x) {
			if (x.getCode() == 403) {
				Zks.showClientWarning(Zks.getLabel("axes.msg.profileUpdateNoPermission"));
			} else {
				throw x;
			}
		}

		refreshEditor();
	}

	public static class ValidBean {
		@NotNull
		@Size(min = 1, max = 128)
		public String displayName;

		@Size(max = 256)
		@Pattern(regexp = "^\\w+((-\\w+)|(\\.\\w+))*@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z]+$", message = "l:axes.validation.emailFormatInvalidate")
		public String email;

	}

}
