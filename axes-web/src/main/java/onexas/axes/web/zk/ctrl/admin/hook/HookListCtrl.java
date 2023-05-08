package onexas.axes.web.zk.ctrl.admin.hook;

import java.text.DateFormat;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;

import onexas.axes.web.model.HookWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.coordinate.api.v1.sdk.CoordinateAdminHookApi;
import onexas.coordinate.api.v1.sdk.model.AHook;
import onexas.coordinate.api.v1.sdk.model.AHookFilter;
import onexas.coordinate.api.v1.sdk.model.AHookListPage;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookListCtrl extends CtrlBase {

	@Wire
	Listbox vhooks;
	ListModelList<HookWrap> hooksModel;

	@Wire
	Paging vpaging;

	AHook selectedHook;
	AHookFilter workingFilter;

	int pageSize;
	int pageIndex;
	long totalSize;

	protected void afterCompose() throws Exception {

		pageSize = workspace.getPreferedPageSize();

		hooksModel = new ListModelList<HookWrap>();
		vhooks.setModel(hooksModel);

		mainComp.addEventListener("onSelect", (evt) -> {
			doSelect();
		});
		mainComp.addEventListener("onPaging", (evt) -> {
			doPaging();
		});

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		doSearch();
	}

	@Override
	protected void doSort() {
		doSearch();
	}

	private void doSearch() {

		// domain
		CoordinateAdminHookApi api = new CoordinateAdminHookApi(workspace.getApiClient());
		AHookFilter filter;

		if (workingFilter != null) {
			filter = Jsons.transform(workingFilter, AHookFilter.class);
		} else {
			filter = new AHookFilter();
		}

		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		} else {
			filter.sortField("createdDateTime").sortDesc(Boolean.TRUE);
		}

		filter.pageSize(pageSize).pageIndex(pageIndex);

		AHookListPage page = api.listHook(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listHook(filter.pageIndex(pageIndex));
		}

		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		hooksModel.clear();
		hooksModel.clearSelection();
		for (AHook hook : page.getItems()) {
			hooksModel.add(new HookWrap(hook));
		}
		if (selectedHook != null) {
			hooksModel.addToSelection(new HookWrap(selectedHook));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof HookEvent) {
			AHook hook = ((HookEvent) evt).getHook();
			switch (((HookEvent) evt).getType()) {
			case UPDATED:
				if (selectedHook != null && selectedHook.getUid().equals(selectedHook.getUid())) {
					selectedHook = hook;
				}
				doSearch();
				break;
			case DELETED:
				if (selectedHook != null && selectedHook.getUid().equals(hook.getUid())) {
					selectedHook = null;
				}
				doSearch();
				break;
			default:
			}
		} else if (evt instanceof HookFilterEvent) {
			workingFilter = ((HookFilterEvent) evt).getFilter();
			doSearch();
		}
	}

	private void refreshPaging() {
		vpaging.setActivePage(pageIndex);
		vpaging.setPageSize(pageSize);
		vpaging.setTotalSize((int) totalSize);
	}

	private void doSelect() {
		HookWrap wrap = hooksModel.getSingleSelection();
		selectedHook = wrap == null ? null : wrap.delegatee;
		workspace.publish(new HookEvent(HookEventType.SELECTED, selectedHook));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	public String getCreatedDateTimeInfo(HookWrap hook) {
		Long createdDateTime = hook.getCreatedDateTime();
		DateFormat f = workspace.getPreferredDateTimeTimeZoneFormat();
		return f.format(createdDateTime);
	}

	public String getTriggerInfo(HookWrap hook) {
		Integer trigger = hook.getTrigger();
		Integer life = hook.getTriggerLife();
		StringBuilder sb = new StringBuilder();
		if (trigger != null) {
			sb.append(trigger);
		} else {
			sb.append("0");
		}
		if (life != null) {
			sb.append(" / ").append(life);
		}
		return sb.toString();
	}
}
