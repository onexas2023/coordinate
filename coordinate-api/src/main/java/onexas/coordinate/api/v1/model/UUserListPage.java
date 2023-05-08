package onexas.coordinate.api.v1.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UUserListPage extends ListPage<UUser> {

	private static final long serialVersionUID = 1L;

	public UUserListPage(List<UUser> items) {
		super(items);
	}

	public UUserListPage(List<UUser> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
