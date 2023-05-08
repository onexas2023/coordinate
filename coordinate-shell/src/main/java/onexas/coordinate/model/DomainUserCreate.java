package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class DomainUserCreate implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String account;

	protected String domain;

	@NotNull
	@Schema(required = true)
	public String getAccount() {
		return account;
	}

	@NotNull
	@Schema(required = true)
	public String getDomain() {
		return domain;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public DomainUserCreate withAccount(String account) {
		this.account = account;
		return this;
	}

	public DomainUserCreate withDomain(String domain) {
		this.domain = domain;
		return this;
	}
}