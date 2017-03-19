package com.sirap.common.manager;

import java.util.HashMap;
import java.util.List;

import com.sirap.basic.domain.xml.XmlItem;
import com.sirap.common.extractor.impl.WebserviceXWeatherExtractor;

public class WeatherManager {

	private static WeatherManager instance;
	
	public static final HashMap<String, String> CITIES = new HashMap<String, String>();
	static {
		CITIES.put("sz", "shenzhen");
		CITIES.put("gz", "guangzhou");
		CITIES.put("bj", "beijing");
		CITIES.put("cd", "chengdu");
		CITIES.put("sh", "shanghai");
		CITIES.put("dl", "dalian");
	}
	
	public static WeatherManager g() {
		if(instance == null) {
			instance = new WeatherManager();
		}
		
		return instance;
	}
	
	public List<XmlItem> search(String criteria) {
		String cityName = CITIES.get(criteria);
		if(cityName == null) {
			cityName = criteria;
		}
		WebserviceXWeatherExtractor yoko = new WebserviceXWeatherExtractor(cityName);
		yoko.process();
		List<XmlItem> records = yoko.getMexItems();
		
		return records;
	}
}
