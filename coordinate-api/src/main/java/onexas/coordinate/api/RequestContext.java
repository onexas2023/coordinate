package onexas.coordinate.api;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.AuthenticationToken;
import onexas.coordinate.model.Permission;
import onexas.coordinate.model.PrincipalPermission;
import onexas.coordinate.model.Role;
import onexas.coordinate.model.User;
import onexas.coordinate.service.AuthenticationTokenService;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserService;
import onexas.coordinate.web.api.Api;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "RequestContext")
@Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {

	private static final Logger logger = LoggerFactory.getLogger(RequestContext.class);

	@Autowired
	HttpServletRequest request;

	@Autowired
	PermissionService permissionService;

	@Autowired
	AuthenticationTokenService authTokenService;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	AuthenticationToken token;
	String userUid;

	Set<PrincipalPermission> permissions;

	public AuthenticationToken grantToken() {

		if (this.token != null) {
			return this.token;
		}

		String tkn = request.getHeader(Api.NAME_AUTH_TOKEN);
		
		//Support short life auth cookie for http get download
		if (Strings.isBlank(tkn)) {
			String key = request.getParameter(Api.NAME_AUTH_COOKIE_PARAMETER);
			if (key != null) {
				Cookie[] cks = request.getCookies();
				if(cks!=null && cks.length>0) {
					for(Cookie ck:cks) {
						if(key.equalsIgnoreCase(ck.getName())) {
							tkn = ck.getValue();
						}
					}
				}
			}
		}
		
		if (Strings.isBlank(tkn)) {
			tkn = request.getParameter(Api.NAME_AUTH_TOKEN);
		}
		
		if (Strings.isBlank(tkn)) {
			throw new UnauthenticatedException("no token in header, cookie or parameter {}", Api.NAME_AUTH_TOKEN);
		}
		
		AuthenticationToken token = authTokenService.find(tkn);
		if (token == null) {
			throw new UnauthenticatedException("token not found, timeout or non-existed");
		}
		if (token.getTimeoutAt() <= System.currentTimeMillis()) {
			throw new UnauthenticatedException("token not found, timeout");
		}

		String clientIp = request.getRemoteAddr();
		String tokenIp = token.getClientIp();
		// verify ip
		if (!Strings.isBlank(tokenIp) && !tokenIp.equals(clientIp)) {
			logger.warn("invalidate token {} client ip {} != {}", Strings.ellipsis(token.getToken(), 13), tokenIp,
					clientIp);
			throw new UnauthenticatedException("invalidate token, deny");
		}

		String account = token.getAccount();
		String domain = token.getDomain();

		User user = userService.findByAccountDomain(account, domain);
		if (user == null) {
			throw new UnauthenticatedException("invalidate token, user not found");
		}
		if (user.getDisabled()) {
			throw new NoPermissionException("user is disabled");
		}

		this.token = token;
		this.userUid = user.getUid();

		return token;
	}

	public String grantUserUid() {
		if (userUid != null) {
			return userUid;
		}
		grantToken();
		return userUid;
	}

	public Set<PrincipalPermission> grantRolePermissions() {
		if (permissions != null) {
			return permissions;
		}

		grantToken();

		List<Role> roles = userService.listRole(userUid);

		Set<PrincipalPermission> permissions = new LinkedHashSet<>();
		for (Role r : roles) {
			for (Permission p : permissionService.listByPrincipal(r.getUid())) {
				permissions.add(new PrincipalPermission(p.getTarget(), p.getAction()));
			}
		}
		this.permissions = Collections.unmodifiableSet(permissions);
		return this.permissions;
	}

	public void grantPermissions(GrantPermissions permissions) {
		if (!hasPermissions(permissions)) {
			throw new NoPermissionException("grant permissions fail");
		}
	}

	public boolean hasPermissions(GrantPermissions permissions) {
		Set<PrincipalPermission> rolePermissions = grantRolePermissions();

		int grantHit = 0;

		l1: for (GrantPermission gp : permissions.getPermissions()) {

			int permissionHit = 0;

			l2: for (PrincipalPermission pp : rolePermissions) {

				if (Permission.ANY_TARGET.equals(pp.getTarget()) || pp.getTarget().equals(gp.getTarget())) {

					int actionHit = 0;

					l3: for (String a : gp.getActions()) {
						if (Permission.ANY_ACTION.equals(pp.getAction()) || pp.getAction().equals(a)) {
							actionHit++;
							if (!gp.isMatchAll()) {
								break l3;
							}
						}
					}

					if ((gp.isMatchAll() && actionHit == gp.getActions().size())
							|| (!gp.isMatchAll() && actionHit > 0)) {
						permissionHit++;
						if (!gp.isMatchAll()) {
							break l2;
						}
					}
				}
			}

			if (permissionHit > 0) {
				grantHit++;
				if (!permissions.isMatchAll()) {
					break l1;
				}
			}

		}

		boolean pass = false;
		if ((permissions.isMatchAll() && grantHit == permissions.getPermissions().size())
				|| (!permissions.isMatchAll() && grantHit > 0)) {
			pass = true;
		}
		return pass;
	}

	@PreDestroy
	public void destroy() {
		if (token != null) {
			if(authTokenService.shouldExtend(token.getTimeoutAt())) {
				try {
					authTokenService.extend(token.getToken());
				} catch (Exception x) {
				}
			}
		}
	}
}
