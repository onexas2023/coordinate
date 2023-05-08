package onexas.axes.web.zk.ctrl.admin.setting;

import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminSettingApi;

/**
 * 
 * @author Dennis Chen
 *
 */
public class MiscSettingCtrl extends CtrlBase {

	protected void afterCompose() throws Exception {

		mainComp.addEventListener("onCleanCache", (evt) -> {
			doCleanCache();
		});
	}

	private void doCleanCache() {
		CoordinateAdminSettingApi api = new CoordinateAdminSettingApi(workspace.getApiClient());
		api.cleanCache();
		Zks.showClientNotification(
				Zks.getLabelWithArg("axes.msg.notifyUpdated", Zks.getLabel("axes.cleanCache")));
	}

}
