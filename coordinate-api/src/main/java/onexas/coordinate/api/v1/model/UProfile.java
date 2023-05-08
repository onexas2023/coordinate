package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	String displayName;

	String email;
	
	String account;
	
	String domain;

	String phone;

	String address;
	

	@Schema(description = "the display name")
	public String getDisplayName() {
		return displayName;
	}

	@Schema(description = "the email")
	public String getEmail() {
		return email;
	}

	@Schema(description = "the address")
	public String getAddress() {
		return address;
	}

	@Schema(description = "the phone number")
	public String getPhone() {
		return phone;
	}

	@Schema(description = "the login account")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Schema(description = "the login domain")
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
