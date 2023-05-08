package onexas.coordinate.api.v1.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UServerSetting implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String adminEmail;

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}

}
