package onexas.axes.web.zk.ctrl.admin.user;

import java.util.Collections;
import java.util.HashMap;
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

import onexas.axes.web.Constants;
import onexas.axes.web.model.RoleWrap;
import onexas.axes.web.zk.ctrl.admin.role.RoleSelectionEvent;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminUserApi;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.AUser;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserRolesEditorCtrl extends CtrlBase {

	@Wire
	Grid vroles;
	ListModelList<RoleWrap> rolesModel;

	@Wire
	Div vtoadd;
	@Wire
	Div vtoremove;

	AUser selectedUser;

	Set<RoleWrap> toAdd;
	Set<RoleWrap> toRemove;

	String requestId;

	protected void afterCompose() throws Exception {

		selectedUser = (AUser) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		toAdd = new LinkedHashSet<>();
		toRemove = new LinkedHashSet<>();

		rolesModel = new ListModelList<>();
		vroles.setModel(rolesModel);

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onAddRole", (evt) -> {
			Map<String, Object> arg = new HashMap<>();
			arg.put(Constants.ARG_REQUEST_ID, requestId = Strings.randomUid());
			arg.put(Constants.ARG_SELECTOR_MULTIPLE, Boolean.TRUE);
			Zks.openPopup(Constants.URI_ROLE_SELECTOR_POPUP, mainComp.getPage(),
					Zks.unwrap(evt, Event.class).getTarget(), 600, -1, "end_before", arg);
		});
		mainComp.addEventListener("onRemoveRole", (evt) -> {
			doRemoveRole((RoleWrap) evt.getData());
		});
		mainComp.addEventListener("onClearToAdd", (evt) -> {
			doClearToAdd();
		});
		mainComp.addEventListener("onClearToRemove", (evt) -> {
			doClearToRemove();
		});
		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshList();
		refreshToAddRemove();
	}

	private void doClearToRemove() {
		toRemove.clear();
		refreshList();
		refreshToAddRemove();
	}

	private void doClearToAdd() {
		toAdd.clear();
		refreshList();
		refreshToAddRemove();
	}

	private void doRemoveRole(RoleWrap role) {
		// toggle
		if (!toRemove.remove(role)) {
			toRemove.add(role);
		}
		toAdd.remove(role);

		int idx = rolesModel.indexOf(role);
		if (idx >= 0) {
			rolesModel.set(idx, role);// force reload
		}

		refreshToAddRemove();
	}

	private void doAddRole(Set<RoleWrap> roles) {
		toRemove.removeAll(roles);
		toAdd.addAll(roles);

		for (RoleWrap r : roles) {
			int idx = rolesModel.indexOf(r);
			if (idx >= 0) {
				rolesModel.set(idx, r);// force reload
			}
		}
		refreshToAddRemove();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof UserEvent) {
			AUser user = ((UserEvent) evt).getUser();
			toAdd.clear();
			toRemove.clear();
			switch (((UserEvent) evt).getType()) {
			case CREATED:
			case UPDATED:
			case SELECTED:
				selectedUser = user;
				refreshList();
				refreshToAddRemove();
				break;
			case DELETED:
				selectedUser = null;
				refreshList();
				refreshToAddRemove();
				break;
			default:
			}
		} else if (evt instanceof RoleSelectionEvent) {
			if (Objects.equals(((RoleSelectionEvent) evt).getRequestId(), requestId)) {
				Set<RoleWrap> roles = ((RoleSelectionEvent) evt).getSelectedRoles();
				doAddRole(roles);
			}
		}
	}

	@Override
	protected void doSort() {
		refreshList();
	}

	private void refreshList() {
		rolesModel.clear();

		if (selectedUser != null) {
			CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());

			List<RoleWrap> l = new LinkedList<>();

			for (ARole role : api.listUserRole(selectedUser.getUid())) {
				l.add(new RoleWrap(role));
			}
			if (sortBy != null) {
				Collections.sort(l, (e1, e2) -> {
					RoleWrap t;
					if (sortDesc) {
						t = e1;
						e1 = e2;
						e2 = t;
					}
					switch (sortBy) {
					case "code":
						return e1.getCode().compareTo(e2.getCode());
					case "name":
						return e1.getName().compareTo(e2.getName());
					}
					throw new IllegalArgumentException("unsupported " + sortBy);
				});
			}
			rolesModel.addAll(l);
		}
	}

	private void refreshToAddRemove() {

		vtoadd.getChildren().clear();
		for (RoleWrap r : toAdd) {
			Div d = new Div();
			Label l = new Label(r.getName());
			d.setSclass("chosen");
			d.appendChild(l);
			vtoadd.appendChild(d);
		}

		vtoremove.getChildren().clear();
		for (RoleWrap r : toRemove) {
			Div d = new Div();
			Label l = new Label(r.getName());
			d.setSclass("chosen");
			d.appendChild(l);
			vtoremove.appendChild(d);
		}
	}

	private void doSave() {
		if (selectedUser == null) {
			return;
		}

		CoordinateAdminUserApi api = new CoordinateAdminUserApi(workspace.getApiClient());

		Set<String> toUid = new LinkedHashSet<>();
		for (RoleWrap r : toAdd) {
			toUid.add(r.getUid());
		}
		api.addUserRole(selectedUser.getUid(), new LinkedList<>(toUid));

		toUid.clear();
		for (RoleWrap r : toRemove) {
			toUid.add(r.getUid());
		}
		api.removeUserRole(selectedUser.getUid(), new LinkedList<>(toUid));
		toAdd.clear();
		toRemove.clear();

		workspace.publish(new UserEvent(UserEventType.UPDATED, selectedUser));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", selectedUser.getDisplayName()));
	}

	public String getRoleIconSclass(RoleWrap role) {
		if (toAdd.contains(role)) {
			return "fas fa-pen";
		} else if (toRemove.contains(role)) {
			return "fas fa-unlink";
		}
		return "";
	}

	public boolean isInToRemove(RoleWrap role) {
		return toRemove.contains(role);
	}
}
