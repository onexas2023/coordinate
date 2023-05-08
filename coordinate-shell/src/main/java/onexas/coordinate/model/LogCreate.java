package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class LogCreate implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String subjectUid;

	protected String subjectType;

	protected String objUid;

	protected String objType;

	protected String reporter;

	protected String content;

	protected Integer level;

	public String getObjUid() {
		return objUid;
	}

	public String getObjType() {
		return objType;
	}

	public String getSubjectUid() {
		return subjectUid;
	}

	public String getSubjectType() {
		return subjectType;
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

	public LogCreate withObjUid(String objUid) {
		this.objUid = objUid;
		return this;
	}

	public LogCreate withObjType(String objType) {
		this.objType = objType;
		return this;
	}

	public LogCreate withSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
		return this;
	}

	public LogCreate withSubjectType(String subjectType) {
		this.subjectType = subjectType;
		return this;
	}

	public LogCreate withReporter(String reporter) {
		this.reporter = reporter;
		return this;
	}

	public LogCreate withContent(String content) {
		this.content = content;
		return this;
	}

	public LogCreate withLevel(int level) {
		this.level = level;
		return this;
	}

}