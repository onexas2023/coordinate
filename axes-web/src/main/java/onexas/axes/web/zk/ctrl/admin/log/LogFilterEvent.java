package onexas.axes.web.zk.ctrl.admin.log;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.ALogFilter;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LogFilterEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ALogFilter filter;

	public LogFilterEvent(ALogFilter filter) {
		super("onLogFilterEvent");
		this.filter = filter;
	}

	public ALogFilter getFilter() {
		return filter;
	}

	public void setFilter(ALogFilter filter) {
		this.filter = filter;
	}

}