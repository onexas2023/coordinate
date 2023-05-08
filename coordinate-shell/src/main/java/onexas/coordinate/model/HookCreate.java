package onexas.coordinate.model;

import java.io.Serializable;

/**
 * 
 * @author Dennis Chen
 *
 */

public class HookCreate implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String description;
	
	protected String zone;

	protected String subjectUid;

	protected String subjectType;

	protected String ownerUid;

	protected String ownerType;

	protected String data;
	
	protected Integer triggerLife;

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

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
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
	
	public HookCreate withTriggerLife(Integer triggerLife) {
		this.triggerLife = triggerLife;
		return this;
	}

	public HookCreate withZone(String zone) {
		this.zone = zone;
		return this;
	}
	
	public HookCreate withDescription(String description) {
		this.description = description;
		return this;
	}

	public HookCreate withSubjectUid(String subjectUid) {
		this.subjectUid = subjectUid;
		return this;
	}

	public HookCreate withSubjectType(String subjectType) {
		this.subjectType = subjectType;
		return this;
	}

	public HookCreate withOwnerUid(String ownerUid) {
		this.ownerUid = ownerUid;
		return this;
	}

	public HookCreate withOwnerType(String ownerType) {
		this.ownerType = ownerType;
		return this;
	}

	public HookCreate withData(String data) {
		this.data = data;
		return this;
	}
}