package onexas.coordinate.service.impl.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooInfoCache")
@Table(name = "COO_INFO_CACHE")
public class InfoCacheEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	String token;

	String info;

	Long createdDateTime;

	Long timeout;

	public InfoCacheEntity() {
	}

	@PrePersist
	protected void onCreate() {
		createdDateTime = System.currentTimeMillis();
	}

	@Id
	@Column
	@Size(max = 128)
	public String getToken() {
		return token;
	}

	@Lob
	public String getInfo() {
		return info;
	}

	@NotNull
	public Long getCreatedDateTime() {
		return createdDateTime;
	}

	@NotNull
	public Long getTimeout() {
		return timeout;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	protected void setCreatedDateTime(Long createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InfoCacheEntity))
			return false;
		InfoCacheEntity other = (InfoCacheEntity) obj;
		if (token == null) {
			return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

}