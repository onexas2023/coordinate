package onexas.axes.web.zk.ctrl.admin.domain;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.RequestContext;
import onexas.axes.web.model.DomainUserWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminDomainApi;
import onexas.coordinate.api.v1.sdk.CoordinateAdminUserApi;
import onexas.coordinate.api.v1.sdk.model.ADomain;
import onexas.coordinate.api.v1.sdk.model.ADomainUser;
import onexas.coordinate.api.v1.sdk.model.ADomainUserCreate;
import onexas.coordinate.api.v1.sdk.model.ADomainUserFilter;
import onexas.coordinate.api.v1.sdk.model.ADomainUserListPage;
import onexas.coordinate.api.v1.sdk.model.AUserFilter;
import onexas.coordinate.api.v1.sdk.model.AUserListPage;
import onexas.coordinate.common.app.AppContext;

/**
 * 
 * @author Dennis Chen
 *
 */
public class DomainUsersEditorCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Grid vusers;
	ListModelList<DomainUserWrap> usersModel;

	@Wire
	Div vtoadd;
	@Wire
	Div vtoremove;

	@Wire
	Paging vpaging;

	int pageSize;
	int pageIndex;
	long totalSize;

	ADomain selectedDomain;

	protected void afterCompose() throws Exception {

		pageSize = workspace.getPreferedPageSize();

		selectedDomain = (ADomain) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		usersModel = new ListModelList<>();
		vusers.setModel(usersModel);
		mainComp.addEventListener("onSearch", (evt) -> {
			refreshList();
			refreshPaging();
		});
		mainComp.addEventListener("onCreateUser", (evt) -> {
			doCreateUser((DomainUserWrap) evt.getData());
		});
		mainComp.addEventListener("onPaging", (evt) -> {
			doPaging();
		});

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshList();
		refreshPaging();
	}

	private void doCreateUser(DomainUserWrap user) {

		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());

		api.createUserByDomainUser(new ADomainUserCreate().account(user.getAccount()).domain(user.getDomain()));

		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyCreated", user.getDisplayName()));

		int idx = usersModel.indexOf(user);
		if (idx >= 0) {
			usersModel.set(idx, user);// force reload
		}
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof DomainEvent) {
			ADomain domain = ((DomainEvent) evt).getDomain();
			switch (((DomainEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedDomain = domain;
				refreshList();
				refreshPaging();
				break;
			case DELETED:
				selectedDomain = null;
				refreshList();
				break;
			default:
			}
		}
	}

	@Override
	protected void doSort() {
		refreshList();
	}

	private void refreshList() {

		String keyword = Zks.trimValue(vkeyword);

		usersModel.clear();

		if (selectedDomain != null) {
			CoordinateAdminDomainApi api = new CoordinateAdminDomainApi(workspace.getApiClient());
			ADomainUserFilter filter = new ADomainUserFilter().criteria(keyword).pageSize(pageSize)
					.pageIndex(pageIndex);
			if (sortBy != null) {
				filter.sortField(sortBy).sortDesc(sortDesc);
			}
			ADomainUserListPage page = api.listDomainUser(selectedDomain.getCode(), filter);
			if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
				pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
				page = api.listDomainUser(selectedDomain.getCode(), filter.pageIndex(pageIndex));
			}
			for (ADomainUser user : page.getItems()) {
				usersModel.add(new DomainUserWrap(user));
			}

			pageIndex = page.getPageIndex();
			totalSize = page.getItemTotal();
		}

	}

	public String getUserIconSclass(DomainUserWrap user) {
		if (!isExistUser(user)) {
			return "fas fa-user-secret";
		}
		return "";
	}

	public boolean isExistUser(DomainUserWrap user) {
		String cacheName = getClass().getName() + ".isExistUser." + user.getAccount() + "#" + user.getDomain();
		RequestContext request = AppContext.bean(RequestContext.class);
		Boolean b = (Boolean) request.getAttribute(cacheName);
		if (b != null) {
			return b.booleanValue();
		}

		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());
		AUserListPage p = api.listUser(new AUserFilter().account(user.getAccount()).domain(user.getDomain()));
		if (p.getItems().size() > 0) {
			b = true;
		} else {
			b = false;
		}
		request.setAttribute(cacheName, b);
		return b;
	}

	private void refreshPaging() {
		vpaging.setTotalSize((int) totalSize);
		vpaging.setPageSize(pageSize);
		vpaging.setActivePage(pageIndex);
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		refreshList();
		refreshPaging();
	}
}
