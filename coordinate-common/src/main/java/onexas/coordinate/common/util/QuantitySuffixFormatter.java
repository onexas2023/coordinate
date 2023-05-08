package onexas.coordinate.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import onexas.coordinate.common.model.Quantity;

/**
 * code from
 * /io/kubernetes/client-java-api/6.0.1/client-java-api-6.0.1-sources.jar
 * 
 * @author Dennis Chen
 *
 */
public class QuantitySuffixFormatter {

	private static final Map<String, QuantityBaseExponent> suffixToBinary = new HashMap<String, QuantityBaseExponent>();

	static {
		suffixToBinary.put("", new QuantityBaseExponent(2, 0, Quantity.Format.BINARY_SI));
		suffixToBinary.put("Ki", new QuantityBaseExponent(2, 10, Quantity.Format.BINARY_SI));
		suffixToBinary.put("Mi", new QuantityBaseExponent(2, 20, Quantity.Format.BINARY_SI));
		suffixToBinary.put("Gi", new QuantityBaseExponent(2, 30, Quantity.Format.BINARY_SI));
		suffixToBinary.put("Ti", new QuantityBaseExponent(2, 40, Quantity.Format.BINARY_SI));
		suffixToBinary.put("Pi", new QuantityBaseExponent(2, 50, Quantity.Format.BINARY_SI));
		suffixToBinary.put("Ei", new QuantityBaseExponent(2, 60, Quantity.Format.BINARY_SI));

	};

	private static final Map<String, QuantityBaseExponent> suffixToDecimal = new HashMap<String, QuantityBaseExponent>();

	static {
		suffixToDecimal.put("n", new QuantityBaseExponent(10, -9, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("u", new QuantityBaseExponent(10, -6, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("m", new QuantityBaseExponent(10, -3, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("", new QuantityBaseExponent(10, 0, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("k", new QuantityBaseExponent(10, 3, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("M", new QuantityBaseExponent(10, 6, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("G", new QuantityBaseExponent(10, 9, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("T", new QuantityBaseExponent(10, 12, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("P", new QuantityBaseExponent(10, 15, Quantity.Format.DECIMAL_SI));
		suffixToDecimal.put("E", new QuantityBaseExponent(10, 18, Quantity.Format.DECIMAL_SI));

	};

	private static final Map<QuantityBaseExponent, String> decimalToSuffix = new HashMap<QuantityBaseExponent, String>();
	static {
		for (Entry<String, QuantityBaseExponent> entry : suffixToDecimal.entrySet()) {
			decimalToSuffix.put(entry.getValue(), entry.getKey());
		}

	};

	private static final Map<QuantityBaseExponent, String> binaryToSuffix = new HashMap<QuantityBaseExponent, String>();
	static {
		for (Entry<String, QuantityBaseExponent> entry : suffixToBinary.entrySet()) {
			binaryToSuffix.put(entry.getValue(), entry.getKey());
		}
	}

	public QuantityBaseExponent parse(final String suffix) {
		final QuantityBaseExponent decimalSuffix = suffixToDecimal.get(suffix);
		if (decimalSuffix != null) {
			return decimalSuffix;
		}

		final QuantityBaseExponent binarySuffix = suffixToBinary.get(suffix);
		if (binarySuffix != null) {
			return binarySuffix;
		}

		if (suffix.length() > 0 && (suffix.charAt(0) == 'E' || suffix.charAt(0) == 'e')) {
			return extractDecimalExponent(suffix);
		}

		throw new QuantityFormatException("Could not parse suffix "+suffix);
	}

	private QuantityBaseExponent extractDecimalExponent(String suffix) {
		try {
			final int exponent = Integer.parseInt(suffix.substring(1));
			return new QuantityBaseExponent(10, exponent, Quantity.Format.DECIMAL_EXPONENT);
		} catch (final NumberFormatException e) {
			throw new QuantityFormatException("Can't parse decimal exponent from " + suffix.substring(1));
		}
	}

	public String format(final Quantity.Format format, final int exponent) {
		switch (format) {
		case DECIMAL_SI:
			return getDecimalSiSuffix(exponent);
		case BINARY_SI:
			return getBinarySiSuffix(exponent);
		case DECIMAL_EXPONENT:
			return exponent == 0 ? "" : "e" + exponent;
		default:
			throw new IllegalStateException("Can't format " + format + " with exponent " + exponent);
		}
	}

	private String getBinarySiSuffix(int exponent) {
		final String suffix = binaryToSuffix.get(new QuantityBaseExponent(2, exponent, Quantity.Format.BINARY_SI));
		if (suffix == null) {
			throw new IllegalArgumentException("No suffix for exponent" + exponent);
		}
		return suffix;
	}

	private String getDecimalSiSuffix(int exponent) {
		final String suffix = decimalToSuffix.get(new QuantityBaseExponent(10, exponent, Quantity.Format.DECIMAL_SI));
		if (suffix == null) {
			throw new IllegalArgumentException("No suffix for exponent" + exponent);
		}
		return suffix;
	}

}
