package onexas.coordinate.service.domain;

import onexas.coordinate.common.err.UnauthenticatedException;

/**
 * 
 * @author Dennis Chen
 *
 */
public interface DomainAuthenticator {

	Authentication authenticate(String account, String password) throws UnauthenticatedException;

	public class Authentication {
		String identity;

		public Authentication() {
		}

		public Authentication(String identify) {
			this.identity = identify;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

}