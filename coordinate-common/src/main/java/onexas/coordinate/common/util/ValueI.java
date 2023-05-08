package onexas.coordinate.common.util;

/**
 * to help hold value in the change final local variable case
 * 
 * @author Dennis Chen
 * 
 * @param <V1>
 */
public class ValueI<V1 extends Object> {

	V1 value1;

	public ValueI() {}
	public ValueI(V1 value1) {
		this.value1 = value1;
	}

	public V1 getValue1() {
		return value1;
	}

	public void setValue1(V1 value1) {
		this.value1 = value1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value1 == null) ? 0 : value1.hashCode());
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
		@SuppressWarnings("rawtypes")
		ValueI other = (ValueI) obj;
		if (value1 == null) {
			if (other.value1 != null)
				return false;
		} else if (!value1.equals(other.value1))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ValueI [value1=" + value1 + "]";
	}
	
	
}
