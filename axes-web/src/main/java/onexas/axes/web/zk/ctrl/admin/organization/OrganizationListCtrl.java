package onexas.axes.web.zk.ctrl.admin.organization;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;

import onexas.axes.web.model.OrganizationWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminOrganizationApi;
import onexas.coordinate.api.v1.sdk.model.AOrganization;
import onexas.coordinate.api.v1.sdk.model.AOrganizationFilter;
import onexas.coordinate.api.v1.sdk.model.AOrganizationListPage;

/**
 * 
 * @author Dennis Chen
 *
 */
public class OrganizationListCtrl extends CtrlBase {

	@Wire
	Textbox vkeyword;

	@Wire
	Listbox vorganizations;
	ListModelList<OrganizationWrap> organizationsModel;

	@Wire
	Paging vpaging;

	AOrganization selectedOrganization;

	int pageSize;
	int pageIndex;
	long totalSize;

	protected void afterCompose() throws Exception {

		pageSize = workspace.getPreferedPageSize();

		organizationsModel = new ListModelList<OrganizationWrap>();
		vorganizations.setModel(organizationsModel);

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
		CoordinateAdminOrganizationApi api = new CoordinateAdminOrganizationApi(workspace.getApiClient());
		AOrganizationFilter filter = new AOrganizationFilter().strContaining(true).strIgnoreCase(true).name(keyword)
				.code(keyword).pageSize(pageSize).pageIndex(pageIndex);
		
		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		}

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
		if (selectedOrganization != null) {
			organizationsModel.addToSelection(new OrganizationWrap(selectedOrganization));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof OrganizationEvent) {
			AOrganization organization = ((OrganizationEvent) evt).getOrganization();
			switch (((OrganizationEvent) evt).getType()) {
			case CREATED:
				selectedOrganization = organization;
				doSearch();
				break;
			case DELETED:
				if (selectedOrganization != null && selectedOrganization.getUid().equals(organization.getUid())) {
					selectedOrganization = null;
				}
				doSearch();
				break;
			case UPDATED:
				if (selectedOrganization != null && selectedOrganization.getUid().equals(organization.getUid())) {
					selectedOrganization = organization;
				}
				doSearch();
				break;
			case START_TO_CREATE:
				selectedOrganization = null;
				organizationsModel.clearSelection();
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
		OrganizationWrap wrap = organizationsModel.getSingleSelection();
		selectedOrganization = wrap == null ? null : wrap.delegatee;
		workspace.publish(new OrganizationEvent(OrganizationEventType.SELECTED, selectedOrganization));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}
}
