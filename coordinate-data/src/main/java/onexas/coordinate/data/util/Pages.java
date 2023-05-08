package onexas.coordinate.data.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Pages {

	public static <T> ListPage<T> page(List<T> totalItems, PageFilter filter) {
		Integer pageIndex = null;
		Integer pageSize = null;
		int totalSize = totalItems.size();

		if (filter != null) {
			pageIndex = filter.getPageIndex();
			pageSize = filter.getPageSize();
		}

		if (pageIndex == null || pageIndex < 0) {
			pageIndex = 0;
		}

		if (pageSize == null) {
			if (pageIndex <= 0) {
				return new ListPage<T>(totalItems);
			} else {
				return new ListPage<T>(Collections.emptyList(), pageIndex, totalSize, totalSize == 0 ? 0 : 1,
						(long) totalSize);
			}
		}

		List<T> pitem = new LinkedList<T>();
		int i = pageIndex * pageSize;
		int j = i + pageSize;
		for (; i < totalSize && i < j; i++) {
			pitem.add(totalItems.get(i));
		}
		
		return new ListPage<T>(pitem, pageIndex, pageSize, totalSize==0?0:(totalSize/pageSize) + (totalSize%pageSize>0?1:0),
				(long) totalSize);
	}

}
