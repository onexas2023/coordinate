package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UProfileUpdate implements Serializable {
	private static final long serialVersionUID = 1L;

	String displayName;

	String email;

	String phone;

	String address;

	@Schema(description = "the display name to update")
	public String getDisplayName() {
		return displayName;
	}

	@Schema(description = "the email to update")
	public String getEmail() {
		return email;
	}

	@Schema(description = "the address to update")
	public String getAddress() {
		return address;
	}

	@Schema(description = "the phone to update")
	public String getPhone() {
		return phone;
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

	public UProfileUpdate withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public UProfileUpdate withEmail(String email) {
		this.email = email;
		return this;
	}

	public UProfileUpdate withPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public UProfileUpdate withAddress(String address) {
		this.address = address;
		return this;
	}

}
