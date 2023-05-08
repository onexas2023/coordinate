package onexas.axes.web.zk.ctrl.admin.log;

import java.text.DateFormat;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;

import onexas.axes.web.model.LogWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.coordinate.api.v1.sdk.CoordinateAdminLogApi;
import onexas.coordinate.api.v1.sdk.model.ALog;
import onexas.coordinate.api.v1.sdk.model.ALogFilter;
import onexas.coordinate.api.v1.sdk.model.ALogListPage;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LogListCtrl extends CtrlBase {

	@Wire
	Listbox vlogs;
	ListModelList<LogWrap> logsModel;

	@Wire
	Paging vpaging;

	ALog selectedLog;
	ALogFilter workingFilter;

	int pageSize;
	int pageIndex;
	long totalSize;

	protected void afterCompose() throws Exception {

		pageSize = workspace.getPreferedPageSize();

		logsModel = new ListModelList<LogWrap>();
		vlogs.setModel(logsModel);

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
		CoordinateAdminLogApi api = new CoordinateAdminLogApi(workspace.getApiClient());
		ALogFilter filter;

		if (workingFilter != null) {
			filter = Jsons.transform(workingFilter, ALogFilter.class);
		} else {
			filter = new ALogFilter();
		}

		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		}else {
			filter.sortField("createdDateTime").sortDesc(Boolean.TRUE);
		}

		filter.pageSize(pageSize).pageIndex(pageIndex);

		ALogListPage page = api.listLog(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listLog(filter.pageIndex(pageIndex));
		}

		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		logsModel.clear();
		logsModel.clearSelection();
		for (ALog log : page.getItems()) {
			logsModel.add(new LogWrap(log));
		}
		if (selectedLog != null) {
			logsModel.addToSelection(new LogWrap(selectedLog));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof LogEvent) {
			ALog log = ((LogEvent) evt).getLog();
			switch (((LogEvent) evt).getType()) {
			case DELETED:
				if (selectedLog != null && selectedLog.getId().equals(log.getId())) {
					selectedLog = null;
				}
				doSearch();
				break;
			default:
			}
		} else if (evt instanceof LogFilterEvent) {
			workingFilter = ((LogFilterEvent) evt).getFilter();
			doSearch();
		}
	}

	private void refreshPaging() {
		vpaging.setActivePage(pageIndex);
		vpaging.setPageSize(pageSize);
		vpaging.setTotalSize((int) totalSize);
	}

	private void doSelect() {
		LogWrap wrap = logsModel.getSingleSelection();
		selectedLog = wrap == null ? null : wrap.delegatee;
		workspace.publish(new LogEvent(LogEventType.SELECTED, selectedLog));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	public String getLevelInfo(LogWrap log) {
		return LogWrap.getLevelInfo(log.delegatee);
	}

	public String getCreatedDateTimeInfo(LogWrap log) {
		Long createdDateTime = log.getCreatedDateTime();
		DateFormat f = workspace.getPreferredDateTimeTimeZoneFormat();
		return f.format(createdDateTime);
	}
}
