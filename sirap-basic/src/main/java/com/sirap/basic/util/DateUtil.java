package com.sirap.basic.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;

public class DateUtil {

	//https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
	public static final String WEEK_DATE = "EEEE MM/dd/yyyy";
	public static final String WEEK_DATE_TIME = "EEEE MM/dd/yyyy HH:mm:ss";
	public static final String HOUR_Min_Sec_AM_DATE = "hh:mm:ss aa, MMM dd, yyyy";
	public static final String HOUR_Min_Sec_AM_WEEK_DATE = "hh:mm:ss aa, EEEE, MMM dd, yyyy";
	public static final String HOUR_Min_Sec_Milli_AM_WEEK_DATE = "hh:mm:ss.SSS aa, EEEE, MMM dd, yyyy";
	public static final String HOUR_Min_AM_WEEK_DATE = "hh:mm aa, EEEE, MMM dd, yyyy";
	public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String HTTP_STYLE = "EEE, MMM dd HH:mm:ss z yyyy";
	public static final String F5TXT = "HH:mm yyyy/MM/dd";
	public static final String MAVEN = "yyyy-MM-dd'T'HH:mm:ssXXX";
	public static final String GMT = "yyyy-MM-dd HH:mm:ss 'GMT'XXX EEEE";
	public static final String GMT2 = "yyyy-MM-dd HH:mm:ss.SSS 'GMT'XXX EEEE";
	public static final String TIGHT17 = "yyyyMMddHHmmssSSS";
	public static final String TIGHT17_SAMPLE = "19700101" + "HHmmssSSS".replaceAll(".", "0");
	public static final String DATETIME_UNDERLINE_TIGHT = "yyyyMMdd_HHmmss";
	public static final String DATE_TIME_FULL = "yyyy-MM-dd_HH:mm:ss.SSS";
	public static final String DATE_ONLY = "yyyy-MM-dd";
	public static final String DATE_ONLY_COMMA = "yyyy.MM.dd";
	public static final String DATE_US = "MMM dd, yyyy";
	public static final String DATE_TIGHT = "yyyyMMdd";
	public static final String TIME_ONLY = "HH:mm:ss";
	public static final String TIME_TIGHT = "HHmmss";
	public static final String[] ROMAN_NUMBERS = {"VII", "I", "II", "III", "IV", "V", "VI"};
	public static final List<String> WEEK_DAY_NUMBERS = StrUtil.split("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
	public static final int[] MAX_DAY_IN_MONTH_LEAP_YEAR = {31,29,31,30,31,30,31,31,30,31,30,31};
	public static final int[] MAX_DAY_IN_MONTH = {31,28,31,30,31,30,31,31,30,31,30,31};
	public static final String NTP_SITE = "http://www.ntsc.ac.cn";

	public static Date calendarToDate(Calendar cal) {
		if(cal == null) {
			return null;
		}
		
		Date date = new Date();
		date.setTime(cal.getTimeInMillis());
		
		return date;
	}

	public static Calendar dateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		return cal;
	}

	public static Date nextDay(int diff) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, diff);
				
		return calendarToDate(cal);
	}

	public static Date nextMonth(int diff) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, diff);
				
		return calendarToDate(cal);
	}
	
	public static Date nextSharpHour(int diff) {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int next = hour + diff;

		cal.set(Calendar.HOUR_OF_DAY, next);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
				
		return calendarToDate(cal);
	}
	
	public static Calendar nextSharpHourCalendar(int diff) {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int next = hour + diff;

		cal.set(Calendar.HOUR_OF_DAY, next);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
				
		return cal;
	}
	
	public static Date add(Date date, int field, int value) {
		Calendar cal = dateToCalendar(date);
		cal.add(field, value);
		
		return calendarToDate(cal);
	}
	
	public static int getYear(Date date) {
		Calendar cal = dateToCalendar(date);
		return cal.get(Calendar.YEAR);
	}
	
	public static int getMonth(Date date) {
		Calendar cal = dateToCalendar(date);
		return cal.get(Calendar.MONTH);
	}
	
	public static int getDay(Date date) {
		Calendar cal = dateToCalendar(date);
		return cal.get(Calendar.DAY_OF_YEAR);
	}
	
	public static int getHour() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	public static int getDayOfWeek(Date date) {
		int index = WEEK_DAY_NUMBERS.indexOf(displayDate(date, "E")) + 1;
		
		return index;
	}
	
	public static int getDayOfWeek() {
		int index = WEEK_DAY_NUMBERS.indexOf(displayNow("E")) + 1;
		
		return index;
	}
	
	/***
	 * 
	 * @param source , such as 1457894561642.
	 * @return
	 */
	public static Date parseLongStrX(Object source) {
		XXXUtil.nullOrEmptyCheck(source, "source");
		Long milliSecondsSince1970 = Long.parseLong(source + "");
		
		Date date = new Date(milliSecondsSince1970);
		return date;
	}

	public static Date parse(String format, String dateStr) {
		return parse(format, dateStr, true);
	}
	
	public static String wrapTightYMD(String source) {
		if(!StrUtil.isRegexMatched("\\d{1,8}", source)) {
			XXXUtil.alert("Illegal date string ", source);
		}
		
		String current = displayNow(DATE_TIGHT);
		int len = DATE_TIGHT.length() -source.length();
		String value = current.substring(0, len) + source;
		
		return value;
	}
	
	public static String displayDateCompact(Date date) {
		Calendar cal = dateToCalendar(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		String str = year + "." + month + "." + day;
		return str;
	}
	
	public static Date construct(Object year, Object month, Object day) {
		Date date = construct(year, month, day, 0, 0, 0);
		
		return date;
	}
	
	public static Date construct(Object year, Object month, Object day, Object hour, Object min, Object second) {
		Calendar now = Calendar.getInstance();
		Integer iYear = MathUtil.toInteger(year + "");
		if(iYear != null) {
			now.set(Calendar.YEAR, iYear);
		}
		
		Integer iMonth = MathUtil.toInteger(month + "");
		if(iMonth != null) {
			now.set(Calendar.MONTH, iMonth - 1);
		}
		
		Integer iDay = MathUtil.toInteger(day + "");
		if(iDay != null) {
			now.set(Calendar.DAY_OF_MONTH, iDay);
		}
		
		Integer iHour = MathUtil.toInteger(hour + "");
		if(iHour != null) {
			now.set(Calendar.HOUR_OF_DAY, iHour);
		}
		
		Integer iMin = MathUtil.toInteger(min + "");
		if(iMin != null) {
			now.set(Calendar.MINUTE, iMin);
		}
		
		Integer iSecond = MathUtil.toInteger(second + "");
		if(iSecond != null) {
			now.set(Calendar.SECOND, iSecond);
		}
		
		return calendarToDate(now);
	}
	
	public static Date parse(String format, String dateStr, boolean printExceptionIfFail) {
		return parse(format, dateStr, Locale.US, printExceptionIfFail);
	}
	
	public static Date parse(String format, String dateStr, Locale locale, boolean printExceptionIfFail) {
		if(dateStr == null || format == null || locale == null) return null;
		
		DateFormat df = new SimpleDateFormat(format, locale);
		Date date = null;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			if(printExceptionIfFail) {
				e.printStackTrace();
			}
		}
		
		return date;
	}
	
	public static String timestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_UNDERLINE_TIGHT);
		return sdf.format(new Date());
	}

	public static String displayNow() {
		return displayDate(new Date(), HOUR_Min_Sec_AM_WEEK_DATE);
	}

	public static String displayNow(String format) {
		return displayDate(new Date(), format);
	}
	
	public static String displayDate(Date date) {
		return displayDate(date, HOUR_Min_Sec_AM_WEEK_DATE);
	}

	public static String displayDate(Date date, String format) {
		return displayDate(date, format, null);
	}
	
	/***
	 * 2017
	 * 20160104
	 * 2013122112
	 * 20120903130934
	 * 20120903130934698
	 * @param datetimeItems
	 * @return
	 */
	public static Date dateOfTight17(String datetimeItems, Object timezone) {
		return dateOf(tight17Of(datetimeItems), TIGHT17, timezone, null);
	}
	
	public static String tight17Of(String datetimeItems) {
		int len = datetimeItems.length();
		XXXUtil.checkRange(len, 4, TIGHT17.length(), datetimeItems);
		
		String full = TIGHT17_SAMPLE.replaceAll("^.{" + len + "}", datetimeItems);
		
		return full;
	}
	
	/***
	 * Gotta be since 1970.01.01
	 * @param source
	 * @return
	 */
	public static String convertLongToDateStrX(long milliSecondsSince1970, String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat, Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String value = df.format(milliSecondsSince1970);
		
		return value;
	}
	
	public static String displayDate(String format, Locale locale) {
		return displayDate(new Date(), format, locale);
	}
//	
//	public static String displayDateWithGMT(Date date, String format, Locale locale) {
//		return displayDateWithGMT(date, format, locale, TIMEZONE_JVM);
//	}
//	
//	public static String displayDateWithGMT(Date date, String format, Locale locale, int tz) {
//		String dateStr = displayDate(date, format, locale);
//		String gmt = " GMT" + StrUtil.signValue(tz);
//		
//		return dateStr + gmt;
//	}
	
	public static String displayDate(Date date, String format, Locale locale) {
		XXXUtil.nullCheck(date, "Date date");
		XXXUtil.nullCheck(format, "String format");
		
		DateFormat df = null;
		if(locale != null) {
			df = new SimpleDateFormat(format, locale);
		} else {
			df = new SimpleDateFormat(format, Locale.US);
		}
		
		return df.format(date);
	}
	
	//0 for January, 11 for December
	public static String getMonthName(int monthIndex, Locale locale) {
		return getMonthName(monthIndex, false, locale);
	}
	
	public static String getMonthName(int monthIndex, boolean isShort, Locale locale) {
		if(monthIndex < 0 || monthIndex > 11) {
			XXXUtil.alert("Dude, you are so out there with index[" + monthIndex + "]");
		}
		
		DateFormatSymbols SYMBOLS = new DateFormatSymbols(locale);
		String[] arr = isShort ? SYMBOLS.getShortMonths() : SYMBOLS.getMonths();
		
		return arr[monthIndex];
	}
	
	public static String parseMonthIndex(String kw) {
		String month = null;
		DateFormatSymbols SYMBOLS = new DateFormatSymbols(Locale.US);
		String[] arr = SYMBOLS.getShortMonths();
		for(int i = 0; i < arr.length; i++) {
			String temp = arr[i];
			if(StrUtil.equals(temp, kw)) {
				month = (i + 1) + "";
				break;
			}
		}
		
		if(month != null) {
			return StrUtil.padLeft(month, 2, "0");
		}
		
		arr = SYMBOLS.getMonths();
		for(int i = 0; i < arr.length; i++) {
			String temp = arr[i];
			if(StrUtil.contains(temp, kw, 3)) {
				month = (i + 1) + "";
				break;
			}
		}
		
		if(month != null) {
			return StrUtil.padLeft(month, 2, "0");
		}
		
		return null;
	}
		
	public static Date hourDiff(Date date, double hourDiff) {
		XXXUtil.nullCheck(date, "Date date");
		
		Date newDate = add(date, Calendar.MINUTE, (int)(hourDiff * 60));
		return newDate;
	}
	
	public static String tzoneOffsetInHour(String tzID) {
		TimeZone tz = TimeZone.getDefault();
		
		if(tzID != null) {
			tz = TimeZone.getTimeZone(tzID);
		}
		
		int offSetInMilli = tz.getOffset(new Date().getTime());
		
		double diff = offSetInMilli / (Konstants.MILLI_PER_HOUR + 0.0);
		String value = StrUtil.removePointZeroes(diff + "");
		if(diff > 0) {
			value = "+" + value;
		}
		
		return value;
	}
	
	public static String[] getShortWeekdays() {
		return getShortWeekdays(Locale.US);
	}
	
	public static String[] getShortWeekdays(Locale locale) {
		DateFormatSymbols SYMBOLS = new DateFormatSymbols(locale);
		return SYMBOLS.getShortWeekdays();
	}
	
	public static int dayDiff(Date d1, Date d2) {
		long diffInMilli = d1.getTime() - d2.getTime();
		
		int diff = (int)(diffInMilli / Konstants.MILLI_PER_DAY);
		
		return diff;
	}

	public static int hourDiff(Date d1, Date d2) {
		long diffInMilli = d1.getTime() - d2.getTime();
		
		int diff = (int)(diffInMilli / Konstants.MILLI_PER_HOUR);
		
		return diff;
	}

	public static int minDiff(Date d1, Date d2) {
		long diffInMilli = d1.getTime() - d2.getTime();
		
		int diff = (int)(diffInMilli / Konstants.MILLI_PER_MINUTE);
		
		return diff;
	}


	public static boolean isSameDay(Date date1, Date date2) {
		Calendar c1 = dateToCalendar(date1);
		Calendar c2 = dateToCalendar(date2);
		
		boolean sameDayOfYear = c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
		if(!sameDayOfYear) {
			return false;
		}
		
		boolean sameYear = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
		
		return sameYear;
	}

	public static int monthDiff(Date d1, Date d2) {
		Calendar c1 = dateToCalendar(d1);
		Calendar c2 = dateToCalendar(d2);
		
		int base = 0;
		int yearDiff = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
		if(yearDiff != 0) {
			base = yearDiff * 12;
		}
		
		int monthDiff = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		base += monthDiff;
		
		return base;
	}

	public static int compareTillDay(Date date1, Date date2) {
		Calendar c1 = dateToCalendar(date1);
		Calendar c2 = dateToCalendar(date2);
		
		int yearDiff = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
		if(yearDiff != 0) {
			return yearDiff;
		}
		
		int dayDiff = c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR);
		if(dayDiff != 0) {
			return dayDiff;
		}
		
		return 0;
	}
	
	/***
	 * 
	 * @param when
	 * @param lower inclusive
	 * @param upper inclusive
	 * @return
	 */
	public static boolean isDayBetweenPeriod(Date when, Date lower, Date upper) {
		if(DateUtil.compareTillDay(when, lower) >= 0 && DateUtil.compareTillDay(upper, when) >= 0) {
			return true;
		}
		
		return false;
	}
	
//	public static String displaySirapFullDate() {
//		return displaySirapFullDateX(new Date());
//	}
//	
//	public static String displaySirapFullDate(Date date, Locale lot) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(displayDate(date, "hh:mm:ss aa"));
//		sb.append(", GMT" + StrUtil.signValue(TIMEZONE_JVM) + ", ");
//		sb.append(displayDate(date, "MM/dd/yyyy"));
//		
//		Calendar cal = dateToCalendar(date);
//		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
//		sb.append(", " + ROMAN_NUMBERS[dayOfWeek - 1]);
//		
//		return sb.toString();
//	}
	
	public static String convertSecondsIntoHourMinuteSecond(int totalSeconds) {
		int hours = totalSeconds / (Konstants.TIME_STEP * Konstants.TIME_STEP);
		int temp = totalSeconds % (Konstants.TIME_STEP * Konstants.TIME_STEP);
		
		int minutes = temp / Konstants.TIME_STEP;
		temp = temp % Konstants.TIME_STEP;
		
		int seconds = temp;
		
		StringBuffer sb = new StringBuffer();
		sb.append(hours).append(":");
		sb.append(StrUtil.padLeft(minutes + "", 2, "0")).append(":");
		sb.append(StrUtil.padLeft(seconds + "", 2, "0"));
		
		return sb.toString();
	}
	
	public static String getJanuaryLikeMonth(int month1To12, boolean showLongName) {
		XXXUtil.checkMonthRange(month1To12);
		if(showLongName) {
			return new DateFormatSymbols(Locale.US).getMonths()[month1To12 - 1];
		} else {
			return new DateFormatSymbols(Locale.US).getShortMonths()[month1To12 - 1];
		}
	}
	
	public static int parseJanuaryLikeMonth(String monthJanuaryToDecember) {
		String[] arr = new DateFormatSymbols().getMonths();
		for(int i = 0; i < arr.length; i++) {
			if(arr[i].toLowerCase().startsWith(monthJanuaryToDecember.toLowerCase())) {
				return i + 1;
			}
		}
		
		throw new MexException("Invalid month '{0}'", monthJanuaryToDecember);
	}
	
	public static boolean isLeapYear(int year) {
		return new GregorianCalendar().isLeapYear(year);
	}
	
	public static List<String> getAllDaysByYear(int year) {
		List<String> items = Lists.newArrayList();
		
		int max = isLeapYear(year) ? 366 : 365;
		Date jan1st = DateUtil.construct(year, 1, 0);
		for(int k = 1 ; k <= max; k++) {
			Date date = add(jan1st, Calendar.DAY_OF_YEAR, k);
			items.add(displayDate(date, DATE_TIGHT));
		}
		
		return items;
	}
	
	public static Date parse(DateFormat format, String datestr) {
		try {
			return format.parse(datestr);
		} catch (ParseException ex) {
			throw new MexException(ex);
		}
	}

	public static SimpleDateFormat formatOf(String pattern, Object zoneinfo, Locale lot) {
		if(lot == null) {
			lot = Locale.ENGLISH;
		}
		SimpleDateFormat susan = new SimpleDateFormat(pattern, lot);
		if(zoneinfo != null) {
			String zoneid;
			String temp = zoneinfo + "";
			Integer ivan = MathUtil.toInteger(temp);
			if(ivan != null) {
				String sign = ivan >= 0 ? "+" : "";
				zoneid = "GMT" + sign + ivan;
			} else {
				zoneid = temp;
			}
			susan.setTimeZone(TimeZone.getTimeZone(zoneid));
		}
		
		return susan;
	}
	
	public static Date dateOf(String str, String pattern, Object timezone, Locale lot) {
		SimpleDateFormat susan = formatOf(pattern, timezone, lot);
		return parse(susan, str);
	}
	
	public static Date dateOf(String str, String pattern) {
		SimpleDateFormat susan = new SimpleDateFormat(pattern);
		return parse(susan, str);
	}
	
	public static Date dateOf(long millis) {
		Date date = new Date(millis);
		
		return date;
	}
	
	public static String strOf(long date, String pattern, Object zoneinfo, Locale lot) {
		SimpleDateFormat susan = formatOf(pattern, zoneinfo, lot);
		String temp = susan.format(date);
		temp = temp.replace("Z", "+00:00");
		
		return temp;
	}
	
	public static String strOf(long date, String pattern, Locale lot) {
		return strOf(date, pattern, null, lot);
	}
	
	public static String strOf(long date, String pattern) {
		return strOf(date, pattern, null, null);
	}
	
	public static String strOf(Date date, String pattern) {
		return strOf(date.getTime(), pattern, null, null);
	}
	
	public static String strOf(Date date, String pattern, Locale lot) {
		return strOf(date.getTime(), pattern, null, lot);
	}
	
	public static String strOf(Date date, String pattern, Object zoneinfo, Locale lot) {
		return strOf(date.getTime(), pattern, zoneinfo, lot);
	}
	
	public static String info(Date date, Locale locale) {
		String temp = strOf(date.getTime(), GMT2, null, locale);
		return "** " + temp + " **";
	}
	
	public static String infoNow(Locale locale) {
		return info(new Date(), locale);
	}

	public static String timeAgo(Date when) {
		return timeAgo(when.getTime());
	}
	
	public static String timeAgo(long when) {
		long duration = (new Date()).getTime() - when;
		String timeago = MathUtil.ymdhmsStrOfSeconds((int)(duration/1000));
		
		return timeago;
	}
}
