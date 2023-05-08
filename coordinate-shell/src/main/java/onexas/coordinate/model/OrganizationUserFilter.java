package onexas.coordinate.model;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class OrganizationUserFilter extends PageFilter {

	private static final long serialVersionUID = 1L;
	
	protected String account;

	protected String email;

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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public OrganizationUserFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public OrganizationUserFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public OrganizationUserFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public OrganizationUserFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public OrganizationUserFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public OrganizationUserFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public OrganizationUserFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public OrganizationUserFilter withAccount(String account) {
		this.account = account;
		return this;
	}
	
	public OrganizationUserFilter withEmail(String email) {
		this.email = email;
		return this;
	}

	public OrganizationUserFilter withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}
}