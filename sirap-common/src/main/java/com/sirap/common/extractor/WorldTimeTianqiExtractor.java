package com.sirap.common.extractor;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.DateUtil;

public class WorldTimeTianqiExtractor extends WorldTimeExtractor {
	
	public static final String URL_TIME = "http://time.tianqi.com"; 

	@Override
	protected void parse() {
		String regex = "<meta.+?(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}).+?>";
		Matcher ma = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);
		if(ma.find()) {
			Date bjTime = DateUtil.parse(DateUtil.DATETIME, ma.group(1));
			datetime = DateUtil.add(bjTime, Calendar.HOUR_OF_DAY, -8);
		}
	}

	@Override
	public String getUrl() {
		printFetching = true;
		return URL_TIME;
	}
}
