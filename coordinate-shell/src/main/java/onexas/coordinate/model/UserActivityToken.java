package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */
public class UserActivityToken implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String requestUid;
	protected String userAccount;
	protected String userDomain;
	protected String userDisplayName;
	protected Long createdDateTime;

	public UserActivityToken() {
	}

	public UserActivityToken(String requestUid, String userAccount, String userDomain, String userDisplayName) {
		this.requestUid = requestUid;
		this.userAccount = userAccount;
		this.userDomain = userDomain;
		this.userDisplayName = userDisplayName;
		this.createdDateTime = System.currentTimeMillis();
	}

	public String getRequestUid() {
		return requestUid;
	}

	public void setRequestUid(String requestUid) {
		this.requestUid = requestUid;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public String getUserDomain() {
		return userDomain;
	}

	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	@Override
	public String toString() {
		return "UserActivityToken [" + (requestUid != null ? "requestUid=" + requestUid + ", " : "")
				+ (userAccount != null ? "userAccount=" + userAccount + ", " : "")
				+ (userDomain != null ? "userDomain=" + userDomain + ", " : "")
				+ (userDisplayName != null ? "userDisplayName=" + userDisplayName + ", " : "")
				+ (createdDateTime != null ? "createdDateTime=" + createdDateTime : "") + "]";
	}

}
