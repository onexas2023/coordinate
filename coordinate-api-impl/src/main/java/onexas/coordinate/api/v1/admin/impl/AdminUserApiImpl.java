package onexas.coordinate.api.v1.admin.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.api.v1.admin.AdminUserApi;
import onexas.coordinate.api.v1.admin.model.ADomainUserCreate;
import onexas.coordinate.api.v1.admin.model.ARole;
import onexas.coordinate.api.v1.admin.model.AUser;
import onexas.coordinate.api.v1.admin.model.AUserCreate;
import onexas.coordinate.api.v1.admin.model.AUserFilter;
import onexas.coordinate.api.v1.admin.model.AUserListPage;
import onexas.coordinate.api.v1.admin.model.AUserOrganization;
import onexas.coordinate.api.v1.admin.model.AUserUpdate;
import onexas.coordinate.api.v1.impl.PreferenceApiImpl;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.IntegrityViolationException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.User;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminUserApiImpl")
@Profile({ Env.PROFILE_API_NODE })
@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = { AdminUserApi.ACTION_VIEW,
		AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
public class AdminUserApiImpl extends ApiImplBase implements AdminUserApi {

	@Autowired
	UserService userService;

	@Autowired
	DomainService domainService;

	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	public AdminUserApiImpl() {
		super(AdminUserApi.API_NAME, V1, AdminUserApi.API_URI);
	}

	@Override
	public AUser getUser(String uid, Boolean find) {
		return Jsons.transform(Boolean.TRUE.equals(find) ? userService.find(uid) : userService.get(uid), AUser.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AUser createUser(AUserCreate user) {
		User m = userService.create(user);
		return Jsons.transform(m, AUser.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AUser updateUser(String uid, AUserUpdate userUpdate) {
		User m = userService.update(uid, userUpdate);
		return Jsons.transform(m, AUser.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response deleteUser(String uid, Boolean quiet) {

		User user = userService.find(uid);
		if (user == null) {
			if (!Boolean.TRUE.equals(quiet)) {
				throw new NotFoundException("user {} not found", uid);
			}
			return new Response();
		}

		if (Domain.LOCAL.equals(user.getDomain())
				&& !AppContext.config().getBoolean("app.allowDeleteAdminAccount", false)) {
			if (user.getAccount().equals(AppContext.config().getString("app.adminAccount"))) {
				throw new IntegrityViolationException("not allow to delete admin user {}", user.getAccount());
			}
		}

		userService.delete(uid, Boolean.TRUE.equals(quiet) ? true : false);
		return new Response();
	}

	@Override
	public List<ARole> listUserRole(String uid) {
		return Jsons.transform(userService.listRole(uid), new TypeReference<List<ARole>>() {
		});
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	public Response addUserRole(String uid, List<String> roleUidList) {

		userService.addRoles(uid, new LinkedHashSet<>(roleUidList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response setUserRole(String uid, List<String> roleUidList) {

		userService.setRoles(uid, new LinkedHashSet<>(roleUidList));

		return new Response();
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response removeUserRole(String uid, List<String> roleUidList) {
		userService.removeRoles(uid, new LinkedHashSet<>(roleUidList));
		return new Response();
	}

	@Override
	public List<AUserOrganization> listUserOrganization(String uid) {
		return Jsons.transform(userService.listOrganization(uid), new TypeReference<List<AUserOrganization>>() {
		});
	}

	@Override
	public AUserListPage listUser(AUserFilter filter) {
		ListPage<User> r = userService.list(filter);
		return new AUserListPage(Jsons.transform(r.getItems(), new TypeReference<List<AUser>>() {
		}), r.getPageIndex(), r.getPageSize(), r.getPageTotal(), r.getItemTotal());
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AUser createUserByDomainUser(ADomainUserCreate userCreate) {
		String account = userCreate.getAccount();
		String domainCode = userCreate.getDomain();
		if (Domain.LOCAL.equals(domainCode)) {
			throw new BadArgumentException("can't create local domain user by this api {}#{}", account, domainCode);
		}
		if (userService.findByAccountDomain(account, domainCode) != null) {
			throw new BadArgumentException("user {}#{} is already existed", account, domainCode);
		}

		Domain domain = domainService.get(domainCode);
		DomainConfig domainConfig = null;

		try {
			domain = domainService.get(domainCode);
			domainConfig = domainService.getConfig(domainCode);
		} catch (NotFoundException x) {
			throw new UnauthenticatedException("no such domain");
		}

		DomainProvider domainProvider = dpfReg.getProvider(domain, domainConfig);

		// check and create user if not exist
		DomainUser domainUser = domainProvider.getUserFinder().findByAccount(account);
		if (domainUser == null) {
			throw new BadArgumentException("domain user ({}#{}) not found", account, domainCode);
		}

		User user = userService.createByDomainUser(domainUser);

		return Jsons.transform(user, AUser.class);
	}

	@Override
	@GrantPermissions(@GrantPermission(target = AdminUserApi.API_PERMISSION_TARGET, action = {
			AdminUserApi.ACTION_MODIFY, AdminUserApi.ACTION_ADMIN }))
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response resetUserPreferences(String uid) {
		
		Set<String> toRemove = new HashSet<>();
		Map<String, String> properties = userService.getProperties(uid, PreferenceApiImpl.CATEGORY);

		properties = PreferenceApiImpl.trimKey(properties);

		for (Entry<String, String> e : properties.entrySet()) {
			toRemove.add(e.getKey());
		}
		
		if (toRemove.size() > 0) {
			toRemove = PreferenceApiImpl.appendKey(toRemove);
			userService.deleteProperties(uid, toRemove);
		}

		return new Response();
	}
}