package onexas.axes.web.zk.ctrl.admin.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.PrincipalPermissionRegistory;
import onexas.axes.web.model.PrincipalPermissionWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.model.APrincipalPermission;
import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.app.Config;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class PrincipalPermissionSelectorPopupCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vpermissions;
	ListModelList<PrincipalPermissionWrap> permissionsModel;

	@Wire
	Div vchosen;

	String requestId;
	Set<PrincipalPermissionWrap> selectedPrincipalPermissions;

	protected void afterCompose() throws Exception {

		requestId = (String) Zks.getScopeArg(mainComp, Constants.ARG_REQUEST_ID);
		if (requestId == null) {
			requestId = Strings.randomUid();
		}

		Boolean multiple = (Boolean) Zks.getScopeArg(mainComp, Constants.ARG_SELECTOR_MULTIPLE);

		@SuppressWarnings("unchecked")
		Set<PrincipalPermissionWrap> permissions = (Set<PrincipalPermissionWrap>) Zks.getScopeArg(mainComp,
				Constants.ARG_SELECTOR_SELECTED_SET);
		if (permissions != null && permissions.size() > 0) {
			selectedPrincipalPermissions = new LinkedHashSet<>();
			for (PrincipalPermissionWrap r : permissions) {
				selectedPrincipalPermissions.add(r);
			}
		}

		permissionsModel = new ListModelList<PrincipalPermissionWrap>();
		vpermissions.setModel(permissionsModel);
		vpermissions.setMultiple(Boolean.TRUE.equals(multiple));

		mainComp.addEventListener("onSearch", (evt) -> {
			doSearch();
		});
		mainComp.addEventListener("onSelect", (evt) -> {
			doSelect();
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

		Set<PrincipalPermissionWrap> permissions = new LinkedHashSet<>();
		if (selectedPrincipalPermissions != null && selectedPrincipalPermissions.size() > 0) {
			for (PrincipalPermissionWrap r : selectedPrincipalPermissions) {
				permissions.add(r);
			}
		}

		Zks.closePopup(mainComp);

		workspace.publish(new PrincipalPermissionSelectionEvent(requestId, permissions));
	}

	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword);

		List<APrincipalPermission> items = AppContext.bean(PrincipalPermissionRegistory.class).list();

		permissionsModel.clear();
		permissionsModel.clearSelection();

		List<PrincipalPermissionWrap> finalList = new LinkedList<>();

		String presetName = null;
		if (keyword.startsWith("@")) {
			presetName = keyword.substring(1).trim();
		}
		
		//if there is any preset, go to preset filter
		if (presetName != null) {
			List<Config> list = AppContext.config().getSubConfigList("axes.permissionPresets.preset");
			Set<String> filters = new HashSet<>();
			for (Config preset : list) {
				String n = preset.getString(Config.PATH_NAME_ATTR);
				if (presetName.equalsIgnoreCase(n)) {
					List<Config> itemscfg = preset.getSubConfigList("item", false);
					for (Config i : itemscfg) {
						String target = i.getString("[@target]");
						String action = i.getString("[@action]");
						filters.add(Strings.format("{}:{}", target, action));
					}
				}
			}
			for (APrincipalPermission principalPermission : items) {
				String k = Strings.format("{}:{}", principalPermission.getTarget(), principalPermission.getAction());
				if(filters.contains(k)) {					
					finalList.add(new PrincipalPermissionWrap(principalPermission));
				}
			}
		} else {
			for (APrincipalPermission principalPermission : items) {
				if (principalPermission.getTarget().toLowerCase().contains(keyword.toLowerCase())) {
					finalList.add(new PrincipalPermissionWrap(principalPermission));
				}
			}
		}
		if (sortBy != null) {
			Collections.sort(finalList, (e1, e2) -> {
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
		permissionsModel.addAll(finalList);

		if (selectedPrincipalPermissions != null && selectedPrincipalPermissions.size() > 0) {
			for (PrincipalPermissionWrap r : selectedPrincipalPermissions) {
				permissionsModel.addToSelection(r);
			}
		}
	}

	private void doSelect() {
		Set<PrincipalPermissionWrap> s = permissionsModel.getSelection();

		if (s != null && s.size() > 0) {
			selectedPrincipalPermissions = new LinkedHashSet<>();
			for (PrincipalPermissionWrap r : s) {
				selectedPrincipalPermissions.add(r);
			}
		} else {
			selectedPrincipalPermissions = null;
		}

		refreshChosen();
	}

	private void refreshChosen() {
		vchosen.getChildren().clear();
		if (selectedPrincipalPermissions != null && selectedPrincipalPermissions.size() > 0) {
			for (PrincipalPermissionWrap r : selectedPrincipalPermissions) {
				Div d = new Div();
				Label l = new Label(r.getTarget() + ":" + r.getAction());
				d.setSclass("chosen");
				d.appendChild(l);
				vchosen.appendChild(d);
			}
		} else {
			vchosen.appendChild(new Label(Zks.getLabel("axes.msg.blankSelection")));
		}
	}
}
