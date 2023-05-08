package onexas.coordinate.api.v1.admin.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminSettingApi;
import onexas.coordinate.api.v1.admin.model.AServerSetting;
import onexas.coordinate.api.v1.admin.model.AServerSettingUpdate;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.ServerSetting;
import onexas.coordinate.service.SettingService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminSettingApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminSettingApi.API_PERMISSION_TARGET, action = {
		AdminSettingApi.ACTION_VIEW, AdminSettingApi.ACTION_MODIFY, AdminSettingApi.ACTION_ADMIN }))
public class AdminSettingApiImpl extends ApiImplBase implements AdminSettingApi {

	@Autowired
	SettingService settingService;

	public AdminSettingApiImpl() {
		super(AdminSettingApi.API_NAME, V1, AdminSettingApi.API_URI);
	}

	@Override
	public AServerSetting getServerSetting() {
		return Jsons.transform(settingService.getServerSetting(), AServerSetting.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminSettingApi.API_PERMISSION_TARGET, action = {
			AdminSettingApi.ACTION_MODIFY, AdminSettingApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AServerSetting updateServerSetting(AServerSettingUpdate serverSettingUpdate) {
		ServerSetting m = settingService.updateServerSetting(serverSettingUpdate);
		return Jsons.transform(m, AServerSetting.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminSettingApi.API_PERMISSION_TARGET, action = {
			AdminSettingApi.ACTION_MODIFY, AdminSettingApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AServerSetting resetServerSetting() {
		ServerSetting m = settingService.resetServerSetting();
		return Jsons.transform(m, AServerSetting.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminSettingApi.API_PERMISSION_TARGET, action = {
			AdminSettingApi.ACTION_MODIFY, AdminSettingApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response cleanCache() {
		settingService.cleanCache();
		return new Response();
	}
}