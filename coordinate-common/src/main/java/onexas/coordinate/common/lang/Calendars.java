package onexas.coordinate.common.lang;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Calendars {

//	public static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");

	public static final Map<String, TimeZone> avaliableTimeZoneMap = Collections.newConcurrentMap();
	static {
		for (String id : TimeZone.getAvailableIDs()) {
			TimeZone tz = TimeZone.getTimeZone(id);
			avaliableTimeZoneMap.put(id, tz);
		}
	}

	public static TimeZone getAvaliableTimeZone(String id) {
		return avaliableTimeZoneMap.get(id);
	}

//	public static TimeZone getGMT(int gmtTimeZone) {
//		return TimeZone.getTimeZone(Strings.format("GMT{}{}", gmtTimeZone >= 0 ? "+" : "-", Math.abs(gmtTimeZone)));
//	}
//
//	public static int getGMT(TimeZone timezone) {
//		int millsecond = timezone.getRawOffset();
//		return millsecond / (1000 * 60 * 60);
//	}
//
//	/**
//	 * get timezone for +0800 or -0700
//	 */
//	public static TimeZone getGMT(String tz) {
//		return TimeZone.getTimeZone(Strings.format("GMT{}", tz));
//	}
//
//	public static class GMTTimeZone {
//
//		public static final GMTTimeZone GMT0 = new GMTTimeZone(0);
//
//		final int gmt;
//		final TimeZone timeZone;
//
//		public GMTTimeZone(int gmt) {
//			this.gmt = gmt;
//			timeZone = Calendars.getGMT(gmt);
//		}
//
//		public GMTTimeZone(TimeZone timeZone) {
//			this.timeZone = timeZone;
//			this.gmt = Calendars.getGMT(timeZone);
//		}
//
//		public int getGMT() {
//			return gmt;
//		}
//
//		public TimeZone getTimeZone() {
//			return timeZone;
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + gmt;
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			GMTTimeZone other = (GMTTimeZone) obj;
//			if (gmt != other.gmt)
//				return false;
//			return true;
//		}
//
//		@Override
//		public String toString() {
//			return "GMTTimeZone [gmt=" + gmt + "]";
//		}
//	}

	public static String UNIFORM_DATETIME_FORMAT = "yyyyMMddHHmmss";
	
	public static String UNIFORM_DATE_FORMAT = "yyyyMMdd";

	public static DateFormat getUniformDateTimeFormat(TimeZone tz) {
		DateFormat f = new SimpleDateFormat(UNIFORM_DATETIME_FORMAT, new DateFormatSymbols(Locale.ENGLISH));
		if (tz != null) {
			f.setTimeZone(tz);
		}
		return f;
	}
	
	public static DateFormat getUniformDateFormat(TimeZone tz) {
		DateFormat f = new SimpleDateFormat(UNIFORM_DATE_FORMAT, new DateFormatSymbols(Locale.ENGLISH));
		if (tz != null) {
			f.setTimeZone(tz);
		}
		return f;
	}

	public static Date parseUniformDateTime(String str) {
		return parseUniformDateTime(TimeZone.getDefault(), str);
	}

	public static Date parseUniformDateTime(TimeZone tz, String str) {
		if (str == null) {
			return null;
		}
		try {
			return getUniformDateTimeFormat(tz).parse(str);
		} catch (ParseException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static String formatUniformDateTime(Date date) {
		return formatUniformDateTime(TimeZone.getDefault(), date);
	}

	public static String formatUniformDateTime(TimeZone tz, Date date) {
		if (date == null) {
			return null;
		}
		return getUniformDateTimeFormat(tz).format(date);
	}
}