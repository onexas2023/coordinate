package onexas.coordinate.service.impl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import onexas.coordinate.model.AuthenticationToken;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooAuthenticationToken")
@Table(name = "COO_AUTH_TOKEN")
public class AuthenticationTokenEntity extends AuthenticationToken {
	private static final long serialVersionUID = 1L;

	Long id;

	Long createdDateTime;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	public Long getId() {
		return id;
	}

	@Column(updatable = false)
	@NotNull
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@Override
	@Column(updatable = false)
	@NotNull
	@Size(max = 512)
	public String getToken() {
		return super.getToken();
	}

	@Override
	@Column(updatable = false)
	@NotNull
	@Size(max = 128)
	public String getAccount() {
		return super.getAccount();
	}

	@Override
	@Column(updatable = false)
	@Size(max = 64)
	public String getClientIp() {
		return super.getClientIp();
	}
	
	@Override
	@Column(updatable = false)
	@Size(min=1, max = 128)
	public String getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	@NotNull
	public Long getTimeoutAt() {
		return super.getTimeoutAt();
	}
	
	@Override
	@Column(updatable = false)
	@NotNull
	@Size(max = 128)
	public String getAliasUid() {
		return super.getAliasUid();
	}

	@Override
	@NotNull
	@Column(updatable = false)
	@Size(max = 128)
	public String getDomain() {
		return super.getDomain();
	}

	protected void setId(Long id) {
		this.id = id;
	}

	protected void setCreatedDateTime(Long createdDateTime) {
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AuthenticationTokenEntity))
			return false;
		AuthenticationTokenEntity other = (AuthenticationTokenEntity) obj;
		if (id == null) {
			return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}