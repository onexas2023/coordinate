package onexas.coordinate.service.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import onexas.coordinate.model.User;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooUser")
@Table(name = "COO_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "account", "domain" }),
		@UniqueConstraint(columnNames = { "aliasUid" }) })
public class UserEntity extends User {

	private static final long serialVersionUID = 1L;

	String password;

	String domainUserIdentity;

	Long createdDateTime;

	Long updatedDateTime;

	Integer version;

	public UserEntity() {
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	@Id
	@Column
	@Size(max = 128)
	public String getUid() {
		return super.getUid();
	}

	@Override
	@Column
	@Size(max = 256)
	public String getEmail() {
		return super.getEmail();
	}

	@Override
	@Column(updatable = false)
	@NotNull
	@Size(min=2, max = 128)
	// e.g de-nn_is@gm@i_l.co-m
	@Pattern(regexp = "(^[a-z])[a-z0-9@\\_\\-\\.]*", message = "account format invalidate")
	public String getAccount() {
		return super.getAccount();
	}

	@Override
	@Column
	@NotNull
	public Boolean getDisabled() {
		return super.getDisabled();
	}

	@Override
	@Column(updatable = false)
	@NotNull
	@Size(max = 128)
	public String getDomain() {
		return super.getDomain();
	}

	@Override
	@Column(updatable = false)
	@NotNull
	@Size(max = 128)
	public String getAliasUid() {
		return super.getAliasUid();
	}

	@Column
	@NotNull
	@Size(min=1, max = 128)
	public String getDisplayName() {
		return displayName;
	}

	@NotNull
	@Column
	@Size(max = 256)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(updatable = false)
	@NotNull
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@Column
	public Long getUpdatedDateTime() {
		return updatedDateTime;
	}

	@Column(name = "_ver")
	@NotNull
	@Version
	public Integer getVersion() {
		return version;
	}

	@Column
	@Size(max = 256)
	public String getDomainUserIdentity() {
		return domainUserIdentity;
	}

	public void setDomainUserIdentity(String domainUserIdentity) {
		this.domainUserIdentity = domainUserIdentity;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	protected void setUpdatedDateTime(Long updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

	@PrePersist
	protected void onCreate() {
		createdDateTime = System.currentTimeMillis();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDateTime = System.currentTimeMillis();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserEntity))
			return false;
		UserEntity other = (UserEntity) obj;
		if (uid == null) {
			return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

}