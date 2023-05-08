package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AUserListPage extends ListPage<AUser> {

	private static final long serialVersionUID = 1L;

	public AUserListPage(List<AUser> items) {
		super(items);
	}

	public AUserListPage(List<AUser> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
