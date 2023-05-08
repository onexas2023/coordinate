package onexas.axes.web.zk.ctrl.admin.job;

import org.zkoss.zk.ui.event.Event;

import onexas.coordinate.api.v1.sdk.model.AJob;
/**
 * 
 * @author Dennis Chen
 *
 */
public class JobEvent extends Event{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JobEventType type;
	AJob job;
	
	public JobEvent(JobEventType type, AJob job) {
		super("onJobEvent");
		this.type = type;
		this.job = job;
	}

	public AJob getJob() {
		return job;
	}

	public void setJob(AJob job) {
		this.job = job;
	}

	public JobEventType getType() {
		return type;
	}
	

}