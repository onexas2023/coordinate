package onexas.coordinate.api.v1.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;

import onexas.coordinate.api.v1.AuthenticationApi;
import onexas.coordinate.api.v1.model.Authentication;
import onexas.coordinate.api.v1.model.AuthenticationRequest;
import onexas.coordinate.api.v1.model.UPrincipalPermission;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.BadArgumentException;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.model.AuthenticationToken;
import onexas.coordinate.model.AuthenticationTokenCreate;
import onexas.coordinate.model.Domain;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.User;
import onexas.coordinate.service.AuthenticationTokenService;
import onexas.coordinate.service.DomainService;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainProvider;
import onexas.coordinate.service.domain.DomainProviderFactoryRegistory;
import onexas.coordinate.web.api.impl.ApiImplBase;

/**
 * 
 * @author Dennis Chen
 *
 */
@RestController(Env.NS_BEAN + "AuthenticationApiImpl")
@Profile({ Env.PROFILE_API_NODE })
public class AuthenticationApiImpl extends ApiImplBase implements AuthenticationApi {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationApiImpl.class);

	private static final int TOKEN_LOOP = 5;

	@Autowired
	HttpServletRequest request;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	AuthenticationTokenService authTokenService;

	@Autowired
	DomainService domainService;

	@Autowired
	DomainProviderFactoryRegistory dpfReg;

	public AuthenticationApiImpl() {
		super(AuthenticationApi.API_NAME, V1, AuthenticationApi.API_URI);
	}

	@Override
	@Transactional(transactionManager = CoordinateEntityManageConfiguration.TX_MANAGER, isolation = Isolation.READ_COMMITTED)
	public Authentication authenticate(AuthenticationRequest authreq) {
		String clientIp = request.getRemoteAddr();
		String token = authreq.getToken();

		String account = authreq.getAccount();
		String domainCode = authreq.getDomain();
		String aliasUid, displayName;

		User user = null;

		boolean createToken = false;
		if (!Strings.isBlank(token)) {
			long now = System.currentTimeMillis();
			AuthenticationToken authToken = authTokenService.find(token);
			if (authToken == null || authToken.getTimeoutAt() <= now) {
				throw new UnauthenticatedException("invalidate token, not found or timeout");
			}
			String tokenIp = authToken.getClientIp();
			// verify ip
			if (!Strings.isBlank(tokenIp) && !tokenIp.equals(clientIp)) {
				logger.warn("invalidate token {} client ip {} != {}", Strings.ellipsis(authToken.getToken(), 13),
						tokenIp, clientIp);
				throw new UnauthenticatedException("invalidate token, deny");
			}
			account = authToken.getAccount();
			domainCode = authToken.getDomain();
			aliasUid = authToken.getAliasUid();

			user = userService.findByAccountDomain(account, domainCode);
			if (user == null) {
				throw new UnauthenticatedException("invalidate token, user not found");
			}
			if (!Strings.equals(aliasUid, user.getAliasUid())) {
				throw new UnauthenticatedException("invalidate token, user alias changed");
			}
			
			Domain domain;
			try {
				domain = domainService.get(domainCode);
			} catch (NotFoundException x) {
				throw new UnauthenticatedException("no such domain {}", domainCode);
			}
			if (Boolean.TRUE.equals(domain.getDisabled())) {
				throw new UnauthenticatedException("domain {} unavailable", domainCode);
			}
			
			displayName = user.getDisplayName();

		} else if (!Strings.isBlank(account)) {

			if (Strings.isBlank(domainCode)) {
				domainCode = Domain.LOCAL;
			}

			String password = authreq.getPassword();

			if (Strings.isBlank(account) || Strings.isBlank(password)) {
				throw new UnauthenticatedException("blank account or password");
			}

			Domain domain;
			DomainConfig domainConfig;

			try {
				domain = domainService.get(domainCode);
				domainConfig = domainService.getConfig(domainCode);
			} catch (NotFoundException x) {
				throw new UnauthenticatedException("no such domain {}", domainCode);
			}

			if (Boolean.TRUE.equals(domain.getDisabled())) {
				throw new UnauthenticatedException("domain {} unavailable", domainCode);
			}

			DomainProvider domainProvider = dpfReg.getProvider(domain, domainConfig);

			onexas.coordinate.service.domain.DomainAuthenticator.Authentication auth;
			try {
				auth = domainProvider.getAuthenticator().authenticate(account, password);
			} catch (UnauthenticatedException x) {
				throw new UnauthenticatedException("wrong account or password");
			}
			// check and create user if not exist
			DomainUser domainUser = domainProvider.getUserFinder().find(auth.getIdentity());
			if (domainUser == null) {
				throw new IllegalStateException(Strings.format("domain user ({} at {}) not found after authentication",
						auth.getIdentity(), domainCode));
			}

			user = userService.findByAccountDomain(account, domainCode);

			if (user == null) {
				// create
				user = userService.createByDomainUser(domainUser);

				// default roles
				List<String> defaultRoles = AppContext.config().getStringList("coordinate.domain.defaultRoles.role");
				if (defaultRoles != null && defaultRoles.size() > 0) {
					Set<String> roleUids = new LinkedHashSet<>();
					for (String code : defaultRoles) {
						Role role = roleService.findByCode(code);
						if (role != null) {
							roleUids.add(role.getUid());
						}
					}
					if (roleUids.size() > 0) {
						userService.setRoles(user.getUid(), roleUids);
					}
				}
			} else {
				// verify identify
				if (!userService.verifyDomainUserIdentity(domainUser)) {
					throw new UnauthenticatedException("inconsistent domain user identity");
				}
			}

			token = Strings.randomUid(TOKEN_LOOP);
			aliasUid = user.getAliasUid();
			displayName = user.getDisplayName();
			createToken = true;
		} else {
			throw new BadArgumentException("token or account does not found");
		}

		if (user.getDisabled()) {
			throw new NoPermissionException("user is disabled");
		}

		if (createToken) {
			token = authTokenService.create(new AuthenticationTokenCreate().withAccount(account).withAliasUid(aliasUid)
					.withDomain(domainCode).withDisplayName(displayName).withClientIp(clientIp)).getToken();
		} else {
			try {
				authTokenService.extend(token);
			} catch (NotFoundException x) {
				throw new UnauthenticatedException("invalidate token, just timeout");
			}
		}

		List<Role> roles = userService.listRole(user.getUid());

		Set<PrincipalPermission> permissions = new LinkedHashSet<>();
		for (Role r : roles) {
			for (PrincipalPermission p : roleService.listPermission(r.getUid())) {
				permissions.add(p);
			}
		}
		Authentication auth = new Authentication();
		auth.setToken(token);
		auth.setAliasUid(aliasUid);
		auth.setDomain(user.getDomain());
		auth.setDisplayName(user.getDisplayName());
		auth.setPermissions(Jsons.transform(permissions, new TypeReference<List<UPrincipalPermission>>() {
		}));
		return auth;
	}

}