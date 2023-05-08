package onexas.coordinate.common.util;
/**
 * 
 * @author Dennis Chen
 *
 */
public class LabelObject<T> {
	final T obj;
	final String label;

	public LabelObject(T obj, String label) {
		super();
		this.obj = obj;
		this.label = label;
	}
	public T getObject() {
		return obj;
	}
	public String getLabel() {
		return label;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		return result;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelObject other = (LabelObject) obj;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		return true;
	}
}