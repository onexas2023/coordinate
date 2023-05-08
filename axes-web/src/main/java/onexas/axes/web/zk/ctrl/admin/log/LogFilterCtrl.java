package onexas.axes.web.zk.ctrl.admin.log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

import onexas.axes.web.model.LogWrap;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.ListModelList;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.model.ALogFilter;
import onexas.coordinate.api.v1.sdk.model.LongBetween;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.util.CalendarHelper;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LogFilterCtrl extends CtrlBase {

	@Wire
	Textbox vsubjectId;

	@Wire
	Textbox vsubjectType;

	@Wire
	Textbox vobjId;

	@Wire
	Textbox vobjType;

	@Wire
	Datebox vdate;

	@Wire
	Timebox vtime;

	@Wire
	Label vtimeZone;

	@Wire
	Listbox vlevel;
	ListModelList<Integer> levelModel;

	protected void afterCompose() throws Exception {

		levelModel = new ListModelList<>();
		levelModel.add(0);
		levelModel.add(1);
		levelModel.add(2);
		levelModel.add(3);
		vlevel.setModel(levelModel);

		TimeZone timeZone = workspace.getPreferredTimeZone();

		vdate.setTimeZone(timeZone);
		vdate.setFormat(workspace.getPreferredDatePattern());
		vtime.setTimeZone(timeZone);
		vtime.setFormat(workspace.getPreferredTimePattern());

		vtimeZone.setValue(timeZone.getDisplayName(workspace.getPreferredLocale()) + "(" + timeZone.getID() + ")");

		mainComp.addEventListener("onFilter", (evt) -> {
			doFilter();
		});
		mainComp.addEventListener("onReset", (evt) -> {
			doReset();
		});
	}

	private void doReset() {
		vsubjectId.setValue(null);
		vsubjectType.setValue(null);
		vobjId.setValue(null);
		vobjType.setValue(null);
		vdate.setValue(null);
		vtime.setValue(null);
		levelModel.clearSelection();
		doFilter();
	}

	private void doFilter() {
		String subjectUid = Zks.trimValue(vsubjectId);
		String subjectType = Zks.trimValue(vsubjectType);
		String objId = Zks.trimValue(vobjId);
		String objType = Zks.trimValue(vobjType);
		Date date = vdate.getValue();
		Date time = vtime.getValue();
		Integer level = levelModel.getSingleSelection();

		ALogFilter filter = new ALogFilter();
		if (!Strings.isBlank(subjectUid)) {
			filter.setSubjectUid(subjectUid);
		}
		if (!Strings.isBlank(subjectType)) {
			filter.setSubjectType(subjectType);
		}
		if (!Strings.isBlank(objId)) {
			filter.setObjUid(objId);
		}
		if (!Strings.isBlank(objType)) {
			filter.setObjType(objType);
		}
		if (date != null && time != null) {
			CalendarHelper helper = workspace.getCalendarHelper();
			Calendar cdate = helper.calendar(helper.merge(date, time));
			cdate.set(Calendar.MILLISECOND, 000);
			LongBetween between = new LongBetween().to(cdate.getTimeInMillis());
			filter.setCreatedDateTimeBetween(between);
		} else if (date != null) {
			CalendarHelper helper = workspace.getCalendarHelper();
			date = helper.toDayEnd(date);
			LongBetween between = new LongBetween().to(date.getTime());
			filter.setCreatedDateTimeBetween(between);
		}
		if (level != null) {
			filter.setLevelGe(level);
		}

		filter.strContaining(true).strIgnoreCase(true);
		workspace.publish(new LogFilterEvent(filter));
	}

	public String getLevelInfo(Integer level) {
		return LogWrap.getLevelInfo(level);
	}
}
