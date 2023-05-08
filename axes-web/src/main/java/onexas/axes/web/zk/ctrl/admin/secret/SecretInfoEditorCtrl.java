package onexas.axes.web.zk.ctrl.admin.secret;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.SecretWrap;
import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Messageboxes;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminSecretApi;
import onexas.coordinate.api.v1.sdk.model.ASecret;
import onexas.coordinate.api.v1.sdk.model.ASecretUpdate;
import onexas.coordinate.api.v1.sdk.model.UDomain;
import onexas.coordinate.common.lang.Objects;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class SecretInfoEditorCtrl extends CtrlBase {

	@Wire
	Label vuid;

	@Wire
	Label vcode;

	@Wire
	Textbox vdescription;

	@Wire
	Textbox vcontent;

	ASecret selectedSecret;

	boolean updateContent;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		selectedSecret = (ASecret) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onDelete", (evt) -> {
			doDelete();
		});
		mainComp.addEventListener("onToggleUpdateContent", (evt) -> {
			doToggleUpdateContent();
		});

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshEditor();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof SecretEvent) {
			ASecret secret = ((SecretEvent) evt).getSecret();
			switch (((SecretEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				if (secret == null || selectedSecret == null
						|| !Objects.equals(secret.getUid(), selectedSecret.getUid())) {
					vcontent.setValue("");
					vcontent.setPlaceholder("");
					vcontent.setReadonly(true);
					updateContent = false;
				}
				selectedSecret = secret;
				refreshEditor();
				break;
			case DELETED:
				selectedSecret = null;
				refreshEditor();
				break;
			default:
			}
		}
	}

	private void refreshEditor() {

		if (selectedSecret != null) {
			vuid.setValue(selectedSecret.getUid());
			vcode.setValue(selectedSecret.getCode());
			vcontent.setPlaceholder(Strings.format("{} : {}", Zks.getLabel("axes.secret.fingerprint"), selectedSecret.getFingerprint()));
			vdescription.setValue(selectedSecret.getDescription());
			vcontent.setReadonly(!updateContent);
		} else {
			vuid.setValue("");
			vcode.setValue("");
			vcontent.setPlaceholder("");
			vdescription.setValue("");

			updateContent = false;
			vcontent.setReadonly(true);
			vcontent.setValue("");
		}

	}

	private void doSave() {
		if (selectedSecret == null) {
			return;
		}
		String description = Zks.trimValue(vdescription);

		ComponentValidator validator = new ComponentValidator(mainComp);
		validator.validate(SecretWrap.class, "description", description, vdescription);

		if (!validator.isValid()) {
			return;
		}

		// don't trim content, keep just original
		String content = vcontent.getValue();

		CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(workspace.getApiClient());
		ASecret secret = api.updateSecret(selectedSecret.getUid(), new ASecretUpdate().description(description)
				.content(updateContent ? (content == null ? "" : content) : null));
		workspace.publish(new SecretEvent(SecretEventType.UPDATED, secret));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", secret.getCode()));

		vcontent.setValue("");
		vcontent.setPlaceholder(Strings.format("{} : {}", Zks.getLabel("axes.secret.fingerprint"), secret.getFingerprint()));
		vcontent.setReadonly(true);
		updateContent = false;
	}

	private void doDelete() {
		if (selectedSecret == null) {
			return;
		}
		final ASecret secret = selectedSecret;
		Messageboxes.showConfirmationBox(null, Zks.getLabelWithArg("axes.msg.confirmDelete", selectedSecret.getCode()),
				(evt) -> {
					Zks.showBusyThenRun(() -> {
						CoordinateAdminSecretApi api = new CoordinateAdminSecretApi(workspace.getApiClient());
						api.deleteSecret(secret.getUid(), false);
						workspace.publish(new SecretEvent(SecretEventType.DELETED, secret));
						Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyDeleted", secret.getCode()));
					});
				}, null);
	}

	private void doToggleUpdateContent() {
		vcontent.setReadonly(!(updateContent = !updateContent));
	}
}
