package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Log implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;

	protected Long id;

	protected Long createdDateTime;

	protected String subjectUid;

	protected String subjectType;

	protected String objUid;

	protected String objType;

	protected String reporter;

	protected String content;

	protected Integer level;

	protected String requestUid;

	protected String userAccount;

	protected String userDomain;

	public Long getId() {
		return id;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public String getSubjectUid() {
		return subjectUid;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public String getObjUid() {
		return objUid;
	}

	public String getObjType() {
		return objType;
	}

	public String getReporter() {
		return reporter;
	}

	public String getContent() {
		return content;
	}

	public Integer getLevel() {
		return level;
	}

	public String getRequestUid() {
		return requestUid;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public String getUserDomain() {
		return userDomain;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}

	public void setRequestUid(String requestUid) {
		this.requestUid = requestUid;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setObjUid(String objUid) {
		this.objUid = objUid;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
}