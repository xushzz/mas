package com.sirap.basic.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;

public class DateUtil {

	public static final int TIMEZONE_JVM = DateUtil.getDefaultTimeZone();
	public static final String WEEK_DATE = "EEEE MM/dd/yyyy";
	public static final String WEEK_DATE_TIME = "EEEE MM/dd/yyyy HH:mm:ss";
	public static final String HOUR_Min_Sec_AM_DATE = "hh:mm:ss aa, MMM dd, yyyy";
	public static final String HOUR_Min_Sec_AM_WEEK_DATE = "hh:mm:ss aa, EEEE, MMM dd, yyyy";
	public static final String HOUR_Min_Sec_Milli_AM_WEEK_DATE = "hh:mm:ss.SSS aa, EEEE, MMM dd, yyyy";
	public static final String HOUR_Min_AM_WEEK_DATE = "hh:mm aa, EEEE, MMM dd, yyyy";
	public static final String DATETIME= "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_F5TXT = "HH:mm yyyy/MM/dd";
	public static final String DATETIME_TIGHT = "yyyyMMddHHmmss";
	public static final String DATETIME_ALL_TIGHT = "yyyyMMddHHmmssSSS";
	public static final String DATETIME_SPACE_TIGHT = "yyyyMMdd_HHmmss";
	public static final String DATE_TIME_FULL = "yyyy-MM-dd_HH:mm:ss.SSS";
	public static final String DATE_ONLY = "yyyy-MM-dd";
	public static final String DATE_ONLY_COMMA = "yyyy.MM.dd";
	public static final String DATE_US = "MMM dd, yyyy";
	public static final String DATE_TIGHT = "yyyyMMdd";
	public static final String TIME_ONLY = "HH:mm:ss";
	public static final String TIME_TIGHT = "HHmmss";
	public static final String[] ROMAN_NUMBERS = {"VII", "I", "II", "III", "IV", "V", "VI"};
	public static final List<String> WEEK_DAY_NUMBERS = StrUtil.split("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
	
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
	public static Date parseLongStr(Object source) {
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
	
	private static String wrapZero(String datetimeItems) {
		int len = DATETIME_ALL_TIGHT.length() - datetimeItems.length();
		StringBuffer sb = new StringBuffer();
		sb.append(datetimeItems);
		if(len > 0) {
			for(int i = 0; i < len; i++) {
				sb.append("0");
			}
		}
		
		String dateStr = sb.toString();
		
		return dateStr;
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
		SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_SPACE_TIGHT);
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
	
	public static Long convertDateStrToLong(String datetimeItems) {
		Date date = null;
		if(EmptyUtil.isNullOrEmpty(datetimeItems)) {
			date = new Date();			
		} else {
			String dateStr = wrapZero(datetimeItems);
			DateFormat df = new SimpleDateFormat(DATETIME_ALL_TIGHT, Locale.US);
			try {
				date = df.parse(dateStr);
			} catch (Exception ex) {
				throw new MexException(ex.getMessage());
			}
		}

		long now = date.getTime();
		long milli1970 = DateUtil.parse(DATETIME_ALL_TIGHT, "19700101000000000").getTime();
		long diff = now - milli1970;
		
		return diff;
	}
	
	/***
	 * Gotta be since 1970.01.01
	 * @param source
	 * @return
	 */
	public static String convertLongToDateStr(long milliSecondsSince1970, String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat, Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String value = df.format(milliSecondsSince1970);
		
		return value;
	}

	public static String displayDateWithGMT(Date date, String format) {
		String dateStr = displayDate(date, format, null);
		String gmt = " GMT" + StrUtil.signValue(TIMEZONE_JVM);
		
		return dateStr + gmt;
	}
	
	public static String displayDate(String format, Locale locale) {
		return displayDate(new Date(), format, locale);
	}
	
	public static String displayDateWithGMT(Date date, String format, Locale locale) {
		return displayDateWithGMT(date, format, locale, TIMEZONE_JVM);
	}
	
	public static String displayDateWithGMT(Date date, String format, Locale locale, int tz) {
		String dateStr = displayDate(date, format, locale);
		String gmt = " GMT" + StrUtil.signValue(tz);
		
		return dateStr + gmt;
	}
	
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
			return StrUtil.extendLeftward(month, 2, "0");
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
			return StrUtil.extendLeftward(month, 2, "0");
		}
		
		return null;
	}
		
	public static Date hourDiff(Date date, int hourDiff) {
		XXXUtil.nullCheck(date, "Date date");
		
		Date newDate = add(date, Calendar.HOUR_OF_DAY, hourDiff);
		return newDate;
	}
	
	public static int getDefaultTimeZone() {
		return getTimeZoneDiff(null);
	}
	
	public static int getTimeZoneDiff(String tzID) {
		TimeZone tz = TimeZone.getDefault();
		
		if(tzID != null) {
			tz = TimeZone.getTimeZone(tzID);
		}
		
		int offSetInMilli = tz.getOffset(new Date().getTime());
		
		int diff = offSetInMilli / Konstants.MILLI_PER_HOUR;
		
		return diff;
	}
	
	public static Locale parseLocale(String localeStr) {
		if(localeStr == null) {
			return null;
		}

		String lang, area;
		
		Pattern p = Pattern.compile("([a-z]{2})_([A-Z]{2})", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(localeStr);
		
		if(m.matches()) {
			lang = m.group(1);
			area = m.group(2);
			return new Locale(lang, area);
		}
		
		p = Pattern.compile("([a-z]{2})", Pattern.CASE_INSENSITIVE);
		m = p.matcher(localeStr);
		
		if(m.matches()) {
			lang = m.group(1);
			return new Locale(lang);
		}
		
		return null;
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
	
	public static Date getTZRelatedDate(int targetTimezone, Date localDate) {
		int diffBetweenAppAndLocal = targetTimezone - TIMEZONE_JVM;
		Date areaDate = DateUtil.hourDiff(localDate, diffBetweenAppAndLocal);
		return areaDate;
	}
	
	public static String displaySirapFullDate() {
		return displaySirapFullDate(new Date());
	}
	
	public static String displaySirapFullDate(Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append(displayDate(date, "hh:mm:ss aa"));
		sb.append(", GMT" + StrUtil.signValue(TIMEZONE_JVM) + ", ");
		sb.append(displayDate(date, "MM/dd/yyyy"));
		
		Calendar cal = dateToCalendar(date);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		sb.append(", " + ROMAN_NUMBERS[dayOfWeek - 1]);
		
		return sb.toString();
	}
	
	public static String convertSecondsIntoHourMinuteSecond(int totalSeconds) {
		int hours = totalSeconds / (Konstants.TIME_STEP * Konstants.TIME_STEP);
		int temp = totalSeconds % (Konstants.TIME_STEP * Konstants.TIME_STEP);
		
		int minutes = temp / Konstants.TIME_STEP;
		temp = temp % Konstants.TIME_STEP;
		
		int seconds = temp;
		
		StringBuffer sb = new StringBuffer();
		sb.append(hours).append(":");
		sb.append(StrUtil.extendLeftward(minutes + "", 2, "0")).append(":");
		sb.append(StrUtil.extendLeftward(seconds + "", 2, "0"));
		
		return sb.toString();
	}
}
