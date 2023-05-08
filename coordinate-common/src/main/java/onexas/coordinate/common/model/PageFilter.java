package onexas.coordinate.common.model;

import java.io.Serializable;

import javax.validation.constraints.Min;

/**
 * 
 * @author Dennis Chen
 *
 */

public class PageFilter extends Filter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Min(value = 0)
	protected Integer pageIndex;

	@Min(value = 1)
	protected Integer pageSize;

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public PageFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public PageFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

}