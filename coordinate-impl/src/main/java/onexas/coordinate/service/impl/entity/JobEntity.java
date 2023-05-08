package onexas.coordinate.service.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import onexas.coordinate.model.Job;
import onexas.coordinate.model.JobState;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooJob")
@Table(name = "COO_JOB")
public class JobEntity extends Job {

	private static final long serialVersionUID = 1L;

	public JobEntity() {
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	public Long getId() {
		return super.getId();
	}

	@Override
	@Column
	@NotNull
	public Long getCreatedDateTime() {
		return super.getCreatedDateTime();
	}

	@Override
	@Column(length = 128)
	@Enumerated(EnumType.STRING)
	@NotNull
	public JobState getState() {
		return super.getState();
	}

	@Override
	@Column
	public Long getFinishedDateTime() {
		return super.getFinishedDateTime();
	}

	@Override
	@Column
	public Long getStartedDateTime() {
		return super.getStartedDateTime();
	}

	@Override
	@Column
	public Boolean getError() {
		return super.getError();
	}

	@Override
	@Column
	@Lob
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	@Column
	@Size(max = 128)
	public String getNode() {
		return super.getNode();
	}

	@Override
	@Column
	@Lob
	public String getResultJson() {
		return super.getResultJson();
	}

	@Override
	@Size(max = 128)
	public String getRequestUid() {
		return super.getRequestUid();
	}

	@Override
	@Size(max = 256)
	public String getSubject() {
		return super.getSubject();
	}
	
	@Override
	@Size(max = 128)
	public String getQueryUid() {
		return super.getQueryUid();
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
		if (!(obj instanceof JobEntity))
			return false;
		JobEntity other = (JobEntity) obj;
		if (id == null) {
			return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}