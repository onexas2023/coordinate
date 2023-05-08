package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class SecretFilter extends PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SecretFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public SecretFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public SecretFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public SecretFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public SecretFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public SecretFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public SecretFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public SecretFilter withCode(String code) {
		this.code = code;
		return this;
	}
}