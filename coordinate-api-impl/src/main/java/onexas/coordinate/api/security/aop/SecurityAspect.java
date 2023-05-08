package onexas.coordinate.api.security.aop;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import onexas.coordinate.api.RequestContext;
import onexas.coordinate.api.security.GrantAuthentication;
import onexas.coordinate.api.security.GrantPermission;
import onexas.coordinate.api.security.GrantPermissions;
import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.err.NoPermissionException;
import onexas.coordinate.model.AuthenticationToken;
import onexas.coordinate.model.UserActivityToken;
import onexas.coordinate.service.AuthenticationTokenService;
import onexas.coordinate.service.PermissionService;
import onexas.coordinate.service.RoleService;
import onexas.coordinate.service.UserActivityContext;
import onexas.coordinate.service.UserService;

/**
 * 
 * @author Dennis Chen
 *
 */
@Aspect
@Component(Env.NS_BEAN + "SecurityAspect")
public class SecurityAspect {
	private static final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

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

	@Autowired
	RequestContext reqContext;

	@Autowired
	UserActivityContext userActivityContext;

	// on class method
	@Around("@annotation(onexas.coordinate.api.security.GrantPermissions)")
	public Object grantPermissionsAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		GrantPermissions anno = method.getAnnotation(GrantPermissions.class);

		if (logger.isDebugEnabled()) {
			logger.debug("grant permissions {} on method {}", anno, signature.getName());
		}
		
		final AuthenticationToken authToken = reqContext.grantToken();
		final UserActivityToken actToken = userActivityContext.getTokenIfAny();
		if (actToken == null) {
			userActivityContext.start(authToken.getAccount(), authToken.getDomain(),
					authToken.getDisplayName());
		}

		try {
			grantPermissions(authToken, anno);
			Object obj = joinPoint.proceed();
			return obj;
		} finally {
			if (actToken == null) {
				userActivityContext.end();
			}
		}
	}

	// on class type, with all method
	@Around("@within(onexas.coordinate.api.security.GrantPermissions)")
	public Object grantPermissionsAnnotationWithin(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		GrantPermissions anno = method.getDeclaringClass().getAnnotation(GrantPermissions.class);

		if (logger.isDebugEnabled()) {
			logger.debug("grant permissions {} on method {}", anno, signature.getName());
		}
		
		final AuthenticationToken authToken = reqContext.grantToken();
		final UserActivityToken actToken = userActivityContext.getTokenIfAny();
		if (actToken == null) {
			userActivityContext.start(authToken.getAccount(), authToken.getDomain(),
					authToken.getDisplayName());
		}

		try {
			grantPermissions(authToken, anno);
			Object obj = joinPoint.proceed();
			return obj;
		} finally {
			if (actToken == null) {
				userActivityContext.end();
			}
		}
	}

	// get test startup error exception
//	@Around("@target(onexas.coordinate.api.security.GrantPermissions)")
//	public Object grantPermissionsAnnotationTarget(ProceedingJoinPoint joinPoint) throws Throwable {
//		System.out.println(">>>>Calling target "+joinPoint.getSignature().getName());
//		Object obj = joinPoint.proceed();
//		return obj;
//	}

	@Around("@annotation(onexas.coordinate.api.security.GrantAuthentication)")
	public Object grantAuthenticationAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		GrantAuthentication anno = method.getAnnotation(GrantAuthentication.class);
		if (logger.isDebugEnabled()) {
			logger.debug("grant authentication {} on method {}", anno, signature.getName());
		}

		final AuthenticationToken authToken = reqContext.grantToken();
		final UserActivityToken actToken = userActivityContext.getTokenIfAny();
		if (actToken == null) {
			userActivityContext.start(authToken.getAccount(), authToken.getDomain(),
					authToken.getDisplayName());
		}

		try {
			Object obj = joinPoint.proceed();
			return obj;
		} finally {
			if (actToken == null) {
				userActivityContext.end();
			}
		}
	}

	@Around("@within(onexas.coordinate.api.security.GrantAuthentication)")
	public Object grantAuthenticationWithin(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		GrantAuthentication anno = method.getDeclaringClass().getAnnotation(GrantAuthentication.class);
		if (logger.isDebugEnabled()) {
			logger.debug("grant authentication {} on method {}", anno, signature.getName());
		}

		final AuthenticationToken authToken = reqContext.grantToken();
		final UserActivityToken actToken = userActivityContext.getTokenIfAny();
		if (actToken == null) {
			userActivityContext.start(authToken.getAccount(), authToken.getDomain(),
					authToken.getDisplayName());
		}
		
		try {
			Object obj = joinPoint.proceed();
			return obj;
		} finally {
			if (actToken == null) {
				userActivityContext.end();
			}
		}
	}

	private void grantPermissions(AuthenticationToken authToken, GrantPermissions anno) {

		onexas.coordinate.api.GrantPermissions permissions = new onexas.coordinate.api.GrantPermissions();
		permissions.setMatchAll(anno.matchAll());
		for (GrantPermission gp : anno.value()) {
			onexas.coordinate.api.GrantPermission permission = new onexas.coordinate.api.GrantPermission();
			permission.withMatchAll(gp.matchAll());
			permission.withTarget(gp.target());
			permission.withActions(gp.action());
			permissions.addToPermissions(permission);
		}

		boolean pass = reqContext.hasPermissions(permissions);

		if (!pass) {
			if (logger.isWarnEnabled()) {
				String annstr = anno.toString().replaceAll("onexas\\.coordinate\\.api\\.security\\.", "");
				logger.warn("grant permission of {}, user {}, from {}", annstr, authToken.getAccount(),
						authToken.getClientIp());
			}
			throw new NoPermissionException("grant permission fail");
		} else {
			logger.debug("grant permission of {}, passed", anno);
		}
	}
	
}