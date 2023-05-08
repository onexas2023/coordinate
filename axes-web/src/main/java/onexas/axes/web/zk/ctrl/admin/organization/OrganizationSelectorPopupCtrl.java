package onexas.axes.web.zk.ctrl.admin.organization;

import java.util.LinkedHashSet;
import java.util.Set;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.Constants;
import onexas.axes.web.model.OrganizationWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminOrganizationApi;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationFilter;
import onexas.coordinate.api.v1.sdk.model.AOrganizationListPage;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationSelectorPopupCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vorganizations;
	ListModelList<OrganizationWrap> organizationsModel;

	@Wire
	Paging vpaging;
	
	@Wire
	Div vchosen;

	String requestId;
	Set<OrganizationWrap> selectedOrganizations;

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
		Set<OrganizationWrap> organizations = (Set<OrganizationWrap>) Zks.getScopeArg(mainComp, Constants.ARG_SELECTOR_SELECTED_SET);
		if (organizations != null && organizations.size() > 0) {
			selectedOrganizations = new LinkedHashSet<>();
			for (OrganizationWrap r : organizations) {
				selectedOrganizations.add(r);
			}
		}

		pageSize = workspace.getPreferedPageSize();

		organizationsModel = new ListModelList<OrganizationWrap>();
		vorganizations.setModel(organizationsModel);
		vorganizations.setMultiple(Boolean.TRUE.equals(multiple));

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

		Set<OrganizationWrap> organizations = new LinkedHashSet<>();
		if (selectedOrganizations != null && selectedOrganizations.size() > 0) {
			for (OrganizationWrap r : selectedOrganizations) {
				organizations.add(r);
			}
		}

		Zks.closePopup(mainComp);

		workspace.publish(new OrganizationSelectionEvent(requestId, organizations));
	}

	private void doSearch() {

		String keyword = Zks.trimValue(vkeyword);
		CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());
		AOrganizationFilter filter = new AOrganizationFilter().strContaining(true).strIgnoreCase(true)
				.code(keyword).name(keyword).pageSize(pageSize).pageIndex(pageIndex);
		AOrganizationListPage page = api.listOrganization(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listOrganization(filter.pageIndex(pageIndex));
		}
		
		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		organizationsModel.clear();
		organizationsModel.clearSelection();
		for (AOrganization organization : page.getItems()) {
			organizationsModel.add(new OrganizationWrap(organization));
		}
		if (selectedOrganizations != null && selectedOrganizations.size() > 0) {
			for (OrganizationWrap r : selectedOrganizations) {
				organizationsModel.addToSelection(r);
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
		Set<OrganizationWrap> s = organizationsModel.getSelection();

		if (s != null && s.size() > 0) {
			selectedOrganizations = new LinkedHashSet<>();
			for (OrganizationWrap r : s) {
				selectedOrganizations.add(r);
			}
		} else {
			selectedOrganizations = null;
		}
		
		refreshChosen();
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}
	
	private void refreshChosen() {
		vchosen.getChildren().clear();
		if (selectedOrganizations != null && selectedOrganizations.size() > 0) {
			for (OrganizationWrap r : selectedOrganizations) {
				Div d = new Div();
				Label l = new Label(r.getName());
				d.setSclass("chosen");
				d.appendChild(l);
				vchosen.appendChild(d);
			}
		}else {
			vchosen.appendChild(new Label(Zks.getLabel("axes.msg.blankSelection")));
		}
	}
}
