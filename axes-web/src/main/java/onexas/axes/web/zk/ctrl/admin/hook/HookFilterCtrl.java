package onexas.axes.web.zk.ctrl.admin.hook;

import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.model.AHookFilter;
import onexas.coordinate.common.lang.Strings;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookFilterCtrl extends CtrlBase {

	@Wire
	Textbox vuid;
	
	@Wire
	Textbox vzone;

	@Wire
	Textbox vsubjectUid;

	@Wire
	Textbox vsubjectType;

	@Wire
	Textbox vownerUid;

	@Wire
	Textbox vownerType;

	protected void afterCompose() throws Exception {

		mainComp.addEventListener("onFilter", (evt) -> {
			doFilter();
		});
		mainComp.addEventListener("onReset", (evt) -> {
			doReset();
		});
	}

	private void doReset() {
		vsubjectUid.setValue(null);
		vsubjectType.setValue(null);
		vownerUid.setValue(null);
		vownerType.setValue(null);

		doFilter();
	}

	private void doFilter() {
		String uid = Zks.trimValue(vuid);
		String zone = Zks.trimValue(vzone);
		String subjectUid = Zks.trimValue(vsubjectUid);
		String subjectType = Zks.trimValue(vsubjectType);
		String ownerUid = Zks.trimValue(vownerUid);
		String ownerType = Zks.trimValue(vownerType);

		AHookFilter filter = new AHookFilter();
		if (!Strings.isBlank(uid)) {
			filter.setUid(uid);
		}
		if (!Strings.isBlank(zone)) {
			filter.setZone(zone);
		}
		if (!Strings.isBlank(subjectUid)) {
			filter.setSubjectUid(subjectUid);
		}
		if (!Strings.isBlank(subjectType)) {
			filter.setSubjectType(subjectType);
		}
		if (!Strings.isBlank(ownerUid)) {
			filter.setOwnerUid(ownerUid);
		}
		if (!Strings.isBlank(ownerType)) {
			filter.setOwnerType(ownerType);
		}

		filter.strContaining(true).strIgnoreCase(true);
		workspace.publish(new HookFilterEvent(filter));
	}
}
