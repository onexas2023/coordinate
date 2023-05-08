package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class AOrganizationListPage extends ListPage<AOrganization> {

	private static final long serialVersionUID = 1L;

	public AOrganizationListPage(List<AOrganization> items) {
		super(items);
	}

	public AOrganizationListPage(List<AOrganization> items, Integer pageIndex, Integer pageSize, Integer pageTotal,
			Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
