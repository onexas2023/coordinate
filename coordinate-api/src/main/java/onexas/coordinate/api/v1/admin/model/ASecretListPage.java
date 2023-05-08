package onexas.coordinate.api.v1.admin.model;

import java.util.List;

import onexas.coordinate.common.model.ListPage;

/**
 * 
 * @author Dennis Chen
 *
 */

public class ASecretListPage extends ListPage<ASecret> {

	private static final long serialVersionUID = 1L;

	public ASecretListPage(List<ASecret> items) {
		super(items);
	}

	public ASecretListPage(List<ASecret> items, Integer pageIndex, Integer pageSize, Integer pageTotal, Long itemTotal) {
		super(items, pageIndex, pageSize, pageTotal, itemTotal);
	}

}
