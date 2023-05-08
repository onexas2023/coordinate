package onexas.axes.web.zk.ctrl.admin.log;

import java.text.DateFormat;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import onexas.axes.web.Constants;
import onexas.axes.web.model.LogWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.model.ALog;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LogInfoCtrl extends CtrlBase {

	@Wire
	Label vsubjectId;

	@Wire
	Label vsubjectType;

	@Wire
	Label vobjType;

	@Wire
	Label vobjId;
	@Wire
	Label vreporter;

	@Wire
	Label vcreatedDateTime;

	@Wire
	Label vlevel;

	@Wire
	Label vcontent;

	ALog selectedLog;

	protected void afterCompose() throws Exception {

		selectedLog = (ALog) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshInfo();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof LogEvent) {
			ALog log = ((LogEvent) evt).getLog();
			switch (((LogEvent) evt).getType()) {
			case SELECTED:
				selectedLog = log;
				refreshInfo();
				break;
			case DELETED:
				if (selectedLog != null && selectedLog.getId().equals(log.getId())) {
					selectedLog = null;
				}
				refreshInfo();
				break;
			default:
			}
		}
	}

	private void refreshInfo() {
		if (selectedLog == null) {
			vsubjectId.setValue("");
			vsubjectType.setValue("");
			vobjType.setValue("");
			vobjId.setValue("");
			vcreatedDateTime.setValue("");
			vlevel.setValue("");
			vcontent.setValue("");
			vreporter.setValue("");
		} else {
			vsubjectId.setValue(selectedLog.getSubjectUid());
			vsubjectType.setValue(selectedLog.getSubjectType());
			vobjType.setValue(selectedLog.getObjType());
			vobjId.setValue(selectedLog.getObjUid());
			vcreatedDateTime.setValue(getCreatedDateTimeInfo(selectedLog));
			vlevel.setValue(getLevelInfo(selectedLog));
			vcontent.setValue(selectedLog.getContent());
			vreporter.setValue(selectedLog.getReporter());
		}
	}

	public String getLevelInfo(ALog log) {
		return LogWrap.getLevelInfo(log);
	}

	public String getCreatedDateTimeInfo(ALog log) {
		Long createdDateTime = log.getCreatedDateTime();
		DateFormat f = workspace.getPreferredDateTimeFormat();
		return f.format(createdDateTime);
	}
}
