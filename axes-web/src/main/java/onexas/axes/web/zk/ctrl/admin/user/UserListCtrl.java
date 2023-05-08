package onexas.axes.web.zk.ctrl.admin.user;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.UserWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminUserApi;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserFilter;
import onexas.coordinate.api.v1.sdk.model.AUserListPage;
import onexas.coordinate.api.v1.sdk.model.UDomain;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserListCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vusers;
	ListModelList<UserWrap> usersModel;

	@Wire
	Paging vpaging;

	AUser selectedUser;

	int pageSize;
	int pageIndex;
	long totalSize;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

		pageSize = workspace.getPreferedPageSize();

		usersModel = new ListModelList<UserWrap>();
		vusers.setModel(usersModel);

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
		// domain
		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());
		AUserFilter filter = new AUserFilter().strContaining(true).strIgnoreCase(true).displayName(keyword)
				.account(keyword).pageSize(pageSize).pageIndex(pageIndex);

		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		}

		AUserListPage page = api.listUser(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listUser(filter.pageIndex(pageIndex));
		}

		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		usersModel.clear();
		usersModel.clearSelection();
		for (AUser user : page.getItems()) {
			usersModel.add(new UserWrap(user));
		}
		if (selectedUser != null) {
			usersModel.addToSelection(new UserWrap(selectedUser));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof UserEvent) {
			AUser user = ((UserEvent) evt).getUser();
			switch (((UserEvent) evt).getType()) {
			case CREATED:
				selectedUser = user;
				doSearch();
				break;
			case DELETED:
				if (selectedUser != null && selectedUser.getUid().equals(user.getUid())) {
					selectedUser = null;
				}
				doSearch();
				break;
			case UPDATED:
				if (selectedUser != null && selectedUser.getUid().equals(user.getUid())) {
					selectedUser = user;
				}
				doSearch();
				break;
			case START_TO_CREATE:
				selectedUser = null;
				usersModel.clearSelection();
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
		UserWrap wrap = usersModel.getSingleSelection();
		selectedUser = wrap == null ? null : wrap.delegatee;
		workspace.publish(new UserEvent(UserEventType.SELECTED, selectedUser));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	public String getDomainName(String code) {
		return domainMap.containsKey(code) ? domainMap.get(code).getName() : code;
	}
}
