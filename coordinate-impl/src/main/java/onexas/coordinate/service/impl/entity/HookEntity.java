package onexas.coordinate.service.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import onexas.coordinate.model.Hook;

/**
 * 
 * @author Dennis Chen
 *
 */
/**
 * @author Dennis Chen
 *
 */
@Entity(name = "cooHook")
@Table(name = "COO_HOOK")
public class HookEntity extends Hook {

	private static final long serialVersionUID = 1L;

	Long updatedDateTime;

	Integer version;

	public HookEntity() {
	}

	@Override
	@Id
	@Column
	@Size(max = 128)
	public String getUid() {
		return super.getUid();
	}
	
	@Override
	@Size(max = 128)
	@Column(updatable = false)
	@NotNull
	@Pattern(regexp = "[a-z]([-a-z0-9]*[a-z0-9])?", message = "zone format invalidate")
	public String getZone() {
		return super.getZone();
	}

	@Override
	@Size(max = 128)
	@Column(updatable = false)
	public String getSubjectUid() {
		return super.getSubjectUid();
	}

	@Override
	@Size(max = 128)
	@Column(updatable = false)
	public String getSubjectType() {
		return super.getSubjectType();
	}

	@Override
	@Size(max = 128)
	@Column(updatable = false)
	public String getOwnerUid() {
		return super.getOwnerUid();
	}

	@Override
	@Size(max = 128)
	@Column(updatable = false)
	public String getOwnerType() {
		return super.getOwnerType();
	}

	@Override
	@Lob
	@Column(updatable = false)
	public String getData() {
		return super.getData();
	}

	@Column(updatable = false)
	public Integer getTriggerLife() {
		return super.getTriggerLife();
	}

	@Column
	public Integer getTrigger() {
		return super.getTrigger();
	}

	@Override
	@Size(max = 256)
	@Column
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
		if (!(obj instanceof HookEntity))
			return false;
		HookEntity other = (HookEntity) obj;
		if (uid == null) {
			return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
}