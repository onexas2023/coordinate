package onexas.axes.web.zk.ctrl.admin.role;

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
import onexas.axes.web.model.PrincipalPermissionWrap;
import onexas.axes.web.zk.ctrl.admin.security.PrincipalPermissionSelectionEvent;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminRoleApi;
import onexas.coordinate.api.v1.sdk.model.APrincipalPermission;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RolePermissionsEditorCtrl extends CtrlBase {

	@Wire
	Grid vpermissions;
	ListModelList<PrincipalPermissionWrap> permissionsModel;

	@Wire
	Div vtoadd;
	@Wire
	Div vtoremove;

	ARole selectedRole;

	Set<PrincipalPermissionWrap> toAdd;
	Set<PrincipalPermissionWrap> toRemove;

	String requestId;

	protected void afterCompose() throws Exception {

		selectedRole = (ARole) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		toAdd = new LinkedHashSet<>();
		toRemove = new LinkedHashSet<>();

		permissionsModel = new ListModelList<>();
		vpermissions.setModel(permissionsModel);

		mainComp.addEventListener("onSave", (evt) -> {
			doSave();
		});
		mainComp.addEventListener("onAddPermission", (evt) -> {
			Map<String, Object> arg = new HashMap<>();
			arg.put(Constants.ARG_REQUEST_ID, requestId = Strings.randomUid());
			arg.put(Constants.ARG_SELECTOR_MULTIPLE, Boolean.TRUE);
			Zks.openPopup(Constants.URI_PRINCIPAL_PERMISSION_SELECTOR_POPUP, mainComp.getPage(),
					Zks.unwrap(evt, Event.class).getTarget(), 600, -1, "end_before", arg);
		});
		mainComp.addEventListener("onRemovePermission", (evt) -> {
			doRemovePermission((PrincipalPermissionWrap) evt.getData());
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

	private void doRemovePermission(PrincipalPermissionWrap permission) {
		// toggle
		if (!toRemove.remove(permission)) {
			toRemove.add(permission);
		}
		toAdd.remove(permission);

		int idx = permissionsModel.indexOf(permission);
		if (idx >= 0) {
			permissionsModel.set(idx, permission);// force reload
		}

		refreshToAddRemove();
	}

	private void doAddPermission(Set<PrincipalPermissionWrap> permissions) {
		toRemove.removeAll(permissions);
		toAdd.addAll(permissions);

		for (PrincipalPermissionWrap r : permissions) {
			int idx = permissionsModel.indexOf(r);
			if (idx >= 0) {
				permissionsModel.set(idx, r);// force reload
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
				break;
			case DELETED:
				selectedRole = null;
				refreshList();
				refreshToAddRemove();
				break;
			default:
			}
		} else if (evt instanceof PrincipalPermissionSelectionEvent) {
			if (Objects.equals(((PrincipalPermissionSelectionEvent) evt).getRequestId(), requestId)) {
				Set<PrincipalPermissionWrap> permissions = ((PrincipalPermissionSelectionEvent) evt)
						.getSelectedPrincipalPermissions();
				doAddPermission(permissions);
			}
		}
	}

	@Override
	protected void doSort() {
		refreshList();
	}

	private void refreshList() {
		permissionsModel.clear();

		if (selectedRole != null) {
			CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
			List<APrincipalPermission> items = api.listRolePermission(selectedRole.getUid());
			
			List<PrincipalPermissionWrap> l = new LinkedList<>();
			
			for (APrincipalPermission permission : items) {
				l.add(new PrincipalPermissionWrap(permission));
			}
			
			if (sortBy != null) {
				Collections.sort(l, (e1, e2) -> {
					PrincipalPermissionWrap t;
					if (sortDesc) {
						t = e1;
						e1 = e2;
						e2 = t;
					}
					switch (sortBy) {
					case "target":
						return e1.getTarget().compareTo(e2.getTarget());
					case "action":
						return e1.getAction().compareTo(e2.getAction());
					}
					throw new IllegalArgumentException("unsupported " + sortBy);
				});
			}
			
			permissionsModel.addAll(l);
		}
	}

	private void refreshToAddRemove() {

		vtoadd.getChildren().clear();
		for (PrincipalPermissionWrap r : toAdd) {
			Div d = new Div();
			Label l = new Label(r.getTarget() + ":" + r.getAction());
			d.setSclass("chosen");
			d.appendChild(l);
			vtoadd.appendChild(d);
		}

		vtoremove.getChildren().clear();
		for (PrincipalPermissionWrap r : toRemove) {
			Div d = new Div();
			Label l = new Label(r.getTarget() + ":" + r.getAction());
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

		Set<APrincipalPermission> toObj = new LinkedHashSet<>();
		for (PrincipalPermissionWrap r : toAdd) {
			toObj.add(r.deletatee);
		}
		api.addRolePermission(selectedRole.getUid(), new LinkedList<>(toObj));

		toObj.clear();
		for (PrincipalPermissionWrap r : toRemove) {
			toObj.add(r.deletatee);
		}
		api.removeRolePermission(selectedRole.getUid(), new LinkedList<>(toObj));
		toAdd.clear();
		toRemove.clear();

		workspace.publish(new RoleEvent(RoleEventType.UPDATED, selectedRole));
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", selectedRole.getName()));
	}

	public String getPermissionIconSclass(PrincipalPermissionWrap permission) {
		if (toAdd.contains(permission)) {
			return "fas fa-pen";
		} else if (toRemove.contains(permission)) {
			return "fas fa-unlink";
		}
		return "";
	}

	public boolean isInToRemove(PrincipalPermissionWrap permission) {
		return toRemove.contains(permission);
	}

}
