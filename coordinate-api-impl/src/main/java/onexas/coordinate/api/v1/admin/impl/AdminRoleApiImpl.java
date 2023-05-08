package onexas.coordinate.api.v1.admin.impl;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminRoleApi;
import onexas.coordinate.api.v1.admin.model.APrincipalPermission;
import onexas.coordinate.api.v1.admin.model.ARole;
import onexas.coordinate.api.v1.admin.model.ARoleCreate;
import onexas.coordinate.api.v1.admin.model.ARoleFilter;
import onexas.coordinate.api.v1.admin.model.ARoleListPage;
import onexas.coordinate.api.v1.admin.model.ARoleUpdate;
import onexas.coordinate.api.v1.admin.model.ARoleUserFilter;
import onexas.coordinate.api.v1.admin.model.AUser;
import onexas.coordinate.api.v1.admin.model.AUserListPage;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.IntegrityViolationException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.User;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminRoleApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = { AdminRoleApi.ACTION_VIEW,
		AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
public class AdminRoleApiImpl extends ApiImplBase implements AdminRoleApi {

	@Autowired
	RoleService roleService;

	public AdminRoleApiImpl() {
		super(AdminRoleApi.API_NAME, V1, AdminRoleApi.API_URI);
	}

	@Override
	public ARole getRole(String uid, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? roleService.find(uid) : roleService.get(uid), ARole.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ARole createRole(ARoleCreate roleCreate) {
		Role m = roleService.create(roleCreate);
		return Jsons.transform(m, ARole.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public ARole updateRole(String uid, ARoleUpdate roleUpdate) {
		Role m = roleService.update(uid, roleUpdate);
		return Jsons.transform(m, ARole.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteRole(String uid, Boolean quiet) {

		Role role = roleService.find(uid);
		if (role == null) {
			if (!Boolean.TRUE.equals(quiet)) {
				throw new NotFoundException("role {} not found", uid);
			}
			return new Response();
		}

		if (!AppContext.config().getBoolean("app.allowDeleteAdminRole", false)) {
			if (role.getCode().equals(AppContext.config().getString("app.adminRole"))) {
				throw new IntegrityViolationException("not allow to delete admin role {}", role.getCode());
			}
		}

		roleService.delete(uid, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response addRoleUser(String uid, List<String> userUidList) {

		roleService.addUsers(uid, new LinkedHashSet<>(userUidList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response setRoleUser(String uid, List<String> userUidList) {

		roleService.setUsers(uid, new LinkedHashSet<>(userUidList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_MODIFY, AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response removeRoleUser(String uid, List<String> userUidList) {

		roleService.removeUsers(uid, new LinkedHashSet<>(userUidList));

		return new Response();
	}

	@Override
	public List<APrincipalPermission> listRolePermission(String uid) {
		List<PrincipalPermission> list = roleService.listPermission(uid);
		return Jsons.transform(list, new TypeReference<List<APrincipalPermission>>() {
		});
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response addRolePermission(String uid, List<APrincipalPermission> rolePermissionList) {

		roleService.addPermissions(uid, new LinkedHashSet<>(rolePermissionList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response setRolePermission(String uid, List<APrincipalPermission> rolePermissionList) {

		roleService.setPermissions(uid, new LinkedHashSet<>(rolePermissionList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminRoleApi.API_PERMISSION_TARGET, action = {
			AdminRoleApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response removeRolePermission(String uid, List<APrincipalPermission> rolePermissionList) {

		roleService.removePermissions(uid, new LinkedHashSet<>(rolePermissionList));

		return new Response();
	}

	@Override
	public ARoleListPage listRole(ARoleFilter filter) {
		ListPage<Role> r = roleService.list(filter);
		return new ARoleListPage(Jsons.transform(r.getItems(), new TypeReference<List<ARole>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}

	@Override
	public AUserListPage listRoleUser(String uid, ARoleUserFilter filter) {
		ListPage<User> r = roleService.listUser(uid, filter);
		return new AUserListPage(Jsons.transform(r.getItems(), new TypeReference<List<AUser>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}
}