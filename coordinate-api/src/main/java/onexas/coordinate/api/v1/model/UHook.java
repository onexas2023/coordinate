package onexas.coordinate.api.v1.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UHook implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String uid;

	protected Long createdDateTime;

	protected Integer trigger;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Integer getTrigger() {
		return trigger;
	}

	public void setTrigger(Integer trigger) {
		this.trigger = trigger;
	}

}
