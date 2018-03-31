package com.sirap.common.command.explicit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sirap.basic.component.MatrixCalendar;
import com.sirap.basic.component.RioCalendar;
import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.domain.TZRecord;
import com.sirap.common.framework.command.target.TargetPDF;
import com.sirap.common.manager.TimeZoneManager;

public class CommandTime extends CommandBase {
	//group text search
	//text file search
	private static final String KEY_CALENDAR = "ca";
	private static final String KEY_DATETIME_SERVER = "d,now";
	private static final String KEY_DATETIME_USER = "du";
	private static final String KEY_TIMEZONE_DISPLAY = "z\\.(.{1,20})";

	
	public boolean handle() {
		
		if(is(KEY_CALENDAR)) {
			int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
			List<String> records = displayMonthCalendar(currentMonth, OptionUtil.readIntegerPRI(options, "d", 0));
			
			setIsPrintTotal(false);
			export(records);
			
			return true;
		}
		
		if(is(KEY_CALENDAR + KEY_2DOTS)) {
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			List<String> records = displayCalendarsOfYear(currentYear, OptionUtil.readIntegerPRI(options, "d", 0));
			
			if(records.size() > 0) {
				records.remove(records.size() - 1);
				setIsPrintTotal(false);
				export(records);
			}
			
			return true;
		}
		
		solo = parseParam(KEY_CALENDAR + "([\\d&]+)");
		if(solo != null) {
			List<String> totalRecords = new ArrayList<String>();
			List<String> yearMonthList = StrUtil.split(solo, '&');
			for(String info:yearMonthList) {
				//current year, given month
				String singleMonth = StrUtil.parseParam("(\\d{1,2})", info);
				if(singleMonth != null) {
					int month = Integer.parseInt(singleMonth);
					if(month < 1 || month > 12) {
						continue;
					}
					
					List<String> temp = displayMonthCalendar(month, OptionUtil.readIntegerPRI(options, "d", 0));
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						totalRecords.addAll(temp);
						totalRecords.add("");
					}
					
					continue;
				}
				
				//given year
				String singleYear = StrUtil.parseParam("(\\d{4})", info);
				if(singleYear != null) {
					Integer year = MathUtil.toInteger(singleYear);
					List<String> temp = displayCalendarsOfYear(year, OptionUtil.readIntegerPRI(options, "d", 0));
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						totalRecords.addAll(temp);
					}
					
					continue;
				}
				
				//given year, given month
				String[] yearMonth = StrUtil.parseParams("(\\d{4})(\\d{1,2})", info);
				if(yearMonth != null) {
					Integer year = MathUtil.toInteger(yearMonth[0]);
					Integer month = MathUtil.toInteger(yearMonth[1]);
					
					List<String> temp = displayMonthCalendar(year, month, OptionUtil.readIntegerPRI(options, "d", 0));
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						totalRecords.addAll(temp);
						totalRecords.add("");
					}
					continue;
				}
				
				//given year, given month, given day
				String[] yearMonthDay = StrUtil.parseParams("(\\d{4})(\\d{2})(\\d{1,2})", info);
				if(yearMonthDay != null) {
					Integer year = MathUtil.toInteger(yearMonthDay[0]);
					Integer month = MathUtil.toInteger(yearMonthDay[1]);
					
					Integer day = MathUtil.toInteger(yearMonthDay[2]);
					if(day == null) {
						day = OptionUtil.readIntegerPRI(options, "d", 0);
					}
					
					List<String> temp = displayMonthCalendar(year, month, day);
					if(!EmptyUtil.isNullOrEmpty(temp)) {
						totalRecords.addAll(temp);
						totalRecords.add("");
					}
					continue;
				}
			}
			
			if(totalRecords.size() > 0) {
				totalRecords.remove(totalRecords.size() - 1);
				setIsPrintTotal(false);
				export(totalRecords);
			}
			
			return true;
		}
		
		if(isIn(KEY_DATETIME_SERVER)) {
			export(DateUtil.displayDateWithGMT(new Date(), DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale()));
			
    		return true;
		}

		if(is(KEY_DATETIME_USER)) {
			Date dateUser = DateUtil.getTZRelatedDate(g().getTimeZoneUser(), new Date());
			export(DateUtil.displayDateWithGMT(dateUser, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE, g().getLocale(), g().getTimeZoneUser()));
    		
			return true;
		}
		
		solo = parseParam(KEY_TIMEZONE_DISPLAY);
		if(solo != null) {
			List<TZRecord> records = TimeZoneManager.g().getTimeZones(solo, g().getLocale(), false);
			if(target instanceof TargetPDF) {
				int[] cellsWidth = {1, 1};
				int[] cellsAlign = {0, 0};
				PDFParams pdfParams = new PDFParams(cellsWidth, cellsAlign);
				target.setParams(pdfParams);
				List<List<String>> items = CollUtil.items2PDFRecords(records);
				export(items);
			} else {
				export(records);	
			}
			
    		return true;
		}
		
		return false;
	}
	
	private List<String> displayMonthCalendar(int year, int month, int dayToMark) {
		XXXUtil.checkMonthRange(month);
		RioCalendar rioCal = new RioCalendar(year, month, dayToMark);
		rioCal.setLocale(g().getLocale());
		return rioCal.generate().getRecords();
	}
	
	private List<String> displayMonthCalendar(int month, int dayToMark) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return displayMonthCalendar(currentYear, month, dayToMark);
	}
	
	private List<String> displayCalendarsOfYear(int year, int dayToMark) {
		List<List<String>> grandList = new ArrayList<List<String>>();
		for(int m = 1; m <= 12; m++) {
			RioCalendar rioCal = new RioCalendar(year, m, dayToMark);
			List<String> records = rioCal.generate().getRecords();
			grandList.add(records);
		}

		MatrixCalendar jamie = new MatrixCalendar(grandList, MatrixCalendar.MatrixMode.THREE);
		List<String> results = jamie.getResults();
		
		return results;
	}
}
