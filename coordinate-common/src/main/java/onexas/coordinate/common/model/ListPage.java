package onexas.coordinate.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ListPage<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Integer pageIndex;

	protected List<T> items;

	protected Integer pageTotal;

	protected Integer pageSize;

	protected Long itemTotal;

	public ListPage(List<T> items) {
		this.items = items;
		pageIndex = 0;
		pageSize = items.size();
		pageTotal = pageSize == 0 ? 0 : 1;
		itemTotal = Long.valueOf(pageSize);
	}

	public ListPage(List<T> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		this.items = items;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.pageTotal = pageTotal;
		this.itemTotal = itemTotal;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public Integer getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(Integer pageTotal) {
		this.pageTotal = pageTotal;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public int size() {
		return items == null ? 0 : items.size();
	}

	public Long getItemTotal() {
		return itemTotal;
	}

	public void setItemTotal(Long itemTotal) {
		this.itemTotal = itemTotal;
	}

}