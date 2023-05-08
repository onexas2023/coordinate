package onexas.axes.web.model;

import onexas.coordinate.api.v1.sdk.model.AJob;
import onexas.coordinate.api.v1.sdk.model.JobState;

/**
 * 
 * @author Dennis Chen
 *
 */
public class JobWrap {

	public final AJob delegatee;

	public JobWrap(AJob delegatee) {
		this.delegatee = delegatee;
	}

	public Long getCreatedDateTime() {
		return delegatee.getCreatedDateTime();
	}

	public Long getFinishedDateTime() {
		return delegatee.getFinishedDateTime();
	}

	public String getMessage() {
		return delegatee.getMessage();
	}

	public String getNode() {
		return delegatee.getNode();
	}
	
	public String getSubject() {
		return delegatee.getSubject();
	}

	public String getResultJson() {
		return delegatee.getResultJson();
	}

	public Long getStartedDateTime() {
		return delegatee.getStartedDateTime();
	}

	public JobState getState() {
		return delegatee.getState();
	}

	public Long getId() {
		return delegatee.getId();
	}
	
	

	public Boolean getError() {
		return delegatee.getError();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegatee.getId().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobWrap other = (JobWrap) obj;
		if (!delegatee.getId().equals(other.getId()))
			return false;
		return true;
	}
}
