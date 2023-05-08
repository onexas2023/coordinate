package onexas.coordinate.api.v1.admin.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminHookApi;
import onexas.coordinate.api.v1.admin.model.AHook;
import onexas.coordinate.api.v1.admin.model.AHookCreate;
import onexas.coordinate.api.v1.admin.model.AHookFilter;
import onexas.coordinate.api.v1.admin.model.AHookListPage;
import onexas.coordinate.api.v1.admin.model.AHookUpdate;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Hook;
import onexas.coordinate.service.HookService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminHookApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminHookApi.API_PERMISSION_TARGET, action = { AdminHookApi.ACTION_VIEW,
		AdminHookApi.ACTION_MODIFY, AdminHookApi.ACTION_ADMIN }))
public class AdminHookApiImpl extends ApiImplBase implements AdminHookApi {

	@Autowired
	HookService hookService;

	public AdminHookApiImpl() {
		super(AdminHookApi.API_NAME, V1, AdminHookApi.API_URI);
	}

	@Override
	public AHookListPage listHook(AHookFilter filter) {
		ListPage<Hook> r = hookService.list(filter);
		return new AHookListPage(Jsons.transform(r.getItems(), new TypeReference<List<AHook>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}

	@Override
	public AHook getHook(String uid, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? hookService.find(uid) : hookService.get(uid), AHook.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminHookApi.API_PERMISSION_TARGET, action = {
			AdminHookApi.ACTION_MODIFY, AdminHookApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AHook createHook(AHookCreate hookCreate) {
		Hook m = hookService.create(hookCreate);
		return Jsons.transform(m, AHook.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminHookApi.API_PERMISSION_TARGET, action = {
			AdminHookApi.ACTION_MODIFY, AdminHookApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AHook updateHook(String uid, AHookUpdate hookUpdate) {
		Hook m = hookService.update(uid, hookUpdate);
		return Jsons.transform(m, AHook.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminHookApi.API_PERMISSION_TARGET, action = {
			AdminHookApi.ACTION_MODIFY, AdminHookApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteHook(String uid, Boolean quiet) {
		hookService.delete(uid, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminHookApi.API_PERMISSION_TARGET, action = {
			AdminHookApi.ACTION_MODIFY, AdminHookApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AHook triggerHook(String uid) {
		return triggerHookWithArgs(uid, null);
	}
	
	@Override
	@GrantPermissions(@GrantPermission(target = AdminHookApi.API_PERMISSION_TARGET, action = {
			AdminHookApi.ACTION_MODIFY, AdminHookApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AHook triggerHookWithArgs(String uid, Map<String, Object> args) {
		Hook m = hookService.trigger(uid, args, null);
		return Jsons.transform(m, AHook.class);
	}
}