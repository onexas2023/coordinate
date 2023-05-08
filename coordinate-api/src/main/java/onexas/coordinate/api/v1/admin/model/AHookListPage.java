package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AHookListPage extends ListPage<AHook> {

	private static final long serialVersionUID = 1L;

	public AHookListPage(List<AHook> items) {
		super(items);
	}

	public AHookListPage(List<AHook> items, Integer pageIndex, Integer pageSize, Integer pageTotal,
			Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
