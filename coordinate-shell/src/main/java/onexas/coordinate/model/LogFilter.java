package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.model.LongBetween;
import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class LogFilter extends PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String subjectUid;

	protected String subjectType;

	protected String objUid;

	protected String objType;

	protected String requestUid;

	protected String reporter;

	protected Integer levelGe;

	protected LongBetween createdDateTimeBetween;

	public LogFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public LogFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public LogFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public LogFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public LogFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public LogFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public LogFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
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

	public Integer getLevelGe() {
		return levelGe;
	}

	public String getRequestUid() {
		return requestUid;
	}

	public String getSubjectUid() {
		return subjectUid;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public LongBetween getCreatedDateTimeBetween() {
		return createdDateTimeBetween;
	}

	public void setCreatedDateTimeBetween(LongBetween createdDateTimeBetween) {
		this.createdDateTimeBetween = createdDateTimeBetween;
	}

	public void setSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public void setObjUid(String objUid) {
		this.objUid = objUid;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public void setRequestUid(String requestUid) {
		this.requestUid = requestUid;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public void setLevelGe(Integer levelGe) {
		this.levelGe = levelGe;
	}

	public LogFilter withSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
		return this;
	}

	public LogFilter withSubjectType(String subjectType) {
		this.subjectType = subjectType;
		return this;
	}

	public LogFilter withObjUid(String objUid) {
		this.objUid = objUid;
		return this;
	}

	public LogFilter withObjType(String objType) {
		this.objType = objType;
		return this;
	}

	public LogFilter withRequestUid(String requestUid) {
		this.requestUid = requestUid;
		return this;
	}

	public LogFilter withReporter(String reporter) {
		this.reporter = reporter;
		return this;
	}

	public LogFilter withLevelGe(Integer levelGe) {
		this.levelGe = levelGe;
		return this;
	}

	public LogFilter withCreatedDateTimeBetween(LongBetween createdDateTimeBetween) {
		this.createdDateTimeBetween = createdDateTimeBetween;
		return this;
	}
}