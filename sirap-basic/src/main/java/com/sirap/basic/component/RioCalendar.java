package com.sirap.basic.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class RioCalendar {

	private static final int DAYS_OF_WEEK = 7;
	private static final int YEAR_MIN = 1000;
	private static final int YEAR_MAX = 9999;
	
	private int lenOfWeekDay = 4;

	private Locale locale;
	private int monthIndex;
	private int year;
	
	private List<String> records = new ArrayList<String>();

	public RioCalendar(int monthParam) {
		this.monthIndex = monthParam - 1; 
	}

	public RioCalendar(int yearParam, int monthParam) {
		this.year = yearParam;
		this.monthIndex = monthParam - 1; 
	}
	
	public List<String> getRecords() {
		return records;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale == null ? Locale.US : locale;
	}

	public boolean generate() {
		if(!verify()) {
			return false;
		}
		
		printTitle();
		printHeader();
		printBody();
		return true;
	}
	
	private boolean verify() {
		String template = "Invalid {0}[{1}], out of range[{2},{3}]";
		boolean isLegal = monthIndex >= 0 && monthIndex <= 11;
		if(!isLegal) {
			C.pl(StrUtil.occupy(template, "Month", (monthIndex + 1), 1, 12));
			return false;
		}
		
		isLegal = year == 0 || year >= YEAR_MIN && year <= YEAR_MAX;
		if(!isLegal) {
			C.pl(StrUtil.occupy(template, "Year", year, YEAR_MIN, YEAR_MAX));
			return false;
		}
		
		return true;
	}
	
	private void printTitle() {
		String yearInfo = year != 0 ? " " + year : "";
		String monthName = DateUtil.getMonthName(monthIndex, getLocale()); 
		records.add(monthName + yearInfo);
	}
	
	private String fixWeekDay(String weekDay) {
		String temp = weekDay;
		String lang = getLocale().getLanguage();
		
		if(lang.equalsIgnoreCase(Locale.CHINESE.getLanguage())) {
			temp = temp.substring(temp.length() - 1);
			lenOfWeekDay = 3;
		} else if(lang.equalsIgnoreCase(Locale.JAPANESE.getLanguage())) {
			temp = temp.substring(0);
			lenOfWeekDay = 3;
		}
		
		return temp;
	}
	
	private void printHeader() {
		String[] weekDays = DateUtil.getShortWeekdays(getLocale());
		StringBuffer sb = new StringBuffer();
		for(int i = 1 ; i < weekDays.length; i++) {
			String weekDay = fixWeekDay(weekDays[i]);
			sb.append(StrUtil.extendLeftward(weekDay + " ", lenOfWeekDay));
		}
		
		records.add(sb.toString());
	}
	
	private void printBody() {
		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		int currentMonthIndex = cal.get(Calendar.MONTH);
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);
		
		int yearParam = year == 0 ? currentYear : year;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, monthIndex);
		cal.set(Calendar.YEAR, yearParam);
		
		int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		cal.set(Calendar.DAY_OF_MONTH, 1);		
		int weekDayOfFirstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
		
		int lenOfDayOfMonthStr = 4;

		StringBuffer sb = new StringBuffer();
		int count = 0;
		for(int i = 1 ; i < weekDayOfFirstDayOfMonth; i++) {
			sb.append(StrUtil.extend(" ", lenOfDayOfMonthStr));
			count++;
		}

		int dayOfMonth = 1;
		while(dayOfMonth <= maxDays) {
			String tempStr;
			
			if(currentMonthIndex == monthIndex && currentDay == dayOfMonth && yearParam == currentYear) {
				tempStr = "*" + dayOfMonth + " ";
			} else {
				tempStr = " " + dayOfMonth + " ";
			}
			String decoratedDayOfMonth = StrUtil.extendLeftward(tempStr + "", 4);
			sb.append(decoratedDayOfMonth);
			
			count++;
			
			if(count % DAYS_OF_WEEK == 0 && dayOfMonth != maxDays) {
				records.add(sb.toString());
				sb.setLength(0);
			}
			
			dayOfMonth++;
		}
		if(sb.length() != 0) {
			records.add(sb.toString());			
		}
	}
}
