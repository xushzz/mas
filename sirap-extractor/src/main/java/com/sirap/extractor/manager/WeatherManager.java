package com.sirap.extractor.manager;

import java.util.HashMap;
import java.util.List;

import com.sirap.basic.search.MexFilter;
import com.sirap.common.domain.WeatherRecord;
import com.sirap.extractor.impl.NationalWeatherExtractor;

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
	
	public List<WeatherRecord> allRecords() {
		NationalWeatherExtractor yoko = new NationalWeatherExtractor();
		yoko.process();
		List<WeatherRecord> records = yoko.getItems();
		
		return records;
	}
	
	public List<WeatherRecord> search(String criteria) {
		String cityName = CITIES.get(criteria);
		if(cityName == null) {
			cityName = criteria;
		}
		
		MexFilter<WeatherRecord> filter = new MexFilter<WeatherRecord>(cityName, allRecords());
		List<WeatherRecord> result = filter.process();	
		
		return result;
	}
}
