package onexas.axes.web.model;

import onexas.coordinate.api.v1.sdk.model.ALog;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LogWrap {

	public final ALog delegatee;

	public LogWrap(ALog delegatee) {
		this.delegatee = delegatee;
	}

	public String getRequestUid() {
		return delegatee.getRequestUid();
	}

	public String getContent() {
		return delegatee.getContent();
	}

	public Long getCreatedDateTime() {
		return delegatee.getCreatedDateTime();
	}

	public Long getId() {
		return delegatee.getId();
	}

	public Integer getLevel() {
		return delegatee.getLevel();
	}

	public String getObjType() {
		return delegatee.getObjType();
	}

	public String getObjUid() {
		return delegatee.getObjUid();
	}

	public String getReporter() {
		return delegatee.getReporter();
	}

	public String getSubjectType() {
		return delegatee.getSubjectType();
	}

	public String getSubjectUid() {
		return delegatee.getSubjectUid();
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
		LogWrap other = (LogWrap) obj;
		if (!delegatee.getId().equals(other.getId()))
			return false;
		return true;
	}
	
	public static String getLevelInfo(ALog log) {
		return getLevelInfo(log.getLevel());
	}
	
	public static String getLevelInfo(int level) {
		switch (level) {
		case 0:
			return "DEBUG";
		case 1:
			return "INFO";
		case 2:
			return "WARN";
		case 3:
			return "ERROR";
		default:
			return Integer.toString(level);
		}
		
	}
}
