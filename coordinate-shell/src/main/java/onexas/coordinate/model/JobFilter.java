package onexas.coordinate.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.model.Constants;
import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class JobFilter extends PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	protected JobState state;

	protected Boolean error;

	@Schema(ref = Constants.SCHEMA_REF_PREFIX + JobState.SCHEMA_NAME)
	public JobState getState() {
		return state;
	}

	public Boolean getError() {
		return error;
	}

	public void setState(JobState state) {
		this.state = state;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

}