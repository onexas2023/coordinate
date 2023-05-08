package onexas.axes.web.zk.ctrl.admin.setting;

import java.util.TimeZone;

import org.apache.logging.log4j.util.Strings;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.axes.web.zk.util.ComponentValidator;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminSettingApi;
import onexas.coordinate.api.v1.sdk.model.AServerSetting;
import onexas.coordinate.api.v1.sdk.model.AServerSettingUpdate;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ServerSettingCtrl extends CtrlBase {

	@Wire
	Textbox vadminEmail;
	@Wire
	Textbox vconsoleUrl;
	@Wire
	Textbox vapiBaseUrl;
	@Wire
	Textbox vapiInternalBaseUrl;
	

	AServerSetting serverSetting;

	protected void afterCompose() throws Exception {

		serverSetting = new CoordinateAdminSettingApi(workspace.getApiClient()).getServerSetting();

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		
		refreshEditor();
	}

	private void refreshEditor() {
		vadminEmail.setValue(serverSetting.getAdminEmail());
		vconsoleUrl.setValue(serverSetting.getConsoleUrl());
		vapiBaseUrl.setValue(serverSetting.getApiBaseUrl());
		vapiInternalBaseUrl.setValue(serverSetting.getApiInternalBaseUrl());
	}

	private void doSave() {

		String adminEmail = Zks.trimValue(vadminEmail);
		String consoleUrl = Zks.trimValue(vconsoleUrl);
		String apiBaseUrl = Zks.trimValue(vapiBaseUrl);
		String apiInternalBaseUrl = Zks.trimValue(vapiInternalBaseUrl);

		ComponentValidator validator = new ComponentValidator(mainComp);

		validator.validate(!Strings.isBlank(adminEmail), vadminEmail, Zks.getLabel("axes.msg.blankField"));
		validator.validate(!Strings.isBlank(consoleUrl), vconsoleUrl, Zks.getLabel("axes.msg.blankField"));
		validator.validate(!Strings.isBlank(apiBaseUrl), vapiBaseUrl, Zks.getLabel("axes.msg.blankField"));
		validator.validate(!Strings.isBlank(apiInternalBaseUrl), vapiInternalBaseUrl, Zks.getLabel("axes.msg.blankField"));

		if (!validator.isValid()) {
			return;
		}

		CoordinateAdminSettingApi api = new CoordinateAdminSettingApi(workspace.getApiClient());
		serverSetting = api.updateServerSetting(new AServerSettingUpdate().adminEmail(adminEmail).consoleUrl(consoleUrl)
				.apiBaseUrl(apiBaseUrl).apiInternalBaseUrl(apiInternalBaseUrl));

		Zks.showClientNotification(
				Zks.getLabelWithArg("axes.msg.notifyPreformed", Zks.getLabel("axes.serverSetting")));

		refreshEditor();
	}
	
	public String getDisplayName(TimeZone tz) {
		return tz.getDisplayName(workspace.getPreferredLocale())+"("+tz.getID()+")";
	}

}
