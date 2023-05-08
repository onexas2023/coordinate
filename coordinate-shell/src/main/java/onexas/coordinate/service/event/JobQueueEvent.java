package onexas.coordinate.service.event;

import java.io.Serializable;

import onexas.coordinate.common.app.ApplicationEvent;

/**
 * 
 * @author Dennis Chen
 *
 */
public abstract class JobQueueEvent extends ApplicationEvent<Serializable> {
	private static final long serialVersionUID = 1L;

	String subject;
	
	public JobQueueEvent(String subject) {
		super(null);
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}
}
