package onexas.axes.web.zk.ctrl.admin.role;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Paging;

import onexas.axes.web.Constants;
import onexas.axes.web.model.UserWrap;
import onexas.axes.web.zk.ctrl.admin.user.UserSelectionEvent;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Events;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminRoleApi;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleUserFilter;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.api.v1.sdk.model.AUserListPage;
import onexas.coordinate.api.v1.sdk.model.UDomain;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleUsersEditorCtrl extends CtrlBase {

	@Wire
	Grid vusers;
	ListModelList<UserWrap> usersModel;

	@Wire
	Div vtoadd;
	@Wire
	Div vtoremove;

	@Wire
	Paging vpaging;

	int pageSize;
	int pageIndex;
	long totalSize;

	ARole selectedRole;

	Set<UserWrap> toAdd;
	Set<UserWrap> toRemove;

	String requestId;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

		pageSize = workspace.getPreferedPageSize();

		selectedRole = (ARole) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		toAdd = new LinkedHashSet<>();
		toRemove = new LinkedHashSet<>();

		usersModel = new ListModelList<>();
		vusers.setModel(usersModel);

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onAddUser", (evt) -> {
			Map<String, Object> arg = new HashMap<>();
			arg.put(Constants.ARG_REQUEST_ID, requestId = Strings.randomUid());
			arg.put(Constants.ARG_SELECTOR_MULTIPLE, Boolean.TRUE);
			Zks.openPopup(Constants.URI_USER_SELECTOR_POPUP, mainComp.getPage(),
					Events.unwrap(evt, Event.class).getTarget(), 600, -1, "end_before", arg);
		});
		mainComp.addEventListener("onRemoveUser", (evt) -> {
			doRemoveUser((UserWrap) evt.getData());
		});
		mainComp.addEventListener("onClearToAdd", (evt) -> {
			doClearToAdd();
		});
		mainComp.addEventListener("onClearToRemove", (evt) -> {
			doClearToRemove();
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
		refreshToAddRemove();
	}

	private void doClearToRemove() {
		toRemove.clear();
		refreshList();
		refreshPaging();
		refreshToAddRemove();
	}

	private void doClearToAdd() {
		toAdd.clear();
		refreshList();
		refreshPaging();
		refreshToAddRemove();
	}

	private void doRemoveUser(UserWrap user) {
		// toggle
		if (!toRemove.remove(user)) {
			toRemove.add(user);
		}
		toAdd.remove(user);

		int idx = usersModel.indexOf(user);
		if (idx >= 0) {
			usersModel.set(idx, user);// force reload
		}

		refreshToAddRemove();
	}

	private void doAddUser(Set<UserWrap> users) {
		toRemove.removeAll(users);
		toAdd.addAll(users);

		for (UserWrap r : users) {
			int idx = usersModel.indexOf(r);
			if (idx >= 0) {
				usersModel.set(idx, r);// force reload
			}
		}
		refreshToAddRemove();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof RoleEvent) {
			ARole role = ((RoleEvent) evt).getRole();
			toAdd.clear();
			toRemove.clear();
			switch (((RoleEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedRole = role;
				refreshList();
				refreshToAddRemove();
				refreshPaging();
				break;
			case DELETED:
				selectedRole = null;
				refreshList();
				refreshToAddRemove();
				refreshPaging();
				break;
			default:
			}
		} else if (evt instanceof UserSelectionEvent) {
			if (Objects.equals(((UserSelectionEvent) evt).getRequestId(), requestId)) {
				Set<UserWrap> users = ((UserSelectionEvent) evt).getSelectedUsers();
				doAddUser(users);
			}
		}
	}
	
	@Override
	protected void doSort() {
		refreshList();
	}

	private void refreshList() {
		usersModel.clear();

		if (selectedRole != null) {
			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
			ARoleUserFilter filter = new ARoleUserFilter().pageSize(pageSize).pageIndex(pageIndex);
			
			if (sortBy != null) {
				filter.sortField(sortBy).sortDesc(sortDesc);
			}
			
			AUserListPage page = api.listRoleUser(selectedRole.getUid(), filter);
			if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
				pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
				page = api.listRoleUser(selectedRole.getUid(), filter.pageIndex(pageIndex));
			}
			for (AUser user : page.getItems()) {
				usersModel.add(new UserWrap(user));
			}

			pageIndex = page.getPageIndex();
			totalSize = page.getItemTotal();
		}

	}

	private void refreshToAddRemove() {

		vtoadd.getChildren().clear();
		for (UserWrap r : toAdd) {
			Div d = new Div();
			Label l = new Label(r.getDisplayName());
			d.setSclass("chosen");
			d.appendChild(l);
			vtoadd.appendChild(d);
		}

		vtoremove.getChildren().clear();
		for (UserWrap r : toRemove) {
			Div d = new Div();
			Label l = new Label(r.getDisplayName());
			d.setSclass("chosen");
			d.appendChild(l);
			vtoremove.appendChild(d);
		}
	}

	private void doSave() {
		if (selectedRole == null) {
			return;
		}

		CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());

		Set<String> toUid = new LinkedHashSet<>();
		for (UserWrap r : toAdd) {
			toUid.add(r.getUid());
		}
		api.addRoleUser(selectedRole.getUid(), new LinkedList<>(toUid));

		toUid.clear();
		for (UserWrap r : toRemove) {
			toUid.add(r.getUid());
		}
		api.removeRoleUser(selectedRole.getUid(), new LinkedList<>(toUid));
		toAdd.clear();
		toRemove.clear();

		workspace.publish(new RoleEvent(RoleEventType.UPDATED, selectedRole));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", selectedRole.getName()));
	}

	public String getUserIconSclass(UserWrap user) {
		if (toAdd.contains(user)) {
			return "fas fa-pen";
		} else if (toRemove.contains(user)) {
			return "fas fa-unlink";
		}
		return "";
	}

	public boolean isInToRemove(UserWrap user) {
		return toRemove.contains(user);
	}

	public String getDomainName(String code) {
		return domainMap.containsKey(code) ? domainMap.get(code).getName() : code;
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
