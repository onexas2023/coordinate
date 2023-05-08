package onexas.axes.web.zk.ctrl.admin.hook;

import java.text.DateFormat;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import onexas.axes.web.Constants;
import onexas.axes.web.zk.util.CtrlBase;
import onexas.axes.web.zk.util.Messageboxes;
import onexas.axes.web.zk.util.Zks;
import onexas.coordinate.api.v1.sdk.CoordinateAdminHookApi;
import onexas.coordinate.api.v1.sdk.model.AHook;
import onexas.coordinate.common.app.AppContext;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookInfoCtrl extends CtrlBase {

	@Wire
	Label vuid;

	@Wire
	Label vsubjectUid;

	@Wire
	Label vsubjectType;

	@Wire
	Label vownerType;

	@Wire
	Label vownerUid;

	@Wire
	Label vcreatedDateTime;

	@Wire
	Label vdata;

	@Wire
	Label vzone;

	@Wire
	Label vdescription;

	@Wire
	Label vtrigger;

	@Wire
	Label vurl;

	AHook selectedHook;

	org.springframework.expression.Expression hookUrlExp;

	protected void afterCompose() throws Exception {

		String hookUrlExpr = AppContext.config().getString("axes.hookUrlExpr");

		ExpressionParser parser = new SpelExpressionParser();
		hookUrlExp = parser.parseExpression(hookUrlExpr);

		selectedHook = (AHook) Zks.getScopeArg(mainComp, Constants.ARG_EDITING_OBJ);

		mainComp.addEventListener("onTrigger", (evt) -> {
			doTrigger();
		});
		mainComp.addEventListener("onDelete", (evt) -> {
			doDelete();
		});

		workspace.subscribe((evt) -> {
			doWorkspaceEvent(evt);
		}, () -> {
			return mainComp.getPage() != null;
		});

		refreshInfo();
	}

	private void doWorkspaceEvent(Event evt) {
		if (evt instanceof HookEvent) {
			AHook hook = ((HookEvent) evt).getHook();
			switch (((HookEvent) evt).getType()) {
			case SELECTED:
			case UPDATED:
				selectedHook = hook;
				refreshInfo();
				break;
			case DELETED:
				if (selectedHook != null && selectedHook.getUid().equals(hook.getUid())) {
					selectedHook = null;
				}
				refreshInfo();
				break;
			default:
			}
		}
	}

	private void refreshInfo() {
		if (selectedHook == null) {
			vuid.setValue("");
			vzone.setValue("");
			vsubjectUid.setValue("");
			vsubjectType.setValue("");
			vownerType.setValue("");
			vownerUid.setValue("");
			vcreatedDateTime.setValue("");
			vdata.setValue("");
			vdescription.setValue("");
			vtrigger.setValue("");
			vurl.setValue("");
		} else {
			vuid.setValue(selectedHook.getUid());
			vzone.setValue(selectedHook.getZone());
			vsubjectUid.setValue(selectedHook.getSubjectUid());
			vsubjectType.setValue(selectedHook.getSubjectType());
			vownerType.setValue(selectedHook.getOwnerType());
			vownerUid.setValue(selectedHook.getOwnerUid());
			vcreatedDateTime.setValue(getCreatedDateTimeInfo(selectedHook));
			vdata.setValue(selectedHook.getData());
			vdescription.setValue(selectedHook.getDescription());
			vtrigger.setValue(getTriggerInfo(selectedHook));

			String basePath = workspace.getApiClient().getBasePath();

			StandardEvaluationContext context = new StandardEvaluationContext();
			context.setVariable("apiBasePath", basePath);
			context.setVariable("hook", selectedHook);

			String url = hookUrlExp.getValue(context, String.class);

			vurl.setValue(url);
		}
	}

	public String getCreatedDateTimeInfo(AHook hook) {
		Long createdDateTime = hook.getCreatedDateTime();
		DateFormat f = workspace.getPreferredDateTimeTimeZoneFormat();
		return f.format(createdDateTime);
	}

	public String getTriggerInfo(AHook hook) {
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

	private void doTrigger() {
		if (selectedHook == null) {
			return;
		}

		CoordinateAdminHookApi api = new CoordinateAdminHookApi(workspace.getApiClient());
		AHook hook = api.triggerHook(selectedHook.getUid());

		if (hook.getTriggerLife() != null && hook.getTrigger() != null
				&& hook.getTrigger().intValue() >= hook.getTriggerLife().intValue()) {
			workspace.publish(new HookEvent(HookEventType.DELETED, hook));
		} else {
			workspace.publish(new HookEvent(HookEventType.UPDATED, hook));
		}
		Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyUpdated", hook.getUid()));
	}

	private void doDelete() {
		if (selectedHook == null) {
			return;
		}
		final AHook hook = selectedHook;
		Messageboxes.showConfirmationBox(null, Zks.getLabelWithArg("axes.msg.confirmDelete", selectedHook.getUid()),
				(evt) -> {
					Zks.showBusyThenRun(() -> {
						CoordinateAdminHookApi api = new CoordinateAdminHookApi(workspace.getApiClient());
						api.deleteHook(hook.getUid(), false);
						workspace.publish(new HookEvent(HookEventType.DELETED, hook));
						Zks.showClientNotification(Zks.getLabelWithArg("axes.msg.notifyDeleted", hook.getUid()));
					});
				}, null);
	}
}
