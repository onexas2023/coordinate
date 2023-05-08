package onexas.coordinate.api.v1.admin.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import onexas.coordinate.api.v1.admin.AdminInitApi;
import onexas.coordinate.api.v1.admin.model.AAdmin;
import onexas.coordinate.api.v1.admin.model.AInitDemoRequest;
import onexas.coordinate.api.v1.admin.model.AInitRequest;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.lang.Collections;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.Permission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.RoleCreate;
import onexas.coordinate.model.User;
import onexas.coordinate.model.UserCreate;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;
import onexas.coordinate.service.event.InitDemoEvent;
import onexas.coordinate.web.api.impl.ApiImplBase;
import onexas.coordinate.web.api.model.Response;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AdminInitApiImpl")
@Profile({ Env.PROFILE_API_NODE })
public class AdminInitApiImpl extends ApiImplBase implements AdminInitApi {

	private static final Logger logger = LoggerFactory.getLogger(AdminInitApiImpl.class);

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	DomainService domainService;

	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	@Autowired
	Environment environment;

	@Autowired
	ApplicationEventPublisher eventPublisher;

	public AdminInitApiImpl() {
		super(AdminInitApi.API_NAME, V1, AdminInitApi.API_URI);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public AAdmin initAdmin(AInitRequest initRequest) {

		checkInitSecret(initRequest);

		if (roleService.count() > 0 || userService.count() > 0) {
			throw new NoPermissionException(
					"there are some data already, system is probably been initialized already");
		}

		String adminAccount = AppContext.config().getString("app.adminAccount");
		String adminRole = AppContext.config().getString("app.adminRole");

		if (adminAccount == null) {
			throw new IllegalStateException("no admin account configuratin found");
		}
		if (adminRole == null) {
			throw new IllegalStateException("no admin role configuratin found");
		}

		String password = Strings.randomPassword(16);

		UserCreate userCreate = new UserCreate().withAccount(adminAccount).withDisplayName(adminAccount)
				.withDomain(Domain.LOCAL).withPassword(password);
		User user = userService.create(userCreate);
		logger.info("initialized admin account {} and role {}", adminAccount, adminRole);

		Role role;
		if ((role = roleService.findByCode(adminRole)) == null) {
			RoleCreate roleCreate = new RoleCreate().withCode(adminRole).withName(adminRole);
			role = roleService.create(roleCreate);
			permissionService.create(role.getUid(), Permission.ANY_TARGET, Permission.ANY_ACTION, "initAdmin");

			logger.info("initialized admin role {}", adminRole);
		}

		userService.setRoles(user.getUid(), Collections.asSet(role.getUid()));

		AAdmin root = new AAdmin();
		root.setPassword(password);
		return root;
	}

	private void checkInitSecret(AInitRequest initRequest) {
		String initSecret = AppContext.config().getString("app.initSecret");
		if (Strings.isBlank(initSecret)) {
			throw new NoPermissionException("the system doesn't allow to init by secret");
		}

		if (!initSecret.equals(initRequest.getSecret())) {
			throw new NoPermissionException("invalid init secret");
		}
	}

	private void checkAdminSecret(AInitRequest initRequest) {
		String adminAccount = AppContext.config().getString("app.adminAccount");
		if (adminAccount == null) {
			throw new NoPermissionException("adminAccount not found");
		}

		String password = initRequest.getSecret();

		String domainCode = Domain.LOCAL;
		Domain domain = domainService.get(domainCode);
		DomainConfig domainConfig = domainService.getConfig(domainCode);

		if (Boolean.TRUE.equals(domain.getDisabled())) {
			throw new NoPermissionException("domain {} disabled", domainCode);
		}

		DomainProvider domainProvider = dpfReg.getProvider(domain, domainConfig);

		try {
			domainProvider.getAuthenticator().authenticate(adminAccount, password);
		} catch (UnauthenticatedException x) {
			throw new NoPermissionException("wrong secret");
		}
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Response initDemo(AInitDemoRequest initDemoRequest) {

		if (!Collections.asSet(environment.getActiveProfiles()).contains("init-demo")) {
			throw new NoPermissionException("the system doesn't allow to init-demo");
		}

		checkAdminSecret(initDemoRequest);

		InitDemoEvent evt = new InitDemoEvent(initDemoRequest.getDemo());
		eventPublisher.publishEvent(evt);

		if(evt.getReportCount()==0) {
			return new Response(Strings.format("available demos {}", Strings.cat(evt.getAvailableDemos())));
		}else {
			return new Response(Strings.format("executed {}", evt.getReportCount()));
		}
	}

}