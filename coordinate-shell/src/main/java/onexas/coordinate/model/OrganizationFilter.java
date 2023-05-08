package onexas.coordinate.model;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class OrganizationFilter extends PageFilter {

	private static final long serialVersionUID = 1L;

	protected String code;

	protected String name;

	protected String description;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public OrganizationFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public OrganizationFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public OrganizationFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public OrganizationFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public OrganizationFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public OrganizationFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public OrganizationFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public OrganizationFilter withCode(String code) {
		this.code = code;
		return this;
	}

	public OrganizationFilter withName(String name) {
		this.name = name;
		return this;
	}

	public OrganizationFilter withDescription(String description) {
		this.description = description;
		return this;
	}

}