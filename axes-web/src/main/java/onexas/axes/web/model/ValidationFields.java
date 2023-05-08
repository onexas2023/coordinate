package onexas.axes.web.model;

import javax.validation.constraints.Size;

/**
 * 
 * @author Dennis Chen
 *
 */
public class ValidationFields {

	String password;

	@Size(min = 4, max = 64)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
