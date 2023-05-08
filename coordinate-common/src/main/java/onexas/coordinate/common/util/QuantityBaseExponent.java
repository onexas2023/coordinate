package onexas.coordinate.common.util;

import onexas.coordinate.common.model.Quantity;

/**
 * code from
 * /io/kubernetes/client-java-api/6.0.1/client-java-api-6.0.1-sources.jar
 * 
 * @author Dennis Chen
 */
public class QuantityBaseExponent {

	private final int base;
	private final int exponent;
	private final Quantity.Format format;

	public QuantityBaseExponent(final int base, final int exponent, final Quantity.Format format) {
		this.base = base;
		this.exponent = exponent;
		this.format = format;
	}

	public int getBase() {
		return base;
	}

	public int getExponent() {
		return exponent;
	}

	public Quantity.Format getFormat() {
		return format;
	}

	@Override
	public String toString() {
		return "BaseExponent{" + "base=" + base + ", exponent=" + exponent + ", format=" + format + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		QuantityBaseExponent that = (QuantityBaseExponent) o;

		return base == that.base && exponent == that.exponent && format == that.format;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
