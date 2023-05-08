package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Property implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;

	protected String value;

	public Property() {
	}

	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
}