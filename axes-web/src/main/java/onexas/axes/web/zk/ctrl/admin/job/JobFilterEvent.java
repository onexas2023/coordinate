package onexas.axes.web.zk.ctrl.admin.job;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.AJobFilter;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobFilterEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AJobFilter filter;

	public JobFilterEvent(AJobFilter filter) {
		super("onJobFilterEvent");
		this.filter = filter;
	}

	public AJobFilter getFilter() {
		return filter;
	}

	public void setFilter(AJobFilter filter) {
		this.filter = filter;
	}

}