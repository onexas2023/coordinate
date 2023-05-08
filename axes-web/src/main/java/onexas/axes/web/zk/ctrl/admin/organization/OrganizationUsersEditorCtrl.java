package onexas.axes.web.zk.ctrl.admin.organization;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
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
import onexas.axes.web.model.OrganizationUserWrap;
import onexas.axes.web.model.UserWrap;
import onexas.axes.web.zk.ctrl.admin.user.UserSelectionEvent;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Events;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.MessageboxInputs;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminOrganizationApi;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUser;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserFilter;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserListPage;
import onexas.coordinate.api.v1.sdk.model.AOrganizationUserRelation;
import onexas.coordinate.api.v1.sdk.model.OrganizationUserRelationType;
import onexas.coordinate.api.v1.sdk.model.UDomain;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;
import onexas.coordinate.common.util.LabelObject;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationUsersEditorCtrl extends CtrlBase {

	@Wire
	Grid vusers;
	ListModelList<OrganizationUserWrap> usersModel;

	@Wire
	Div vtoadd;
	@Wire
	Div vtoremove;

	@Wire
	Paging vpaging;

	int pageSize;
	int pageIndex;
	long totalSize;

	AOrganization selectedOrganization;

	Set<OrganizationUserWrap> toAdd;
	Set<OrganizationUserWrap> toRemove;

	String requestId;

	Map<String, UDomain> domainMap = new LinkedHashMap<>();

	protected void afterCompose() throws Exception {

		for (UDomain domain : workspace.getMetainfo().getDomains()) {
			domainMap.put(domain.getCode(), domain);
		}

		pageSize = workspace.getPreferedPageSize();

		selectedOrganization = (AOrganization) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

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
			doRemoveUser((OrganizationUserWrap) evt.getData());
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

	private void doRemoveUser(OrganizationUserWrap user) {
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

	private void doAddUser(Set<OrganizationUserWrap> users) {
		toRemove.removeAll(users);
		// use the new one, the type might changed
		toAdd.removeAll(users);
		toAdd.addAll(users);

		for (OrganizationUserWrap r : users) {
			int idx = usersModel.indexOf(r);
			if (idx >= 0) {
				usersModel.set(idx, r);// force reload
			}
		}
		refreshToAddRemove();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof OrganizationEvent) {
			AOrganization organization = ((OrganizationEvent) evt).getOrganization();
			toAdd.clear();
			toRemove.clear();
			switch (((OrganizationEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedOrganization = organization;
				refreshList();
				refreshToAddRemove();
				refreshPaging();
				break;
			case DELETED:
				selectedOrganization = null;
				refreshList();
				refreshToAddRemove();
				refreshPaging();
				break;
			default:
			}
		} else if (evt instanceof UserSelectionEvent) {
			if (Objects.equals(((UserSelectionEvent) evt).getRequestId(), requestId)) {
				final Set<UserWrap> users = ((UserSelectionEvent) evt).getSelectedUsers();

				List<LabelObject<OrganizationUserRelationType>> list = new LinkedList<>();
				for (OrganizationUserRelationType e : OrganizationUserRelationType.values()) {
					list.add(new LabelObject<OrganizationUserRelationType>(e,
							Zks.getLabel("axes.organization.userType." + e.getValue())));
				}
				MessageboxInputs.showInputList(Zks.getLabel("axes.msg.selectOrganizationUserType"), null, list,
						null, null, "fas fa-clipboard-check ", null, (event) -> {
							OrganizationUserRelationType type = event.getInput();
							if (type != null) {
								Set<OrganizationUserWrap> ousers = new LinkedHashSet<>();
								for (UserWrap u : users) {
									ousers.add(new OrganizationUserWrap(
											Jsons.transform(u.delegatee, AOrganizationUser.class).relationType(type)));
								}
								doAddUser(ousers);
							}
						});
			}
		}
	}

	@Override
	protected void doSort() {
		refreshList();
	}

	private void refreshList() {
		usersModel.clear();

		if (selectedOrganization != null) {
			CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());
			AOrganizationUserFilter filter = new AOrganizationUserFilter().pageSize(pageSize).pageIndex(pageIndex);
			if (sortBy != null) {
				filter.sortField(sortBy).sortDesc(sortDesc);
			}
			AOrganizationUserListPage page = api.listOrganizationUser(selectedOrganization.getUid(), filter);
			if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
				pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
				page = api.listOrganizationUser(selectedOrganization.getUid(), filter.pageIndex(pageIndex));
			}
			for (AOrganizationUser user : page.getItems()) {
				usersModel.add(new OrganizationUserWrap(user));
			}

			pageIndex = page.getPageIndex();
			totalSize = page.getItemTotal();
		}
	}

	private void refreshToAddRemove() {

		vtoadd.getChildren().clear();
		for (OrganizationUserWrap r : toAdd) {
			Div d = new Div();
			Label l = new Label(r.getDisplayName() + ":"
					+ Zks.getLabel("axes.organization.userType." + r.getRelationType().getValue()));
			d.setSclass("chosen");
			d.appendChild(l);
			vtoadd.appendChild(d);
		}

		vtoremove.getChildren().clear();
		for (OrganizationUserWrap r : toRemove) {
			Div d = new Div();
			Label l = new Label(r.getDisplayName() + ":"
					+ Zks.getLabel("axes.organization.userType." + r.getRelationType().getValue()));
			d.setSclass("chosen");
			d.appendChild(l);
			vtoremove.appendChild(d);
		}
	}

	private void doSave() {
		if (selectedOrganization == null) {
			return;
		}

		CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());

		Set<AOrganizationUserRelation> toRel = new LinkedHashSet<>();
		for (OrganizationUserWrap r : toAdd) {
			toRel.add(new AOrganizationUserRelation().userUid(r.getUid()).type(r.getRelationType()));
		}
		api.addOrganizationUser(selectedOrganization.getUid(), new LinkedList<>(toRel));

		Set<String> toUid = new LinkedHashSet<>();
		for (OrganizationUserWrap r : toRemove) {
			toUid.add(r.getUid());
		}
		api.removeOrganizationUser(selectedOrganization.getUid(), new LinkedList<>(toUid));
		toAdd.clear();
		toRemove.clear();

		workspace.publish(new OrganizationEvent(OrganizationEventType.UPDATED, selectedOrganization));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", selectedOrganization.getName()));
	}

	public String getUserIconSclass(OrganizationUserWrap user) {
		if (toAdd.contains(user)) {
			return "fas fa-pen";
		} else if (toRemove.contains(user)) {
			return "fas fa-unlink";
		}
		return "";
	}

	public String getUserTypeIconSclass(OrganizationUserWrap user) {
		switch (user.getRelationType()) {
		case ADVANCED_MEMBER:
			return "fas fa-user-cog";
		case MEMBER:
			return "fas fa-user";
		case SUPERVISOR:
			return "fas fa-user-tie";
		default:
			return "";
		}
	}

	public String getType(OrganizationUserWrap user) {
		return Zks.getLabel("axes.organization.userType." + user.getRelationType().getValue());
	}

	public boolean isInToRemove(OrganizationUserWrap user) {
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
