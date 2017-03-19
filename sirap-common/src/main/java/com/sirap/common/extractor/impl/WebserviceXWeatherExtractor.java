package com.sirap.common.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.xml.XmlItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class WebserviceXWeatherExtractor extends Extractor<XmlItem> {
	
	public static final String URL_TEMPLATE = "http://www.webservicex.net/globalweather.asmx/GetWeather?CityName={0}&CountryName={1}"; 
	public static final String[] ITEM_NAMES = {"Temperature", "Location", "Time", "RelativeHumidity", "Wind", "Visibility", "DewPoint", "Pressure"}; 
	
	public WebserviceXWeatherExtractor(String cityName) {
		String param = encodeURLParam(cityName);
		String url = StrUtil.occupy(URL_TEMPLATE, param, "china");
		printFetching = true;
		setUrl(url);
		printExceptionIfNeeded = false;
	}
	
	private String record;
	
	public String getRecord() {
		return record;
	}

	public static void main(String[] args) {
		String kw = "Japan";
		kw = "changchun";
		WebserviceXWeatherExtractor frank = new WebserviceXWeatherExtractor(kw);
		frank.process();
		C.list(CollectionUtil.items2PrintRecords(frank.getMexItems()));
	}

	@Override
	protected void parseContent() {
		StringBuffer regex = new StringBuffer();
		regex.append("&lt;CurrentWeather&gt;");
		regex.append("(.+?)");
		regex.append("&lt;/CurrentWeather&gt;");
		Matcher m = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);
		
		while(m.find()) {
			String content = m.group(1);
			List<XmlItem> items = readItemsByMultipleTagNames(content, ITEM_NAMES);
			mexItems.addAll(items);
		}
	}
	
	public List<XmlItem> readItemsByMultipleTagNames(String source, String... tagNames) {
		List<XmlItem> list = new ArrayList<>();
		for(String tagName : tagNames) {
			List<XmlItem> items = readItemsByTagName(source, tagName);
			if(EmptyUtil.isNullOrEmpty(items)) {
				continue;
			}
			
			list.addAll(items);
		}
		
		return list;
	}

	public List<XmlItem> readItemsByTagName(String source, String tagName) {
		Matcher m = createDefaultMatcher(source, tagName);
		
		List<XmlItem> items = new ArrayList<>();
		
		while(m.find()) {
			String value = m.group(1);
			XmlItem item = new XmlItem(tagName, value);
			items.add(item);
		}
		
		return items;
	}

	public XmlItem readFirstMatchedItemByTagName(String source, String tagName) {
		Matcher m = createDefaultMatcher(source, tagName);
		
		if(m.find()) {
			String value = m.group(1);
			XmlItem item = new XmlItem(tagName, value);

			return item;
		}
		
		return null;
	}
	
	private Matcher createDefaultMatcher(String source, String tagName) {
		String regex = "&lt;" + tagName + "&gt;([^<>]*?)&lt;/" + tagName + "&gt;";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		
		return m;
	}
}
