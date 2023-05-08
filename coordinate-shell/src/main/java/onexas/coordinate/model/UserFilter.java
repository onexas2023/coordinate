package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UserFilter extends PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String account;

	protected String email;

	protected String domain;

	protected String displayName;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public UserFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public UserFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public UserFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public UserFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public UserFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public UserFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public UserFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public UserFilter withAccount(String account) {
		this.account = account;
		return this;
	}

	public UserFilter withEmail(String email) {
		this.email = email;
		return this;
	}

	public UserFilter withDomain(String domain) {
		this.domain = domain;
		return this;
	}

	public UserFilter withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}
}