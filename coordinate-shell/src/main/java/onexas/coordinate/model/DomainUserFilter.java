package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class DomainUserFilter extends PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String criteria;

	public String getCriteria() {
		return criteria;
	}

	public Boolean getStrIgnoreCase() {
		return strIgnoreCase;
	}

	public Boolean getStrContaining() {
		return strContaining;
	}

	public Boolean getMatchAny() {
		return matchAny;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public DomainUserFilter withCriteria(String criteria) {
		this.criteria = criteria;
		return this;
	}

	public DomainUserFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public DomainUserFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public DomainUserFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public DomainUserFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public DomainUserFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public DomainUserFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public DomainUserFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}
}