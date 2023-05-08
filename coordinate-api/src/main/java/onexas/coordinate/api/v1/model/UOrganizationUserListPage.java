package onexas.coordinate.api.v1.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UOrganizationUserListPage extends ListPage<UOrganizationUser> {

	private static final long serialVersionUID = 1L;

	public UOrganizationUserListPage(List<UOrganizationUser> items) {
		super(items);
	}

	public UOrganizationUserListPage(List<UOrganizationUser> items, Integer pageIndex, Integer pageSize,
			Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
