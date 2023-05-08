package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AJobListPage extends ListPage<AJob> {

	private static final long serialVersionUID = 1L;

	public AJobListPage(List<AJob> items) {
		super(items);
	}

	public AJobListPage(List<AJob> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
