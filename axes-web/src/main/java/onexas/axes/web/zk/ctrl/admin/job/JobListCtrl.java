package onexas.axes.web.zk.ctrl.admin.job;

import java.text.DateFormat;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;

import onexas.axes.web.model.JobWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.coordinate.api.v1.sdk.CoordinateAdminJobApi;
import onexas.coordinate.api.v1.sdk.model.AJob;
import onexas.coordinate.api.v1.sdk.model.AJobFilter;
import onexas.coordinate.api.v1.sdk.model.AJobListPage;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobListCtrl extends CtrlBase {

	@Wire
	Listbox vjobs;
	ListModelList<JobWrap> jobsModel;

	@Wire
	Paging vpaging;

	AJob selectedJob;
	AJobFilter workingFilter;

	int pageSize;
	int pageIndex;
	long totalSize;

	protected void afterCompose() throws Exception {

		pageSize = workspace.getPreferedPageSize();

		jobsModel = new ListModelList<JobWrap>();
		vjobs.setModel(jobsModel);

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
		CoordinateAdminJobApi api = new CoordinateAdminJobApi(workspace.getApiClient());
		AJobFilter filter;
		
		if(workingFilter!=null) {
			filter = Jsons.transform(workingFilter, AJobFilter.class);
		}else {
			filter = new AJobFilter();
		}
		
		if (sortBy != null) {
			filter.sortField(sortBy).sortDesc(sortDesc);
		}else {
			filter.sortField("createdDateTime").sortDesc(Boolean.TRUE);
		}
		
		filter.pageSize(pageSize).pageIndex(pageIndex);
		
		AJobListPage page = api.listJob(filter);
		if (pageIndex > 0 && pageIndex > page.getPageTotal() - 1) {
			pageIndex = page.getPageTotal() > 0 ? page.getPageTotal() - 1 : 0;
			page = api.listJob(filter.pageIndex(pageIndex));
		}

		pageIndex = page.getPageIndex();
		totalSize = page.getItemTotal();

		jobsModel.clear();
		jobsModel.clearSelection();
		for (AJob job : page.getItems()) {
			jobsModel.add(new JobWrap(job));
		}
		if (selectedJob != null) {
			jobsModel.addToSelection(new JobWrap(selectedJob));
		}

		refreshPaging();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof JobEvent) {
//			Job job = ((JobEvent) evt).getJob();
			switch (((JobEvent) evt).getType()) {
			default:
			}
		} else if (evt instanceof JobFilterEvent) {
			workingFilter = ((JobFilterEvent) evt).getFilter();
			doSearch();
		}
	}

	private void refreshPaging() {
		vpaging.setActivePage(pageIndex);
		vpaging.setPageSize(pageSize);
		vpaging.setTotalSize((int) totalSize);
	}

	private void doSelect() {
		JobWrap wrap = jobsModel.getSingleSelection();
		selectedJob = wrap == null ? null : wrap.delegatee;
		workspace.publish(new JobEvent(JobEventType.SELECTED, selectedJob));
	}

	private void doPaging() {
		pageIndex = vpaging.getActivePage();
		doSearch();
	}

	public String getDateTimeInfo(Long dateTime) {
		if (dateTime == null) {
			return "";
		}
		DateFormat f = workspace.getPreferredDateTimeTimeZoneFormat();
		return f.format(dateTime);
	}

	public String getErrorIconSclass(JobWrap job) {
		if (Boolean.TRUE.equals(job.getError())) {
			return "fas fa-bomb";
		}
		return "";
	}
}
