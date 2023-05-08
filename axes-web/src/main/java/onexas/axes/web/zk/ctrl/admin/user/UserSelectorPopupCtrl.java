package onexas.axes.web.zk.ctrl.admin.user;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.UserWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminUserApi;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserFilter;
import onexas.coordinate.api.v1.sdk.model.AUserListPage;
import onexas.coordinate.api.v1.sdk.model.UDomain;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserSelectorPopupCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vusers;
	ListModelList<UserWrap> usersModel;

	@Wire
	Paging vpaging;

	@Wire
	Div vchosen;

	String requestId;
	Set<UserWrap> selectedUsers;

	int pageSize;
	int pageIndex;
	long totalSize;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

		requestId = (String) Zks.getScopeArg(mainComp, Constants.ARG_REQUEST_ID);
		if (requestId == null) {
			requestId = Strings.randomUid();
		}

		Boolean multiple = (Boolean) Zks.getScopeArg(mainComp, Constants.ARG_SELECTOR_MULTIPLE);

		@SuppressWarnings("unchecked")
		Set<UserWrap> users = (Set<UserWrap>) Zks.getScopeArg(mainComp, Constants.ARG_SELECTOR_SELECTED_SET);
		if (users != null && users.size() > 0) {
			selectedUsers = new LinkedHashSet<>();
			for (UserWrap r : users) {
				selectedUsers.add(r);
			}
		}

		pageSize = workspace.getPreferedPageSize();

		usersModel = new ListModelList<UserWrap>();
		vusers.setModel(usersModel);
		vusers.setMultiple(Boolean.TRUE.equals(multiple));

		mainComp.addEventListener("onSearch", (evt) -> {
			doSearch();
		});
		mainComp.addEventListener("onSelect", (evt) -> {
			doSelect();
		});
		mainComp.addEventListener("onPaging", (evt) -> {
			doPaging();
		});
		mainComp.addEventListener("onSelectOK", (evt) -> {
			doSelectOK();
		});
		mainComp.addEventListener("onSelectCancel", (evt) -> {
			doSelectCancel();
		});

		doSearch();
		refreshChosen();
	}

	private void doSelectCancel() {
		Zks.closePopup(mainComp);
	}

	private void doSelectOK() {

		Set<UserWrap> users = new LinkedHashSet<>();
		if (selectedUsers != null && selectedUsers.size() > 0) {
			for (UserWrap r : selectedUsers) {
				users.add(r);
			}
		}

		Zks.closePopup(mainComp);

		workspace.publish(new UserSelectionEvent(requestId, users));
	}

	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword);
		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());
		AUserFilter filter = new AUserFilter().strContaining(true).strIgnoreCase(true).account(keyword)
				.displayName(keyword).pageSize(pageSize).pageIndex(pageIndex);
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
		if (selectedUsers != null && selectedUsers.size() > 0) {
			for (UserWrap r : selectedUsers) {
				usersModel.addToSelection(r);
			}
		}

		refreshPaging();
	}

	private void refreshPaging() {
		vpaging.setActivePage(pageIndex);
		vpaging.setPageSize(pageSize);
		vpaging.setTotalSize((int) totalSize);
	}

	private void doSelect() {
		Set<UserWrap> s = usersModel.getSelection();

		if (s != null && s.size() > 0) {
			selectedUsers = new LinkedHashSet<>();
			for (UserWrap r : s) {
				selectedUsers.add(r);
			}
		} else {
			selectedUsers = null;
		}

		refreshChosen();
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	private void refreshChosen() {
		vchosen.getChildren().clear();
		if (selectedUsers != null && selectedUsers.size() > 0) {
			for (UserWrap r : selectedUsers) {
				Div d = new Div();
				Label l = new Label(r.getDisplayName());
				d.setSclass("chosen");
				d.appendChild(l);
				vchosen.appendChild(d);
			}
		} else {
			vchosen.appendChild(new Label(Zks.getLabel("axes.msg.blankSelection")));
		}
	}

	public String getDomainName(String code) {
		return domainMap.containsKey(code) ? domainMap.get(code).getName() : code;
	}
}
