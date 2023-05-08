package onexas.coordinate.common.lang;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import onexas.coordinate.common.util.Base58UUID;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Strings {

	public static final Charset UTF8 = Charset.forName("UTF8");

	public static String toUid(long val) {
		return new UUID(0, val).toString();
	}

	public static String randomUid() {
		return Base58UUID.encode(UUID.randomUUID());
	}

	public static String randomUid(int loop) {
		if (loop <= 0) {
			throw new IllegalStateException("loop must > 0, but is " + loop);
		}
		StringBuilder sb = new StringBuilder();
		do {
			sb.append(randomUid());
			--loop;
			if (loop > 0) {
				sb.append("-");
			}
		} while (loop > 0);
		return sb.toString();

	}

	static private final char[] pwdSeed = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9' };
	static private final char[] nameSeed = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	static private final char[] numberSeed = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	static private final Random random = new Random();

	public static String randomPassword(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(pwdSeed[random.nextInt(pwdSeed.length)]);
		}
		return sb.toString();
	}

	public static String randomName(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(nameSeed[random.nextInt(nameSeed.length)]);
		}
		return sb.toString();
	}

	public static String randomNumber(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(numberSeed[random.nextInt(numberSeed.length)]);
		}
		return sb.toString();
	}

	public static String trim(String str) {
		return trim(str, null);
	}

	public static String trim(String str, Integer maxLength) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		if (maxLength != null && maxLength >= 0 && str.length() > maxLength) {
			str = str.substring(0, maxLength);
		}
		return str.trim();
	}

	public static String trimToNull(String str) {
		return trimToNull(str, null);
	}

	public static String trimToNull(String str, Integer maxLength) {
		str = str == null ? null : trim(str, maxLength);
		return str == null || str.isEmpty() ? null : str;
	}

	public static String trimToEmpty(String str) {
		return trimToEmpty(str, null);
	}

	public static String trimToEmpty(String str, Integer maxLength) {
		return str == null ? "" : trim(str, maxLength);
	}
	
	public static String trimToEmpty(Object obj) {
		return trimToEmpty(obj, null);
	}

	public static String trimToEmpty(Object obj, Integer maxLength) {
		return obj == null ? "" : trim(obj.toString(), maxLength);
	}

	/**
	 * format the string with args, the arg will replace '{}' in str follow the
	 * order
	 * 
	 * @param str
	 * @param args
	 */
	public static final String format(String str, Object... args) {
		if (args == null || args.length == 0) {
			return str;
		}

		StringBuilder sb = new StringBuilder();
		char[] ca = str.toCharArray();
		int argidx = 0;
		for (int i = 0; i < ca.length; i++) {
			char c = ca[i];
			if (c == '{') {
				if (i < ca.length - 1 && ca[i + 1] == '}' && argidx < args.length) {
					sb.append(args[argidx]);
					i++;
					argidx++;
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static String splitAndGet(String str, String splitRegex, int index, String defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		String[] s = str.split(splitRegex);
		if (index >= s.length) {
			return defaultValue;
		}
		return s[index];
	}

	public static String matchAndGet(String str, String matchRegex, int index, String defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		Pattern patten = Pattern.compile(matchRegex);
		Matcher matcher = patten.matcher(str);
		int c = -1;
		while (matcher.find()) {
			str = matcher.group();
			c++;
			if (c == index) {
				return str;
			}
		}
		return defaultValue;
	}

	public static String matchAndGetInParentheses(String str, int index, String defaultValue) {
		String[] ss = matchParentheses(str, null);
		if (ss == null || ss.length <= index) {
			return defaultValue;
		}
		return ss[index];
	}

	public static String[] matchParentheses(String str, String[] defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		Pattern patten = Pattern.compile("\\([\\w\\W&&[^\\(\\)]]++\\)");
		Matcher matcher = patten.matcher(str);
		List<String> result = new LinkedList<String>();
		while (matcher.find()) {
			str = matcher.group();
			str = str.substring(1, str.length() - 1);
			result.add(str);
		}
		return result.toArray(new String[result.size()]);
	}

	public static String matchAndGetInBrackets(String str, int index, String defaultValue) {
		String[] ss = matchBrackets(str, null);
		if (ss == null || ss.length <= index) {
			return defaultValue;
		}
		return ss[index];
	}

	public static String[] matchBrackets(String str, String[] defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		Pattern patten = Pattern.compile("\\[[\\w\\W&&[^\\[\\]]]+\\]");
		Matcher matcher = patten.matcher(str);
		List<String> result = new LinkedList<String>();
		while (matcher.find()) {
			str = matcher.group();
			str = str.substring(1, str.length() - 1);
			result.add(str);
		}
		return result.toArray(new String[result.size()]);
	}

	public static String matchAndGetInBraces(String str, int index, String defaultValue) {
		String[] ss = matchBraces(str, null);
		if (ss == null || ss.length <= index) {
			return defaultValue;
		}
		return ss[index];
	}

	public static String[] matchBraces(String str, String[] defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		Pattern patten = Pattern.compile("\\{[\\w\\W&&[^\\{\\}]]+\\}");
		Matcher matcher = patten.matcher(str);
		List<String> result = new LinkedList<String>();
		while (matcher.find()) {
			str = matcher.group();
			str = str.substring(1, str.length() - 1);
			result.add(str);
		}
		return result.toArray(new String[result.size()]);
	}

//	public static long toSafeLong(String str){
//		try{
//			return Long.parseLong(str);
//		}catch(Exception x){
//			return 0;
//		}
//	}
//	public static int toSafeInt(String str){
//		try{
//			return Integer.parseInt(str);
//		}catch(Exception x){
//			return 0;
//		}
//	}
//	public static double toSafeDouble(String str){
//		try{
//			return Double.parseDouble(str);
//		}catch(Exception x){
//			return 0;
//		}
//	}
//	public static float toSafeFloat(String str){
//		try{
//			return Float.parseFloat(str);
//		}catch(Exception x){
//			return 0;
//		}
//	}

	public static boolean isBlank(String s) {
		if (s == null) {
			return true;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(String a, String b) {
		if (a == b || (a != null && b != null && a.equals(b)))
			return true;

		return false;
	}

	public static String toHexString(byte b) {
		return Integer.toString((b & 0xff) + 0x100, 16).substring(1);
	}

	public static String toHexString(byte[] b) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}

	public static byte[] toByteArray(String hexString) {
		int size = hexString.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int i = 0; i < size; i = i + 2) {
			int b = Integer.parseInt(hexString.substring(i, i + 2), 16);
			bos.write(b);
		}
		return bos.toByteArray();
	}

	public static String gusessEncoding(byte[] str) {
		// TODO
		return "MS950";
	}

	public static final String[] digitalDegrees = new String[] { "", "K", "M", "G", "T", "P", "E" };

	public static final int DIGITAL_DEGREE_0 = 0;
	public static final int DIGITAL_DEGREE_K = 1;
	public static final int DIGITAL_DEGREE_M = 2;
	public static final int DIGITAL_DEGREE_G = 3;
	public static final int DIGITAL_DEGREE_T = 4;
	public static final int DIGITAL_DEGREE_P = 5;
	public static final int DIGITAL_DEGREE_E = 6;

	public static String getShortDigitalUnitString(Number value) {
		return getShortDigitalUnitString(value, DIGITAL_DEGREE_0, DIGITAL_DEGREE_E, 1024, "i", "#,###.##", "");
	}

	public static String getShortDigitalUnitString(Number value, String unit) {
		return getShortDigitalUnitString(value, DIGITAL_DEGREE_0, DIGITAL_DEGREE_E, 1024, "i", "#,###.##", unit);
	}

	public static String getShortDigitalUnitString(Number value, int startDegree, String unit) {
		return getShortDigitalUnitString(value, startDegree, DIGITAL_DEGREE_E, 1024, "i", "#,###.##", unit);
	}

	public static String getShortDigitalUnitString(Number value, int startDegree, int endDegree, String unit) {
		return getShortDigitalUnitString(value, startDegree, endDegree, 1024, "i", "#,###.##", unit);
	}

	public static String getShortDigitalUnitString(Number value, int startDegree, int endDegree, int radix,
			String radixIndicate, String format, String unit) {
		DecimalFormat shortDigitalFormat = new DecimalFormat(format);
		if (value instanceof BigInteger) {
			return getShortDigitalUnitString(new BigDecimal((BigInteger) value), startDegree, endDegree, radix,
					radixIndicate, shortDigitalFormat, unit);
		} else if (value instanceof BigDecimal) {
			return getShortDigitalUnitString((BigDecimal) value, startDegree, endDegree, radix, radixIndicate,
					shortDigitalFormat, unit);
		}

		double dval = value.doubleValue();
		int degree = startDegree;

		while (dval >= radix && degree < endDegree && degree < digitalDegrees.length - 1) {
			dval = dval / radix;
			degree++;
		}

		StringBuilder str = new StringBuilder().append(shortDigitalFormat.format(dval)).append(" ")
				.append(digitalDegrees[degree]).append(degree > 0 ? radixIndicate : "").append(unit);
		return str.toString();
	}

	private static String getShortDigitalUnitString(BigDecimal value, int startDegree, int endDegree, int radix,
			String radixIndicate, DecimalFormat shortDigitalFormat, String unit) {
		BigDecimal dval = value;
		int degree = startDegree;

		BigDecimal bradix = BigDecimal.valueOf(radix);

		while (dval.compareTo(bradix) >= 0 && degree < endDegree && degree < digitalDegrees.length - 1) {
			dval = dval.divide(bradix, 2, RoundingMode.HALF_EVEN);
			degree++;
		}

		StringBuilder str = new StringBuilder().append(shortDigitalFormat.format(dval)).append(" ")
				.append(digitalDegrees[degree]).append(degree > 0 ? radixIndicate : "").append(unit);
		return str.toString();
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String getHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String getTimeString(String pattern, long time) {
		SimpleDateFormat f = new SimpleDateFormat(pattern);
		return f.format(time);
	}

	public static String ellipsis(String str, int length) {
		if (str == null) {
			return null;
		}
		if (str.length() <= length) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		int e = length - 3;
		if (e > 0) {
			sb.append(str.substring(0, e));
			e = 3;
		} else {
			e = length;
		}
		for (int i = 0; i < e; i++) {
			sb.append(".");
		}
		return sb.toString();
	}

	public static int compareTo(String str1, String str2) {
		if (str1 != null && str2 != null) {
			return str1.compareTo(str2);
		} else if (str1 != null) {
			return 1;
		} else if (str2 != null) {
			return -1;
		}
		return 0;
	}

	public static int compareToIgnoreCase(String str1, String str2) {
		if (str1 != null && str2 != null) {
			return str1.compareToIgnoreCase(str2);
		} else if (str1 != null) {
			return 1;
		} else if (str2 != null) {
			return -1;
		}
		return 0;
	}

	public static String cat(Object... strs) {
		StringBuilder sb = new StringBuilder();
		for (Object s : strs) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String cat(Collection<String> collection) {
		return cat(collection, ",");
	}

	public static String cat(Collection<String> collection, String comma) {
		if (collection == null || collection.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String s : collection) {
			if (sb.length() > 0) {
				sb.append(comma);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public static String toInfoExpr(Map<String, String> infoMap) {
		if (infoMap == null || infoMap.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String key : infoMap.keySet()) {
			String val = infoMap.get(key);
			sb.append("[");
			for (char c : key.toCharArray()) {
				switch (c) {
				case '\\':
					sb.append("\\\\");
					break;
				case ':':
					sb.append("\\:");
					break;
				default:
					sb.append(c);
				}
			}
			sb.append(":");
			for (char c : val.toCharArray()) {
				switch (c) {
				case '\\':
					sb.append("\\\\");
					break;
				case ']':
					sb.append("\\]");
					break;
				default:
					sb.append(c);
				}
			}
			sb.append("]");
		}
		return sb.toString();
	}

	public static Map<String, String> toInfoMap(String infoMapExpr) {
		Map<String, String> map = new LinkedHashMap<>();
		if (infoMapExpr == null || infoMapExpr.length() == 0) {
			return map;
		}
		int state = 0;// 0:start,1:name,2:value
		StringBuilder key = null;
		StringBuilder val = null;

		char[] arr = infoMapExpr.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];

			if (state == 0) {
				if (c == '[') {
					key = new StringBuilder();
					val = new StringBuilder();
					state = 1;
				}
			} else if (state == 1) {
				if (c == '\\') {
					if (i < arr.length - 1) {
						key.append(arr[i + 1]);
						i++;
					}
				} else if (c == ':') {
					state = 2;
				} else {
					key.append(c);
				}
			} else if (state == 2) {
				if (c == '\\') {
					if (i < arr.length - 1) {
						val.append(arr[i + 1]);
						i++;
					}
				} else if (c == ']') {
					map.put(key.toString().trim(), val.toString().trim());
					state = 0;
				} else {
					val.append(c);
				}
			}
		}
		return map;
	}

	/**
	 * parse expression to a millisecond time value You can put long value(e.g.
	 * 18000) or time expression in the config file, e.g. 3d9h30m20s30ms. Acceptable
	 * symbol, d-Day, h-Hour, m-Minute, s-second, ms-Millisecond
	 */
	public static Long parseMillisecond(String timeExpr) {
		if (Strings.isBlank(timeExpr)) {
			return null;
		}
		timeExpr = timeExpr.trim();
		if (timeExpr.matches("[0-9]*")) {
			return Long.parseLong(timeExpr);
		}

		Pattern patten = Pattern.compile("[^0-9]*");
		Matcher matcher = patten.matcher(timeExpr);
		int idx = 0;
		String unit;
		String val;
		int s;
		int e;
		long time = 0;
		while (matcher.find()) {
			s = matcher.start();
			e = matcher.end();
			if (s == e) {
				continue;
			}
			unit = matcher.group().trim();
			val = timeExpr.substring(idx, s);
			try {
				switch (unit.toLowerCase()) {
				case "ms":
					time += Long.parseLong(val);
					break;
				case "s":
					time += Long.parseLong(val) * Numbers.SECOND;
					break;
				case "m":
					time += Long.parseLong(val) * Numbers.MINUTE;
					break;
				case "h":
					time += Long.parseLong(val) * Numbers.HOUR;
					break;
				case "d":
					time += Long.parseLong(val) * Numbers.DAY;
					break;
				case "y":
					time += Long.parseLong(val) * 365 * Numbers.DAY;
					break;
				default:
					throw new IllegalStateException(
							"wrong fromat " + timeExpr + ", only y,d,h,m,s,ms are allowed, but get " + unit);
				}
			} catch (NumberFormatException x) {
				throw new IllegalStateException("wrong fromat " + timeExpr + ", only y,d,h,m,s,ms unit are allowed", x);
			}
			idx = e;
		}
		if (idx != timeExpr.length()) {
			throw new IllegalStateException("wrong fromat " + timeExpr + ", only y,d,h,m,s,ms are allowed");
		}
		return time;
	}

	public static void println(String str, Object... args) {
		System.out.println(format(str, args));
	}

	public static void print(String str, Object... args) {
		System.out.print(format(str, args));
	}

	public static String toString(Object nullable) {
		return nullable == null ? "" : nullable.toString();
	}

}