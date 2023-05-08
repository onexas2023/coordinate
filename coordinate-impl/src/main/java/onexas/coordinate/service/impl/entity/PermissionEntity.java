package onexas.coordinate.service.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import onexas.coordinate.model.Permission;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooPermission")
@Table(name = "COO_PERMISSION",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "principal", "target", "action" }) })
public class PermissionEntity extends Permission {
	private static final long serialVersionUID = 1L;

	Long createdDateTime;

	public PermissionEntity() {
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
	@Size(max = 128)
	@NotNull
	public String getPrincipal() {
		return super.getPrincipal();
	}

	@Override
	@Column
	@Size(max = 128)
	public String getRemark() {
		return super.getRemark();
	}

	@Override
	@Column
	@Size(max = 128)
	@NotNull
	public String getTarget() {
		return super.getTarget();
	}

	@Override
	@Column
	@Size(max = 128)
	@NotNull
	public String getAction() {
		return super.getAction();
	}

	@Override
	public void setPrincipal(String principal) {
		super.setPrincipal(principal);
	}

	@Override
	public void setRemark(String mark) {
		super.setRemark(mark);
	}

	@Override
	public void setTarget(String target) {
		super.setTarget(target);
	}

	@Override
	public void setAction(String action) {
		super.setAction(action);
	}

	@Column(updatable = false)
	@NotNull
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	@PrePersist
	protected void onCreate() {
		createdDateTime = System.currentTimeMillis();
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
		if (!(obj instanceof PermissionEntity))
			return false;
		PermissionEntity other = (PermissionEntity) obj;
		if (uid == null) {
			return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

}