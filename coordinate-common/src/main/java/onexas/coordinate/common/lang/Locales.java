package onexas.coordinate.common.lang;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * 
 * @author Dennis Chen
 * 
 */
public class Locales {

	public static Locale getLocale(String locale) {
		return getLocale(locale, "_");
	}

	public static Locale getLocale(String locale, String delim) {
		StringTokenizer st = new StringTokenizer(locale, delim);
		String l = null;
		String c = null;
		if (st.hasMoreElements()) {//
			l = (String) st.nextElement();
		}
		if (st.hasMoreElements()) {
			c = (String) st.nextElement();
		}
		if (!Strings.isBlank(l) && !Strings.isBlank(c)) {
			return new Locale(l, c);
		} else if (!Strings.isBlank(l)) {
			return new Locale(l);
		}
		throw new IllegalStateException("Unknown locale " + locale);
	}

	public static String toString(Locale locale) {
		return toString(locale, "_");
	}

	public static String toString(Locale locale, String delim) {
		String l = locale.getLanguage();
		String c = locale.getCountry();
		StringBuilder sb = new StringBuilder();
		if (!Strings.isBlank(l)) {
			sb.append(l);
			if (!Strings.isBlank(c)) {
				sb.append(delim).append(c);
			}
		} else if (!Strings.isBlank(c)) {
			sb.append(c);
		}
		return sb.toString();
	}
}