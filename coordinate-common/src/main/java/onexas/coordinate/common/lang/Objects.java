package onexas.coordinate.common.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Objects {
	
	/**
	 * a tricky way to identify a jvm_id by class loader level uid (assume the class is not reload again)
	 */
	public static final String JVM_ID = Strings.randomUid();

	@SafeVarargs
	public static <T> T firstNonNull(T... objs) {
		for (T o : objs) {
			if (o != null) {
				return o;
			}
		}
		return null;
	}

	public static Integer coerceToInteger(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Integer) {
			return (Integer) obj;
		} else if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else if (obj instanceof String) {
			try {
				return Integer.parseInt((String) obj);
			} catch (Exception x) {
			}
		} else if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? 1 : 0;
		}
		return null;
	}

	public static Long coerceToLong(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Long) {
			return (Long) obj;
		} else if (obj instanceof Number) {
			return ((Number) obj).longValue();
		} else if (obj instanceof String) {
			try {
				return Long.parseLong((String) obj);
			} catch (Exception x) {
			}
		} else if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? 1L : 0L;
		}
		return null;
	}

	public static Float coerceToFloat(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Float) {
			return (Float) obj;
		} else if (obj instanceof Number) {
			return ((Number) obj).floatValue();
		} else if (obj instanceof String) {
			try {
				return Float.parseFloat((String) obj);
			} catch (Exception x) {
			}
		} else if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? 1.0F : 0.0F;
		}
		return null;
	}

	public static Double coerceToDouble(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Double) {
			return (Double) obj;
		} else if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		} else if (obj instanceof String) {
			try {
				return Double.parseDouble((String) obj);
			} catch (Exception x) {
			}
		} else if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? 1D : 0D;
		}
		return null;
	}

	public static Boolean coerceToBoolean(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else if (obj instanceof Number) {
			return ((Number) obj).doubleValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
		} else if (obj instanceof String) {
			try {
				return Boolean.parseBoolean((String) obj);
			} catch (Exception x) {
			}
		}
		return null;
	}

	public static BigDecimal coerceToBigDecimal(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		} else if (obj instanceof BigInteger) {
			return new BigDecimal((BigInteger) obj);
		} else if (obj instanceof Integer) {
			return new BigDecimal((Integer) obj);
		} else if (obj instanceof Long) {
			return new BigDecimal((Long) obj);
		} else if (obj instanceof Float) {
			return new BigDecimal((Float) obj);
		} else if (obj instanceof Double) {
			return new BigDecimal((Double) obj);
		} else if (obj instanceof String) {
			try {
				return new BigDecimal((String) obj);
			} catch (Exception x) {
			}
		}
		return null;
	}

	public static BigInteger coerceToBigInteger(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof BigInteger) {
			return (BigInteger) obj;
		} else if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).toBigInteger();
		} else if (obj instanceof Number) {
			return BigInteger.valueOf(((Number) obj).longValue());
		} else if (obj instanceof String) {
			try {
				return new BigInteger((String) obj);
			} catch (Exception x) {
			}
		}
		return null;
	}

	public static final boolean equals(Object a, Object b) {
		if (a == b || (a != null && b != null && a.equals(b)))
			return true;
		if ((a instanceof BigDecimal) && (b instanceof BigDecimal))
			return ((BigDecimal) a).compareTo((BigDecimal) b) == 0;

		if (a == null || !a.getClass().isArray())
			return false;

		if ((a instanceof Object[]) && (b instanceof Object[])) {
			final Object[] as = (Object[]) a;
			final Object[] bs = (Object[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (!equals(as[j], bs[j])) // recursive
					return false;
			return true;
		}
		if ((a instanceof int[]) && (b instanceof int[])) {
			final int[] as = (int[]) a;
			final int[] bs = (int[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof byte[]) && (b instanceof byte[])) {
			final byte[] as = (byte[]) a;
			final byte[] bs = (byte[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof char[]) && (b instanceof char[])) {
			final char[] as = (char[]) a;
			final char[] bs = (char[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof long[]) && (b instanceof long[])) {
			final long[] as = (long[]) a;
			final long[] bs = (long[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof short[]) && (b instanceof short[])) {
			final short[] as = (short[]) a;
			final short[] bs = (short[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		if ((a instanceof double[]) && (b instanceof double[])) {
			final double[] as = (double[]) a;
			final double[] bs = (double[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (Double.compare(as[j], bs[j]) != 0)
					return false;
			return true;
		}
		if ((a instanceof float[]) && (b instanceof float[])) {
			final float[] as = (float[]) a;
			final float[] bs = (float[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (Float.compare(as[j], bs[j]) != 0)
					return false;
			return true;
		}
		if ((a instanceof boolean[]) && (b instanceof boolean[])) {
			final boolean[] as = (boolean[]) a;
			final boolean[] bs = (boolean[]) b;
			if (as.length != bs.length)
				return false;
			for (int j = as.length; --j >= 0;)
				if (as[j] != bs[j])
					return false;
			return true;
		}
		return false;
	}

	public static boolean equalsMapValue(Map<String, Object> m1, Map<String, Object> m2, String key) {
		if (m1 != null && m2 != null) {
			if (m1.containsKey(key) && m2.containsKey(key) && equals(m1.get(key), m2.get(key))) {
				return true;
			}
		} else if (m1 == null && m2 == null) {
			return true;
		}
		return false;
	}

}
