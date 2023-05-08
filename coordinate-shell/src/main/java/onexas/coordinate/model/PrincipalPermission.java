package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author Dennis Chen
 *
 */

public class PrincipalPermission implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String target;

	protected String action;

	public PrincipalPermission() {
	}

	public PrincipalPermission(String target, String action) {
		this.target = target;
		this.action = action;
	}

	@NotNull
	@Schema(required = true)
	public String getTarget() {
		return target;
	}

	@NotNull
	@Schema(required = true)
	public String getAction() {
		return action;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PrincipalPermission))
			return false;
		PrincipalPermission other = (PrincipalPermission) obj;
		if (action == null) {
			return false;
		} else if (!action.equals(other.action))
			return false;
		if (target == null) {
			return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PrincipalPermission [" + (target != null ? "target=" + target + ", " : "")
				+ (action != null ? "action=" + action : "") + "]";
	}

}
