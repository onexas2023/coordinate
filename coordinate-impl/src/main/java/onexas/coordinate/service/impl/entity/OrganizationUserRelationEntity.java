package onexas.coordinate.service.impl.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import onexas.coordinate.model.OrganizationUserRelationType;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooOrganizationUserRelation")
@Table(name = "COO_ORG_USER_REL")
@IdClass(OrganizationUserRelationEntity.PK.class)
public class OrganizationUserRelationEntity {

	String organizationUid;
	String userUid;
	Long createdDateTime;
	OrganizationUserRelationType type;

	OrganizationEntity organization;
	UserEntity user;

	public OrganizationUserRelationEntity() {
	}

	public OrganizationUserRelationEntity(String organizationUid, String userUid, OrganizationUserRelationType type) {
		this.organizationUid = organizationUid;
		this.userUid = userUid;
		this.type = type;
	}

	@Id
	@Column(length = 128, updatable = false)
	@Size(max = 128)
	public String getOrganizationUid() {
		return organizationUid;
	}

	@Id
	@Column(length = 128, updatable = false)
	@Size(max = 128)
	public String getUserUid() {
		return userUid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(insertable = false, updatable = false, name = "organizationUid")
	@JsonIgnore
	public OrganizationEntity getOrganization() {
		return organization;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(insertable = false, updatable = false, name = "userUid")
	@JsonIgnore
	public UserEntity getUser() {
		return user;
	}

	@Column(updatable = false)
	@NotNull
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@Column(length = 128)
	@Enumerated(EnumType.STRING)
	@NotNull
	public OrganizationUserRelationType getType() {
		return type;
	}

	public void setType(OrganizationUserRelationType type) {
		this.type = type;
	}

	protected void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setOrganizationUid(String organizationUid) {
		this.organizationUid = organizationUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	protected void setOrganization(OrganizationEntity organization) {
		this.organization = organization;
	}

	protected void setUser(UserEntity user) {
		this.user = user;
	}

	@PrePersist
	protected void onCreate() {
		createdDateTime = System.currentTimeMillis();
	}

	public static class PK implements Serializable {
		private static final long serialVersionUID = 1L;
		String organizationUid;
		String userUid;

		public PK() {
		}

		public PK(String organizationUid, String userUid) {
			this.organizationUid = organizationUid;
			this.userUid = userUid;
		}

		public String getOrganizationUid() {
			return organizationUid;
		}

		public String getUserUid() {
			return userUid;
		}

		public void setOrganizationUid(String organizationUid) {
			this.organizationUid = organizationUid;
		}

		public void setUserUid(String userUid) {
			this.userUid = userUid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((organizationUid == null) ? 0 : organizationUid.hashCode());
			result = prime * result + ((userUid == null) ? 0 : userUid.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PK other = (PK) obj;
			if (organizationUid == null) {
				return false;
			} else if (!organizationUid.equals(other.organizationUid))
				return false;
			if (userUid == null) {
				return false;
			} else if (!userUid.equals(other.userUid))
				return false;
			return true;
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((organizationUid == null) ? 0 : organizationUid.hashCode());
		result = prime * result + ((userUid == null) ? 0 : userUid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof OrganizationUserRelationEntity))
			return false;
		OrganizationUserRelationEntity other = (OrganizationUserRelationEntity) obj;
		if (organizationUid == null) {
			if (other.organizationUid != null)
				return false;
		} else if (!organizationUid.equals(other.organizationUid))
			return false;
		if (userUid == null) {
			return false;
		} else if (!userUid.equals(other.userUid))
			return false;
		return false;
	}

}