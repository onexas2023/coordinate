package onexas.axes.web.zk.ctrl.admin.secret;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.SecretWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminSecretApi;
import onexas.coordinate.api.v1.sdk.model.ASecret;
import onexas.coordinate.api.v1.sdk.model.ASecretCreate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretCreatorCtrl extends CtrlBase {

	@Wire
	Textbox vcode;

	@Wire
	Textbox vdescription;

	@Wire
	Textbox vcontent;

	protected void afterCompose() throws Exception {

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onCancel", (evt) -> {
			doCancel();
		});
	}

	private void doCancel() {
		workspace.publish(new SecretEvent(SecretEventType.SELECTED, null));
	}

	private void doSave() {
		String code = Zks.trimValue(vcode);
		String description = Zks.trimValue(vdescription);
		// don't trim content, keep it just original
		String content = vcontent.getValue();

		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(SecretWrap.class, "code", code, vcode);
		validator.validate(SecretWrap.class, "description", description, vdescription);

		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(workspace.getApiClient());
		ASecret secret = api.createSecret(
				new ASecretCreate().code(code).description(description).content(content == null ? "" : content));
		workspace.publish(new SecretEvent(SecretEventType.CREATED, secret));

		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyCreated", secret.getCode()));
	}
}
