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

import onexas.coordinate.model.Role;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooRole")
@Table(name = "COO_ROLE", uniqueConstraints = { @UniqueConstraint(columnNames = { "code" }) })
public class RoleEntity extends Role {

	private static final long serialVersionUID = 1L;

	Long updatedDateTime;

	Integer version;

	public RoleEntity() {
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
	@NotNull
	@Size(min=1, max = 128)
	public String getName() {
		return super.getName();
	}

	@Override
	@Column(updatable = false)
	@NotNull
	@Size(min=1, max = 128)
	@Pattern(regexp = "[a-z]([-a-z0-9]*[a-z0-9])?", message = "code format invalidate")
	public String getCode() {
		return super.getCode();
	}

	@Override
	@Column
	@Size(max = 256)
	public String getDescription() {
		return super.getDescription();
	}

	@Column
	@NotNull
	public Long getCreatedDateTime() {
		return super.getCreatedDateTime();
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
		if (!(obj instanceof RoleEntity))
			return false;
		RoleEntity other = (RoleEntity) obj;
		if (uid == null) {
			return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

}