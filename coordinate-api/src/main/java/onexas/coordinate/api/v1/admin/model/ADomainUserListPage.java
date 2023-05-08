package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ADomainUserListPage extends ListPage<ADomainUser> {

	private static final long serialVersionUID = 1L;

	public ADomainUserListPage(List<ADomainUser> items) {
		super(items);
	}

	public ADomainUserListPage(List<ADomainUser> items, Integer pageIndex, Integer pageSize, Integer pageTotal,
			Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
