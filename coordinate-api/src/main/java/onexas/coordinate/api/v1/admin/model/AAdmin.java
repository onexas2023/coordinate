package onexas.coordinate.api.v1.admin.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AAdmin implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
