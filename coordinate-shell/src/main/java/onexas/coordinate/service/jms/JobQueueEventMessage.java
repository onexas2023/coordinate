package onexas.coordinate.service.jms;

import java.io.Serializable;

import onexas.coordinate.model.UserActivityToken;
import onexas.coordinate.service.event.JobQueueEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobQueueEventMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	Long jobId;

	JobQueueEvent event;
	
	UserActivityToken userActivityToken;

	public JobQueueEventMessage(Long jobId, JobQueueEvent event,UserActivityToken userActivityToken) {
		super();
		this.jobId = jobId;
		this.event = event;
		this.userActivityToken = userActivityToken;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public JobQueueEvent getEvent() {
		return event;
	}

	public void setEvent(JobQueueEvent event) {
		this.event = event;
	}

	public UserActivityToken getUserActivityToken() {
		return userActivityToken;
	}

	public void setUserActivityToken(UserActivityToken userActivityToken) {
		this.userActivityToken = userActivityToken;
	}
	
	

}
