package onexas.coordinate.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.Constants;
import onexas.coordinate.common.util.Jsons;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Job implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Long id;

	protected String subject;

	protected String node;

	protected Long createdDateTime;

	protected Long startedDateTime;

	protected JobState state;

	protected Long finishedDateTime;

	protected Boolean error;

	protected String message;

	protected String requestUid;

	protected String resultJson;
	
	protected String queryUid;

	public Long getId() {
		return id;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + JobState.SCHEMA_NAME)
	public JobState getState() {
		return state;
	}

	public Long getFinishedDateTime() {
		return finishedDateTime;
	}

	public Boolean getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getNode() {
		return node;
	}

	public String getResultJson() {
		return resultJson;
	}

	public Long getStartedDateTime() {
		return startedDateTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getQueryUid() {
		return queryUid;
	}

	public void setQueryUid(String queryUid) {
		this.queryUid = queryUid;
	}

	public String getRequestUid() {
		return requestUid;
	}

	public void setRequestUid(String requestUid) {
		this.requestUid = requestUid;
	}

	public void setStartedDateTime(Long startedDateTime) {
		this.startedDateTime = startedDateTime;
	}

	public <R> R objectifyResult(Class<R> clz) {
		if (resultJson != null) {
			return Jsons.objectify(resultJson, clz);
		}
		return null;
	}

	public void setResultJson(String resultJson) {
		this.resultJson = resultJson;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
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