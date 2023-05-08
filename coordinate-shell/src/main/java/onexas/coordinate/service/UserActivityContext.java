package onexas.coordinate.service;

import onexas.coordinate.model.UserActivityToken;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface UserActivityContext {

	UserActivityToken start(String userAccount, String userDomain, String userDisplayName);

	void end();

	UserActivityToken getTokenIfAny();

	UserActivityToken follow(UserActivityToken token);

	void unfollow(UserActivityToken previousToken);
}
