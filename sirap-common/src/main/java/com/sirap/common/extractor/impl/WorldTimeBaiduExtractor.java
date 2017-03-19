package com.sirap.common.extractor.impl;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;

/**
 * @deprecated been replcaed by WorldTimeBJTimeO
 * @author Administrator
 *
 */
public class WorldTimeBaiduExtractor extends WorldTimeExtractor {
	
	public static final String URL_TIME = "http://open.baidu.com/special/time/"; 
	private static final long BEIJING_TZ_OFFSET_MILLIS = 8 * Konstants.MILLI_PER_HOUR;

	public static void main(String[] args) {
		WorldTimeExtractor frank = new WorldTimeBaiduExtractor();
		frank.process();
		C.pl(frank.datetime);
	}

	@Override
	protected void parseContent() {
		StringBuffer regex = new StringBuffer();
		regex.append("window.baidu_time\\((\\d+)\\)");
		Matcher m = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);
		if(m.find()) {
			Long beijingTimeMilliSecondsSince1970 = Long.parseLong(m.group(1));
			long gmtMilliSecondsSince1970 = beijingTimeMilliSecondsSince1970 - BEIJING_TZ_OFFSET_MILLIS;
			datetime = new Date(gmtMilliSecondsSince1970);
		}
	}

	@Override
	public String getUrl() {
		return URL_TIME;
	}
}
