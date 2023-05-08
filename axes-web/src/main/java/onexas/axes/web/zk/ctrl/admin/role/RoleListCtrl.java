package onexas.axes.web.zk.ctrl.admin.role;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.RoleWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminRoleApi;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleFilter;
import onexas.coordinate.api.v1.sdk.model.ARoleListPage;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleListCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vroles;
	ListModelList<RoleWrap> rolesModel;

	@Wire
	Paging vpaging;

	ARole selectedRole;

	int pageSize;
	int pageIndex;
	long totalSize;

	protected void afterCompose() throws Exception {

		pageSize = workspace.getPreferedPageSize();

		rolesModel = new ListModelList<RoleWrap>();
		vroles.setModel(rolesModel);

		mainComp.addEventListener("onSearch", (evt) -> {
			doSearch();
		});
		mainComp.addEventListener("onSelect", (evt) -> {
			doSelect();
		});
		mainComp.addEventListener("onPaging", (evt) -> {
			doPaging();
		});

		doSearch();

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});
	}
	
	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword);
		CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
		ARoleFilter filter = new ARoleFilter().strContaining(true).strIgnoreCase(true).name(keyword).code(keyword)
				.pageSize(pageSize).pageIndex(pageIndex);
		
		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		}
		
		ARoleListPage page = api.listRole(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listRole(filter.pageIndex(pageIndex));
		}

		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		rolesModel.clear();
		rolesModel.clearSelection();
		for (ARole role : page.getItems()) {
			rolesModel.add(new RoleWrap(role));
		}
		if (selectedRole != null) {
			rolesModel.addToSelection(new RoleWrap(selectedRole));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof RoleEvent) {
			ARole role = ((RoleEvent) evt).getRole();
			switch (((RoleEvent) evt).getType()) {
			case CREATED:
				selectedRole = role;
				doSearch();
				break;
			case DELETED:
				if (selectedRole != null && selectedRole.getUid().equals(role.getUid())) {
					selectedRole = null;
				}
				doSearch();
				break;
			case UPDATED:
				if (selectedRole != null && selectedRole.getUid().equals(role.getUid())) {
					selectedRole = role;
				}
				doSearch();
				break;
			case START_TO_CREATE:
				selectedRole = null;
				rolesModel.clearSelection();
				break;
			default:
			}
		}
	}

	private void refreshPaging() {
		vpaging.setActivePage(pageIndex);
		vpaging.setPageSize(pageSize);
		vpaging.setTotalSize((int) totalSize);
	}

	private void doSelect() {
		RoleWrap wrap = rolesModel.getSingleSelection();
		selectedRole = wrap == null ? null : wrap.delegatee;
		workspace.publish(new RoleEvent(RoleEventType.SELECTED, selectedRole));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}
}
