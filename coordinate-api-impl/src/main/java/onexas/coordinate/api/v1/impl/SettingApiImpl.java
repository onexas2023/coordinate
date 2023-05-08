package onexas.coordinate.api.v1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.SettingApi;
import onexas.coordinate.api.v1.model.UServerSetting;
import onexas.coordinate.api.v1.model.USetting;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.service.SettingService;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "SettingApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = SettingApi.API_PERMISSION_TARGET, action = { SettingApi.ACTION_VIEW,
		SettingApi.ACTION_MODIFY }))
public class SettingApiImpl extends ApiImplBase implements SettingApi {

	@Autowired
	SettingService settingService;

	public SettingApiImpl() {
		super(SettingApi.API_NAME, V1, SettingApi.API_URI);
	}

	@Override
	public USetting getSetting() {
		USetting setting = new USetting();
		setting.setServer(getServerSetting());
		return setting;
	}

	@Override
	public UServerSetting getServerSetting() {
		return Jsons.transform(settingService.getServerSetting(), UServerSetting.class);
	}
}