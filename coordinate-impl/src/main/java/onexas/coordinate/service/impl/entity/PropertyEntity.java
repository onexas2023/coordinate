package onexas.coordinate.service.impl.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import onexas.coordinate.model.Property;

/**
 * 
 * @author Dennis Chen
 *
 */
@Entity(name = "cooProperty")
@Table(name = "COO_PROPERTY")
@IdClass(PropertyEntity.PK.class)
public class PropertyEntity extends Property {

	private static final long serialVersionUID = 1L;

	protected String objUid;

	protected String category;

	@Id
	@Column(length = 128)
	@Size(max = 128)
	public String getObjUid() {
		return objUid;
	}

	public void setObjUid(String objUid) {
		this.objUid = objUid;
	}

	@Override
	@Id
	@Column(length = 128)
	@Size(max = 128)
	public String getName() {
		return super.getName();
	}

	@Override
	@Lob
	@Column
	public String getValue() {
		return super.getValue();
	}

	@Column
	@Size(max = 128)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public static class PK implements Serializable {
		private static final long serialVersionUID = 1L;
		String objUid;
		String name;

		public PK() {
		}

		public PK(String objUid, String name) {
			this.objUid = objUid;
			this.name = name;
		}

		public String getObjUid() {
			return objUid;
		}

		public String getName() {
			return name;
		}

		public void setObjUid(String objUid) {
			this.objUid = objUid;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((objUid == null) ? 0 : objUid.hashCode());
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
			if (name == null) {
				return false;
			} else if (!name.equals(other.name))
				return false;
			if (objUid == null) {
				return false;
			} else if (!objUid.equals(other.objUid))
				return false;
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((objUid == null) ? 0 : objUid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PropertyEntity))
			return false;
		PropertyEntity other = (PropertyEntity) obj;
		if (name == null) {
			return false;
		} else if (!name.equals(other.name))
			return false;
		if (objUid == null) {
			return false;
		} else if (!objUid.equals(other.objUid))
			return false;
		return true;
	}

}