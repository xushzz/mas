package com.sirap.extractor.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.common.domain.WeatherRecord;
import com.sirap.common.extractor.Extractor;

public class NationalWeatherExtractor extends Extractor<WeatherRecord> {
	
	public static final String URL_TEMPLATE = "http://www.nmc.cn/publish/forecast/china.html"; 
	
	public NationalWeatherExtractor() {
		printFetching = true;
		setUrl(URL_TEMPLATE);
	}
	
	@Override
	protected void parseContent() {
		StringBuffer regex = new StringBuffer();
		regex.append("<div class=\"cname\">");
		regex.append("\\s+<a target=\"_blank\" href=\"/publish/forecast/[A-Z]{1,5}/([A-Z]{1,50}).html\">([^<]+)</a>");
		regex.append("\\s+</div>");
		regex.append("\\s+<div class=\"weather\">");
		regex.append("\\s+([^<]+)");
		regex.append("\\s+</div>");
		regex.append("\\s+<div class=\"temp\">");
		regex.append("\\s+([^<]+)");
		regex.append("\\s+</div>");
		Matcher m = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);
		
		Set<String> cities = new HashSet<>();
		while(m.find()) {
			String cityPinyin = m.group(1).trim();
			if(cities.contains(cityPinyin)) {
				continue;
			}
			cities.add(cityPinyin);
			String city = m.group(2).trim();
			String weather = m.group(3).trim();
			String temper = m.group(4).trim();
			
			WeatherRecord xiu = new WeatherRecord();
			xiu.setCityPY(cityPinyin);
			xiu.setCity(city);
			xiu.setWeather(weather);
			xiu.setCelsius(temper);
			
			mexItems.add(xiu);
		}
	}
}
