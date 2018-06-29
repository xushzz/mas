package com.sirap.common.command.explicit;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.component.MatrixCalendar;
import com.sirap.basic.component.RioCalendar;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.NetworkUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.TzoneUtil;
import com.sirap.basic.util.WebReader;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.extractor.CommonExtractors;

public class CommandTime extends CommandBase {
	private static final String KEY_CALENDAR = "ca";
	private static final String KEY_DATETIME = "d";
	private static final String KEY_TIMEZONE = "z";
	private static final String KEY_TO_DATE = "td";
	private static final String KEY_TO_LONG = "tl";

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
		
		if(is(KEY_DATETIME)) {
			export(DateUtil.strOf(new Date(), DateUtil.GMT2, null, g().getLocale()));
			
    		return true;
		}

		if(is(KEY_DATETIME + "u")) {
			String tzone = g().getUserValueOf("user.timezone");
			if(tzone == null) {
				C.pl2("User timezone unavailable, please check key: " + "user.timezone");
			} else {
				export(DateUtil.strOf(new Date(), DateUtil.GMT2, tzone, g().getLocale()));
			}
    		
			return true;
		}
		
		if(is(KEY_DATETIME + KEY_DOT)) {
			String site = g().getUserValueOf("time.site.top", DateUtil.NTP_SITE);
			dealwithInternetTime(site);
			
			return true;
		}
		
		solo = parseParam(KEY_DATETIME + "\\s+(.+)");
		if(solo != null) {
			dealwithInternetTime(solo);
			
    		return true;
		}
		
		if(is(KEY_DATETIME + KEY_2DOTS)) {
			Date now = new Date();
			List<String> sites = g().getUserValuesByKeyword("time.site.");
			Map<String, Date> dates = CommonExtractors.internetTimes(sites);
			dates.put("localhost " + NetworkUtil.getLocalhostIp(), now);
			Iterator<String> it = dates.keySet().iterator();
			List<String> items = Lists.newArrayList();
			while(it.hasNext()) {
				String key = it.next();
				String datestr = DateUtil.strOf(dates.get(key), DateUtil.GMT, 0, g().getLocale());
				items.add(datestr + " => " + key);
			}

			Collections.sort(items);
			export(items);
			
			return true;
		}
		
		if(is(KEY_TIMEZONE + KEY_2DOTS)) {
			dealwithTimezone("");
			
			return true;
		}
		
		solo = parseParam(KEY_TIMEZONE + "\\s+(.+?)");
		if(solo != null) {
			dealwithTimezone(solo);

			return true;
		}
		
		solo = parseParam(KEY_TO_DATE + "\\.(-?\\d{1,14})");
		if(solo != null) {
			Long millis = Long.parseLong(solo);
			List<String> items = new ArrayList<>();
			String timezone = OptionUtil.readString(options, "tz");
			Object[] zones;
			if(timezone != null) {
				zones = new Object[]{timezone};
			} else {
				zones = new Object[]{ZoneId.systemDefault().toString(), "GMT"};
			}

			for(Object zone : zones) {
				String x2 = DateUtil.strOf(millis, DateUtil.GMT2, zone, g().getLocale());
				String t17 = DateUtil.strOf(millis, DateUtil.TIGHT17, zone, g().getLocale());
				t17 += " $tz=" + zone;
				items.add(x2 + " #tl." + t17);
			}
			items.add(DateUtil.infoNow(g().getLocale()));
			
			export(items);
			
			return true;
		}
		
		if(is(KEY_TO_LONG)) {
			List<String> items = Lists.newArrayList();
			Date now = new Date();
			String template = "{0} milliseconds";
			items.add(StrUtil.occupy(template, now.getTime()));
			items.add(DateUtil.info(now, g().getLocale()));
			export(items);
			
			return true;
		}
		
		solo = parseParam(KEY_TO_LONG + "\\.(\\d{4,17})");
		if(solo != null) {
			List<Object> items = Lists.newArrayList();
			String timezone = OptionUtil.readString(options, "tz");
			Object[] zones;
			if(timezone != null) {
				zones = new Object[]{timezone};
			} else {
				zones = new Object[]{ZoneId.systemDefault().toString(), "GMT"};
			}
			
			for(Object zone : zones) {
				Date date = DateUtil.dateOfTight17(solo, zone);
				String msec = date.getTime() + " milliseconds $tz=" + zone;
				items.add(msec);
			}
			
			items.add(DateUtil.infoNow(g().getLocale()));

			export(items);
			
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
	
	private void dealwithTimezone(String criteria) {
		boolean showOffset = OptionUtil.readBooleanPRI(options, "o", true);
		boolean showDatetime = OptionUtil.readBooleanPRI(options, "d", false);
		String locales = OptionUtil.readString(options, "l");
		List<List> data = TzoneUtil.namesAndMore(criteria, locales, showOffset, showDatetime);
		exportMatrix(data, "c=#s2");
	}
	
	private void dealwithInternetTime(String site) {
		Date now = new Date();
		List<List> data = Lists.newArrayList();
		Date date = WebReader.dateOfWebsite(site);
		
		data.add(Lists.newArrayList("Internet time", DateUtil.strOf(date, DateUtil.GMT2, 0, locale())));
		
		String tzone = g().getUserValueOf("user.timezone");
		if(tzone != null) {
			data.add(Lists.newArrayList("User time", DateUtil.strOf(now, DateUtil.GMT2, tzone, locale())));
		}
		data.add(Lists.newArrayList("Local time", DateUtil.strOf(now, DateUtil.GMT2, null, locale())));
		exportMatrix(data, "c=:#s");
	}
}
