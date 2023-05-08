package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class HookMatch implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String zone;

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public HookMatch withZone(String zone) {
		this.zone = zone;
		return this;
	}

}