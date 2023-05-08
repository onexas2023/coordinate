package onexas.axes.web.zk.ctrl.admin.job;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;

import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.coordinate.api.v1.sdk.model.AJobFilter;
import onexas.coordinate.api.v1.sdk.model.JobState;
import onexas.coordinate.common.lang.Collections;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobFilterCtrl extends CtrlBase {

	@Wire
	Listbox verror;
	ListModelList<Boolean> errorModel;

	@Wire
	Listbox vstate;
	ListModelList<JobState> stateModel;

	protected void afterCompose() throws Exception {

		errorModel = new ListModelList<>();
		errorModel.addAll(Collections.asList(Boolean.TRUE, Boolean.FALSE));
		verror.setModel(errorModel);

		stateModel = new ListModelList<>();
		stateModel.addAll(Collections.asList(JobState.values()));
		vstate.setModel(stateModel);

		mainComp.addEventListener("onFilter", (evt) -> {
			doFilter();
		});
		mainComp.addEventListener("onReset", (evt) -> {
			doReset();
		});
	}

	private void doReset() {
		errorModel.clearSelection();
		stateModel.clearSelection();
		doFilter();
	}

	private void doFilter() {
		Boolean error = errorModel.getSingleSelection();
		JobState state = stateModel.getSingleSelection();

		AJobFilter filter = new AJobFilter();
		if (error != null) {
			filter.setError(error);
		}
		if (state != null) {
			filter.setState(state);
		}

		filter.strContaining(true).strIgnoreCase(true);
		workspace.publish(new JobFilterEvent(filter));
	}
}
