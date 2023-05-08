package onexas.coordinate.service.impl.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooOrganizationRecord")
@Table(name = "COO_ORG_REC")
public class OrganizationRecordEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	String uid;

	String code;

	String name;

	Long createdDateTime;

	Long deletedDateTime;

	Integer version;

	public OrganizationRecordEntity() {
	}

	@Id
	@Column
	@Size(max = 128)
	public String getUid() {
		return uid;
	}

	@Column
	@NotNull
	@Size(max = 128)
	public String getName() {
		return name;
	}

	@Column
	@NotNull
	@Size(max = 128)
	public String getCode() {
		return code;
	}

	@Column
	@NotNull
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@Column
	public Long getDeletedDateTime() {
		return deletedDateTime;
	}

	@Column(name = "_ver")
	@NotNull
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setDeletedDateTime(Long deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof OrganizationRecordEntity))
			return false;
		OrganizationRecordEntity other = (OrganizationRecordEntity) obj;
		if (uid == null) {
			return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

}