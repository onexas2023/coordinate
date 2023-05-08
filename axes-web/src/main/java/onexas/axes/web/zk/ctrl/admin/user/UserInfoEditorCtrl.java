package onexas.axes.web.zk.ctrl.admin.user;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.UserWrap;
import onexas.axes.web.model.ValidationFields;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Messageboxes;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminUserApi;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserUpdate;
import onexas.coordinate.api.v1.sdk.model.UDomain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserInfoEditorCtrl extends CtrlBase {

	
	@Wire
	Label vuid;
	
	@Wire
	Label vaccount;

	@Wire
	Label vdomain;

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

	AUser selectedUser;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		selectedUser = (AUser) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

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
		if (evt instanceof UserEvent) {
			AUser user = ((UserEvent) evt).getUser();
			switch (((UserEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedUser = user;
				refreshEditor();
				break;
			case DELETED:
				selectedUser = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {
		vpassword.setValue("");
		vpwdagain.setValue("");

		if (selectedUser != null) {
			vuid.setValue(selectedUser.getUid());
			vaccount.setValue(selectedUser.getAccount());
			vdisplayName.setValue(selectedUser.getDisplayName());
			vemail.setValue(selectedUser.getEmail());
			vdomain.setValue(
					domainMap.containsKey(selectedUser.getDomain()) ? domainMap.get(selectedUser.getDomain()).getName()
							: "");
			vdisabled.setChecked(Boolean.TRUE.equals(selectedUser.getDisabled()));
		} else {
			vuid.setValue("");
			vaccount.setValue("");
			vdisplayName.setValue("");
			vemail.setValue("");
			vdomain.setValue("");
			vdisabled.setChecked(false);
		}
	}

	private void doSave() {
		if (selectedUser == null) {
			return;
		}
		String displayName = Zks.trimValue(vdisplayName);
		String password = Zks.trimValue(vpassword);
		String pwdagain = Zks.trimValue(vpwdagain);
		String email = Zks.trimValue(vemail);
		boolean disabled = vdisabled.isChecked();

		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(UserWrap.class, "displayName", displayName, vdisplayName);
		validator.validate(UserWrap.class, "email", email, vemail);

		if (!Strings.isBlank(password)) {
			validator.validate(ValidationFields.class, "password", password, vpassword);
			validator.validate(password.equals(pwdagain), vpwdagain,
					Zks.getLabel("axes.validation.passwordAgainInvalidate"));
		}
		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());
		AUser user = api.updateUser(selectedUser.getUid(), new AUserUpdate().displayName(displayName)
				.password(Strings.isBlank(password) ? null : password).email(email).disabled(disabled));
		workspace.publish(new UserEvent(UserEventType.UPDATED, user));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", user.getDisplayName()));
	}

	private void doDelete() {
		if (selectedUser == null) {
			return;
		}
		final AUser user = selectedUser;
		Messageboxes.showConfirmationBox(null,
				Zks.getLabelWithArg("axes.msg.confirmDelete", selectedUser.getDisplayName()), (evt) -> {
					Zks.showBusyThenRun(() -> {
						CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());
						api.deleteUser(user.getUid(), false);
						workspace.publish(new UserEvent(UserEventType.DELETED, user));
						Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyDeleted", user.getDisplayName()));
					});
				}, null);
	}
}
