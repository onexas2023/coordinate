package onexas.axes.web.zk.ctrl.admin.role;

import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.RoleWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminRoleApi;
import onexas.coordinate.api.v1.sdk.model.ARole;
import onexas.coordinate.api.v1.sdk.model.ARoleFilter;
import onexas.coordinate.api.v1.sdk.model.ARoleListPage;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class RoleSelectorPopupCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vroles;
	ListModelList<RoleWrap> rolesModel;

	@Wire
	Paging vpaging;

	@Wire
	Div vchosen;

	String requestId;
	Set<RoleWrap> selectedRoles;

	int pageSize;
	int pageIndex;
	long totalSize;

	protected void afterCompose() throws Exception {

		requestId = (String) Zks.getScopeArg(mainComp, Constants.ARG_REQUEST_ID);
		if (requestId == null) {
			requestId = Strings.randomUid();
		}

		Boolean multiple = (Boolean) Zks.getScopeArg(mainComp, Constants.ARG_SELECTOR_MULTIPLE);

		@SuppressWarnings("unchecked")
		Set<RoleWrap> roles = (Set<RoleWrap>) Zks.getScopeArg(mainComp, Constants.ARG_SELECTOR_SELECTED_SET);
		if (roles != null && roles.size() > 0) {
			selectedRoles = new LinkedHashSet<>();
			for (RoleWrap r : roles) {
				selectedRoles.add(r);
			}
		}

		pageSize = workspace.getPreferedPageSize();

		rolesModel = new ListModelList<RoleWrap>();
		vroles.setModel(rolesModel);
		vroles.setMultiple(Boolean.TRUE.equals(multiple));

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

		Set<RoleWrap> roles = new LinkedHashSet<>();
		if (selectedRoles != null && selectedRoles.size() > 0) {
			for (RoleWrap r : selectedRoles) {
				roles.add(r);
			}
		}

		Zks.closePopup(mainComp);

		workspace.publish(new RoleSelectionEvent(requestId, roles));
	}

	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword);
		CoordinateAdminRoleApi api = new CoordinateAdminRoleApi(workspace.getApiClient());
		ARoleFilter filter = new ARoleFilter().strContaining(true).strIgnoreCase(true).code(keyword).name(keyword)
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
		if (selectedRoles != null && selectedRoles.size() > 0) {
			for (RoleWrap r : selectedRoles) {
				rolesModel.addToSelection(r);
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
		Set<RoleWrap> s = rolesModel.getSelection();

		if (s != null && s.size() > 0) {
			selectedRoles = new LinkedHashSet<>();
			for (RoleWrap r : s) {
				selectedRoles.add(r);
			}
		} else {
			selectedRoles = null;
		}

		refreshChosen();
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	private void refreshChosen() {
		vchosen.getChildren().clear();
		if (selectedRoles != null && selectedRoles.size() > 0) {
			for (RoleWrap r : selectedRoles) {
				Div d = new Div();
				Label l = new Label(r.getName());
				d.setSclass("chosen");
				d.appendChild(l);
				vchosen.appendChild(d);
			}
		} else {
			vchosen.appendChild(new Label(Zks.getLabel("axes.msg.blankSelection")));
		}
	}
}
