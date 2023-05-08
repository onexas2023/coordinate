package onexas.axes.web.zk.ctrl.admin.job;

import java.text.DateFormat;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.model.AJob;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobInfoCtrl extends CtrlBase {

	@Wire
	Label vsubject;
	@Wire
	Label vnode;

	@Wire
	Label vcreatedDateTime;
	@Wire
	Label vstartedDateTime;

	@Wire
	Label vfinishedDateTime;

	@Wire
	Label vstate;
	@Wire
	Label verror;

	@Wire
	Label vmessage;

	@Wire
	Label vresultJson;

	AJob selectedJob;

	protected void afterCompose() throws Exception {

		selectedJob = (AJob) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshInfo();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof JobEvent) {
			AJob job = ((JobEvent) evt).getJob();
			switch (((JobEvent) evt).getType()) {
			case SELECTED:
				selectedJob = job;
				refreshInfo();
				break;
			default:
			}
		}
	}

	private void refreshInfo() {
		if (selectedJob == null) {
			vsubject.setValue("");
			vnode.setValue("");
			vcreatedDateTime.setValue("");
			vstartedDateTime.setValue("");
			vfinishedDateTime.setValue("");
			vstate.setValue("");
			verror.setValue("");
			vmessage.setValue("");
			vresultJson.setValue("");
		} else {
			vsubject.setValue(selectedJob.getSubject());
			vnode.setValue(selectedJob.getNode());
			vcreatedDateTime.setValue(getDateTimeInfo(selectedJob.getCreatedDateTime()));
			vstartedDateTime.setValue(getDateTimeInfo(selectedJob.getStartedDateTime()));
			vfinishedDateTime.setValue(getDateTimeInfo(selectedJob.getFinishedDateTime()));
			vstate.setValue(Strings.toString(selectedJob.getState()));			
			verror.setValue(Strings.toString(selectedJob.getError()));
			vmessage.setValue(selectedJob.getMessage());
			String json = selectedJob.getResultJson();
			if(!Strings.isBlank(json)) {
				json = Jsons.pretty(json);
			}
			vresultJson.setValue(json);
		}
	}

	public String getDateTimeInfo(Long dateTime) {
		if(dateTime==null) {
			return "";
		}
		DateFormat f = workspace.getPreferredDateTimeTimeZoneFormat();
		return f.format(dateTime);
	}
}
