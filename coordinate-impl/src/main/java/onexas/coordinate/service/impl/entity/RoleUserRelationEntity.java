package onexas.coordinate.service.impl.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooRoleUserRelation")
@Table(name = "COO_ROLE_USER_REL")
@IdClass(RoleUserRelationEntity.PK.class)
public class RoleUserRelationEntity {

	String roleUid;
	String userUid;
	Long createdDateTime;

	RoleEntity role;
	UserEntity user;

	public RoleUserRelationEntity() {
	}

	public RoleUserRelationEntity(String roleUid, String userUid) {
		this.roleUid = roleUid;
		this.userUid = userUid;
	}

	@Id
	@Column(length = 128, updatable = false)
	@Size(max = 128)
	public String getRoleUid() {
		return roleUid;
	}

	@Id
	@Column(length = 128, updatable = false)
	@Size(max = 128)
	public String getUserUid() {
		return userUid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(insertable = false, updatable = false, name = "roleUid")
	@JsonIgnore
	public RoleEntity getRole() {
		return role;
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

	protected void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setRoleUid(String roleUid) {
		this.roleUid = roleUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	protected void setRole(RoleEntity role) {
		this.role = role;
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
		String roleUid;
		String userUid;

		public PK() {
		}

		public PK(String roleUid, String userUid) {
			this.roleUid = roleUid;
			this.userUid = userUid;
		}

		public String getRoleUid() {
			return roleUid;
		}

		public String getUserUid() {
			return userUid;
		}

		public void setRoleUid(String roleUid) {
			this.roleUid = roleUid;
		}

		public void setUserUid(String userUid) {
			this.userUid = userUid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((roleUid == null) ? 0 : roleUid.hashCode());
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
			if (roleUid == null) {
				return false;
			} else if (!roleUid.equals(other.roleUid))
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
		result = prime * result + ((roleUid == null) ? 0 : roleUid.hashCode());
		result = prime * result + ((userUid == null) ? 0 : userUid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RoleUserRelationEntity))
			return false;
		RoleUserRelationEntity other = (RoleUserRelationEntity) obj;
		if (roleUid == null) {
			return false;
		} else if (!roleUid.equals(other.roleUid))
			return false;
		if (userUid == null) {
			return false;
		} else if (!userUid.equals(other.userUid))
			return false;
		return true;
	}

}