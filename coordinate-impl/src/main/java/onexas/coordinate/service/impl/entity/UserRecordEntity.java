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
@Entity(name = "cooUserRecord")
@Table(name = "COO_USER_REC")
public class UserRecordEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	String uid;

	String account;

	String aliasUid;

	String domain;

	Long createdDateTime;

	Long deletedDateTime;
	
	String displayName;

	Integer version;

	public UserRecordEntity() {
	}

	public void setUid(String uid) {
		this.uid = uid;
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
	public String getAccount() {
		return account;
	}

	@Column
	@NotNull
	@Size(max = 128)
	public String getDomain() {
		return domain;
	}

	@Column
	@NotNull
	@Size(max = 128)
	public String getAliasUid() {
		return aliasUid;
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

	@Column
	@NotNull
	@Size(max = 128)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setDeletedDateTime(Long deletedDateTime) {
		this.deletedDateTime = deletedDateTime;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setAliasUid(String aliasUid) {
		this.aliasUid = aliasUid;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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
		if (!(obj instanceof UserRecordEntity))
			return false;
		UserRecordEntity other = (UserRecordEntity) obj;
		if (uid == null) {
			return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

}