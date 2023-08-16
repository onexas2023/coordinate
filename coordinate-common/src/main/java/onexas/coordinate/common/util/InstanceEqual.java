package onexas.coordinate.common.util;

/**
 * a util class compares instance directly in equals method and use identityHasCode in hashCode.
 * this util is for comparing when a class implements equals,hashCode but you want to compare instance directly (e.g. in Set or in Map-Key)
 * 
 * @author Dennis Chen
 *
 */
public class InstanceEqual<T> {

	T instance;

	public InstanceEqual(T instance) {
		this.instance = instance;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(instance);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceEqual<?> other = (InstanceEqual<?>) obj;
		return instance == other.instance;
	}
}
