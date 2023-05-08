package onexas.coordinate.model;

import java.io.Serializable;

import onexas.coordinate.common.model.PageFilter;

/**
 * 
 * @author Dennis Chen
 *
 */

public class HookFilter extends PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String uid;
	
	protected String zone;
	
	protected String subjectUid;

	protected String subjectType;

	protected String ownerUid;

	protected String ownerType;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSubjectUid() {
		return subjectUid;
	}

	public void setSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
	}

	public String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getOwnerUid() {
		return ownerUid;
	}

	public void setOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public HookFilter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public HookFilter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

	public HookFilter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public HookFilter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public HookFilter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public HookFilter withPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}

	public HookFilter withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public HookFilter withSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
		return this;
	}

	public HookFilter withSubjectType(String subjectType) {
		this.subjectType = subjectType;
		return this;
	}

	public HookFilter withOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
		return this;
	}

	public HookFilter withOwnerType(String ownerType) {
		this.ownerType = ownerType;
		return this;
	}
	
	public HookFilter withZone(String zone) {
		this.zone = zone;
		return this;
	}
}