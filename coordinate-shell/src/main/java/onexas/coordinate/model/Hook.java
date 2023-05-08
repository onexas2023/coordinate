package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Hook implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String uid;

	protected String zone;

	protected Long createdDateTime;

	protected String description;

	protected String subjectUid;

	protected String subjectType;

	protected String ownerUid;

	protected String ownerType;

	protected String data;

	protected Integer triggerLife;

	protected Integer trigger;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getTriggerLife() {
		return triggerLife;
	}

	public void setTriggerLife(Integer triggerLife) {
		this.triggerLife = triggerLife;
	}

	public Integer getTrigger() {
		return trigger;
	}

	public void setTrigger(Integer trigger) {
		this.trigger = trigger;
	}

}