package onexas.coordinate.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Permission implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ANY_RPINCIPAL = "*";
	public static final String ANY_TARGET = "*";
	public static final String ANY_ACTION = "*";

	protected String uid;

	protected String principal;

	protected String target;

	protected String action;

	protected String remark;

	@NotNull
	public String getUid() {
		return uid;
	}

	@NotNull
	public String getPrincipal() {
		return principal;
	}

	public String getRemark() {
		return remark;
	}

	@NotNull
	public String getTarget() {
		return target;
	}

	@NotNull
	public String getAction() {
		return action;
	}

	protected void setUid(String uid) {
		this.uid = uid;
	}

	protected void setPrincipal(String principal) {
		this.principal = principal;
	}

	protected void setRemark(String remark) {
		this.remark = remark;
	}

	protected void setTarget(String target) {
		this.target = target;
	}

	protected void setAction(String action) {
		this.action = action;
	}
}