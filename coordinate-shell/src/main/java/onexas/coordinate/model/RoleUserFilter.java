package onexas.coordinate.model;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class RoleUserFilter extends PageFilter {

	private static final long serialVersionUID = 1L;

	public RoleUserFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public RoleUserFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public RoleUserFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public RoleUserFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public RoleUserFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public RoleUserFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public RoleUserFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}
}