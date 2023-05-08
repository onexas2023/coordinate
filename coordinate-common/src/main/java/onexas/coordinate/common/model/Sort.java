package onexas.coordinate.common.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Sort implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Boolean sortDesc;

	protected String sortField;

	public Boolean getSortDesc() {
		return sortDesc;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public Sort withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public Sort withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

}