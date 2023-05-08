package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AOrganizationUserListPage extends ListPage<AOrganizationUser> {

	private static final long serialVersionUID = 1L;

	public AOrganizationUserListPage(List<AOrganizationUser> items) {
		super(items);
	}

	public AOrganizationUserListPage(List<AOrganizationUser> items, Integer pageIndex, Integer pageSize,
			Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
