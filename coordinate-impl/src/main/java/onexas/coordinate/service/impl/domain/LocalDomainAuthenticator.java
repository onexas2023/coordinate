package onexas.coordinate.service.impl.domain;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.err.UnauthenticatedException;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.User;
import onexas.coordinate.service.UserService;
import onexas.coordinate.service.domain.DomainAuthenticator;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LocalDomainAuthenticator implements DomainAuthenticator {
	String domainCode;
	DomainConfig domainConfig;

	public LocalDomainAuthenticator(String domainCode, DomainConfig domainConfig) {
		this.domainCode = domainCode;
		this.domainConfig = domainConfig;
	}

	@Override
	public Authentication authenticate(String account, String password) throws UnauthenticatedException {
		UserService userService = AppContext.getBean(UserService.class);
		if (userService.verifyPasswordByAccountDomain(account, domainCode, password)) {
			User user = userService.findByAccountDomain(account, domainCode);
			Authentication auth = new Authentication();
			auth.setIdentity(user.getUid());
			return auth;
		}
		throw new UnauthenticatedException("wrong account or password");
	}


}
