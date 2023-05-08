package onexas.coordinate.service.impl;

import org.springframework.stereotype.Service;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.model.UserActivityToken;
import onexas.coordinate.service.UserActivityContext;

/**
 * 
 * @author Dennis Chen
 *
 */
@Service(Env.NS_BEAN + "UserActivityContextImpl")
public class UserActivityContextImpl implements UserActivityContext {

	private ThreadLocal<UserActivityToken> tlToken = new ThreadLocal<>();
	
	
	@Override
	public UserActivityToken start(String userAccount, String userDomain, String userDisplayName) {
		UserActivityToken token = new UserActivityToken(Strings.randomUid(), userAccount, userDomain, userDisplayName);
		tlToken.set(token);
		return token;
	}

	@Override
	public void end() {
		tlToken.remove();
	}

	@Override
	public UserActivityToken getTokenIfAny() {
		return tlToken.get();
	}

	@Override
	public UserActivityToken follow(UserActivityToken token) {
		UserActivityToken prev = tlToken.get();
		tlToken.set(token);
		return prev;
	}

	@Override
	public void unfollow(UserActivityToken previousToken) {
		tlToken.set(previousToken);
	}

	

}