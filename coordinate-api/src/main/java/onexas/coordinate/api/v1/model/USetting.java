package onexas.coordinate.api.v1.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class USetting implements Serializable {

	private static final long serialVersionUID = 1L;

	protected UServerSetting server;

	public UServerSetting getServer() {
		return server;
	}

	public void setServer(UServerSetting server) {
		this.server = server;
	}

}
