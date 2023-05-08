package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ARoleListPage extends ListPage<ARole> {

	private static final long serialVersionUID = 1L;

	public ARoleListPage(List<ARole> items) {
		super(items);
	}

	public ARoleListPage(List<ARole> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
