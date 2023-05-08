package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ALogListPage extends ListPage<ALog> {

	private static final long serialVersionUID = 1L;

	public ALogListPage(List<ALog> items) {
		super(items);
	}

	public ALogListPage(List<ALog> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
