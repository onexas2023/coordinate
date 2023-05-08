package onexas.coordinate.service.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import onexas.coordinate.model.Log;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooLog")
@Table(name = "COO_LOG")
public class LogEntity extends Log {

	private static final long serialVersionUID = 1L;

	public LogEntity() {
	}

	@Override
	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO, generator="COO_LOG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	public Long getId() {
		return super.getId();
	}

	@NotNull
	@Override
	public Long getCreatedDateTime() {
		return super.getCreatedDateTime();
	}

	@Override
	@Size(max = 128)
	public String getSubjectUid() {
		return super.getSubjectUid();
	}

	@Override
	@Size(max = 256)
	public String getSubjectType() {
		return super.getSubjectType();
	}

	@Override
	@Size(max = 128)
	public String getObjUid() {
		return super.getObjUid();
	}

	@Override
	@Size(max = 256)
	public String getObjType() {
		return super.getObjType();
	}

	@Override
	@Size(max = 256)
	public String getReporter() {
		return super.getReporter();
	}

	@Override
	@Lob
	public String getContent() {
		return super.getContent();
	}

	@Override
	@NotNull
	public Integer getLevel() {
		return super.getLevel();
	}

	@Override
	@Size(max = 128)
	public String getRequestUid() {
		return super.getRequestUid();
	}

	@Override
	@Size(max = 128)
	public String getUserAccount() {
		return super.getUserAccount();
	}

	@Override
	@Size(max = 128)
	public String getUserDomain() {
		return super.getUserDomain();
	}

	@PrePersist
	protected void onCreate() {
		createdDateTime = System.currentTimeMillis();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LogEntity))
			return false;
		LogEntity other = (LogEntity) obj;
		if (id == null) {
			return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}