package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class RoleFilter extends PageFilter implements Serializable {

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

	public RoleFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public RoleFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public RoleFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public RoleFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public RoleFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public RoleFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public RoleFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public RoleFilter withCode(String code) {
		this.code = code;
		return this;
	}

	public RoleFilter withName(String name) {
		this.name = name;
		return this;
	}

	public RoleFilter withDescription(String description) {
		this.description = description;
		return this;
	}

}