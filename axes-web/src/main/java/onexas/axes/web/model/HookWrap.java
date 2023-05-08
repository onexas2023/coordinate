package onexas.axes.web.model;

import onexas.coordinate.api.v1.sdk.model.AHook;

/**
 * 
 * @author Dennis Chen
 *
 */
public class HookWrap {

	public final AHook delegatee;

	public HookWrap(AHook delegatee) {
		this.delegatee = delegatee;
	}

	public Long getCreatedDateTime() {
		return delegatee.getCreatedDateTime();
	}

	public String getUid() {
		return delegatee.getUid();
	}

	public String getData() {
		return delegatee.getData();
	}

	public String getDescription() {
		return delegatee.getDescription();
	}

	public String getOwnerType() {
		return delegatee.getOwnerType();
	}

	public String getOwnerUid() {
		return delegatee.getOwnerUid();
	}

	public String getSubjectType() {
		return delegatee.getSubjectType();
	}

	public String getSubjectUid() {
		return delegatee.getSubjectUid();
	}

	public Integer getTrigger() {
		return delegatee.getTrigger();
	}

	public Integer getTriggerLife() {
		return delegatee.getTriggerLife();
	}

	public String getZone() {
		return delegatee.getZone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delegatee.getUid().hashCode();
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
		HookWrap other = (HookWrap) obj;
		if (!delegatee.getUid().equals(other.getUid()))
			return false;
		return true;
	}
}
