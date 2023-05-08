package onexas.axes.web.zk.ctrl.admin.hook;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.AHook;
/**
 * 
 * @author Dennis Chen
 *
 */
public class HookEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HookEventType type;
	AHook hook;
	
	public HookEvent(HookEventType type, AHook hook) {
		super("onHookEvent");
		this.type = type;
		this.hook = hook;
	}

	public AHook getHook() {
		return hook;
	}

	public void setHook(AHook hook) {
		this.hook = hook;
	}

	public HookEventType getType() {
		return type;
	}
	

}