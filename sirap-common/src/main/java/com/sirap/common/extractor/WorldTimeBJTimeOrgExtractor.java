package com.sirap.common.extractor;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.DateUtil;

public class WorldTimeBJTimeOrgExtractor extends WorldTimeExtractor {
	
	public static final String URL_TIME = "http://www.beijing-time.org/time15.asp"; 

	/***
	 * The critical info for this regular expression
	 * nyear=2016; nmonth=5; nday=28; nwday=6; nhrs=9; nmin=33; nsec=29;
	 * August says 8.
	 */
	@Override
	protected void parseContent() {
		StringBuffer regex = new StringBuffer();
		
		regex.append("nyear=([\\d]{4});nmonth=([\\d]{1,2});nday=([\\d]{1,2});[^;]+;nhrs=([\\d]{1,2});nmin=([\\d]{1,2});nsec=([\\d]{1,2})");
		Matcher m = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);
		if(m.find()) {
			String year = m.group(1);
			String month = m.group(2);
			String day = m.group(3);
			String hour = m.group(4);
			String minute = m.group(5);
			String second = m.group(6);
			Date bjTime = DateUtil.construct(year, month, day, hour, minute, second);
			datetime = DateUtil.add(bjTime, Calendar.HOUR_OF_DAY, -8);
		}
	}

	@Override
	public String getUrl() {
		printFetching = true;
		return URL_TIME;
	}
}
