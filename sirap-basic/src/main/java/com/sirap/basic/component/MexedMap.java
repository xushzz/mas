package com.sirap.basic.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.sirap.basic.math.CircularItemsDetector;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.TrumpUtil;

public class MexedMap {
	
	private Map<String, String> container = new LinkedHashMap<String, String>();
	
	public MexedMap() {
		
	}
	
	public void recoverValues(String passcode) {
		Iterator<String> it = container.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String origin = container.get(key);
			String temp = TrumpUtil.decodeMixedTextBySIRAP(origin, passcode);
			String finale = StrUtil.occupySystemPropertyOrEnvironmentVariable(temp, true);
			container.put(key, finale);
		}
	}
	
	public List<String> detectCircularItems() {
		CircularItemsDetector james = new CircularItemsDetector(container);
		return james.detect();
	}
	
	public MexedMap(Map<String, String> container) {
		if(container != null) {
			this.container = container;
		}
	}

	public void put(String key, String value) {
		container.put(key, value);
	}

	public String getReturnKeyIfNull(String key) {
		return get(key, key);
	}

	public String get(String key) {
		return container.get(key);
	}
	
	public int getNumber(String key) {
		return getNumber(key, 0);
	}
	
	public int getNumber(String key, int defaultIfNull) {
		String strValue = container.get(key);
		if(strValue == null) {
			return defaultIfNull;
		} else {
			Integer value = MathUtil.toInteger(strValue.trim());
			return value != null ? value : defaultIfNull;
		}
	}

	public String get(String key, String defaultIfNull) {
		String value = container.get(key);
		return value != null ? value.trim() : defaultIfNull;
	}

	public Map<String, String> getContainer() {
		return container;
	}
	
	public List<String> getValuesByKeyword(String keyword) {
		return getValuesByKeyword(keyword, false);
	}
	
	public List<String> getValuesByKeyword(String keyword, boolean withKey) {
		List<Map.Entry<String, String>> results = getEntriesByKeyword(keyword);
		List<String> records = new ArrayList<String>();
		for(Map.Entry<String, String> entry: results) {
			String suffix = withKey ? entry.getKey() + "=" : "";
			records.add(suffix + entry.getValue());
		}
		
		return records;
	}
	
	public HashMap<String, String> getKeyValuesByPartialKeyword(String keyword) {
		List<Map.Entry<String, String>> results = getEntriesByKeyword(keyword);
		HashMap<String, String> records = new HashMap<>();
		for(Map.Entry<String, String> entry: results) {
			records.put(entry.getKey(), entry.getValue());
		}
		
		return records;
	}
	
	private List<Map.Entry<String, String>> getEntriesByKeyword(String keyword) {
		Map<String, String> sortedMap = new TreeMap<String, String>();
		Iterator<String> it = container.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = container.get(key);
			if(StrUtil.contains(key, keyword)) {
				sortedMap.put(key, value);
			}
		}
		
		List<Map.Entry<String, String>> results = new ArrayList<Map.Entry<String, String>>(sortedMap.entrySet());
		return results;
	}

	public List<String> listEntries() {
		List<String> entries = new ArrayList<String>();
		
		Iterator<String> it = container.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = container.get(key);
			entries.add(key + "=" + value);
		}
		
		Collections.sort(entries);
		return entries;
	}
}
