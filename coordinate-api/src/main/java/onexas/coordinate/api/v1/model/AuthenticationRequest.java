package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AuthenticationRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String token;

	String account;

	String password;

	String domain;

	@Schema(description = "the stored (in cookie generally) token to authenticate, it will not be used with account/password/domain")
	public String getToken() {
		return token;
	}

	@Schema(description = "the user's account")
	public String getAccount() {
		return account;
	}

	@Schema(description = "the user's password")
	public String getPassword() {
		return password;
	}

	@Schema(description = "user's domain, default is local")
	public String getDomain() {
		return domain;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
