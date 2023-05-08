package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UPasswordUpdate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String oldPassword;

	String newPassword;

	@Schema(description = "the old password for verfiying update", required = true)
	@NotNull
	public String getOldPassword() {
		return oldPassword;
	}

	@Schema(description = "the new password for updating", required = true)
	@NotNull
	@Size(min = 4, max = 64)
	public String getNewPassword() {
		return newPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
