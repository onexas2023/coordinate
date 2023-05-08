package onexas.axes.web.zk.ctrl.admin.hook;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.AHookFilter;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookFilterEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AHookFilter filter;

	public HookFilterEvent(AHookFilter filter) {
		super("onHookFilterEvent");
		this.filter = filter;
	}

	public AHookFilter getFilter() {
		return filter;
	}

	public void setFilter(AHookFilter filter) {
		this.filter = filter;
	}

}