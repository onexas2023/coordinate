package onexas.coordinate.api.v1.model;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UMetainfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<UDomain> domains;

	@Schema(description = "the domain list")
	public List<UDomain> getDomains() {
		return domains;
	}

	public void setDomains(List<UDomain> domains) {
		this.domains = domains;
	}

}
