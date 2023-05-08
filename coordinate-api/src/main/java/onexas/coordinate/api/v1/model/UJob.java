package onexas.coordinate.api.v1.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.Constants;
import onexas.coordinate.model.JobState;

/**
 * 
 * @author Dennis Chen
 *
 */

public class UJob implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String queryUid;

	protected Long createdDateTime;

	protected Long startedDateTime;

	protected JobState state;

	protected Long finishedDateTime;

	protected Boolean error;

	protected String message;

	@Schema
	public String getQueryUid() {
		return queryUid;
	}

	@Schema
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@Schema
	public Long getStartedDateTime() {
		return startedDateTime;
	}

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + JobState.SCHEMA_NAME)
	public JobState getState() {
		return state;
	}

	@Schema
	public Long getFinishedDateTime() {
		return finishedDateTime;
	}

	@Schema
	public Boolean getError() {
		return error;
	}

	@Schema
	public String getMessage() {
		return message;
	}

	public void setQueryUid(String queryUid) {
		this.queryUid = queryUid;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setStartedDateTime(Long startedDateTime) {
		this.startedDateTime = startedDateTime;
	}

	public void setState(JobState state) {
		this.state = state;
	}

	public void setFinishedDateTime(Long finishedDateTime) {
		this.finishedDateTime = finishedDateTime;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
