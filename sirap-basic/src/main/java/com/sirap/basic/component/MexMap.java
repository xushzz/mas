package com.sirap.basic.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.TrumpUtil;
import com.sirap.basic.util.XXXUtil;

public class MexMap {
	
	private Map<String, String> container = Maps.newConcurrentMap();
	private String type;
	
	public String getType() {
		return type;
	}

	public MexMap() {}
	
	public MexMap(String type) {
		this.type = type;
	}
	
	public List<TypedKeyValueItem> listOf() {
		List<TypedKeyValueItem> items = Lists.newArrayList();
		Iterator<String> it = container.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = container.get(key);
			TypedKeyValueItem item = new TypedKeyValueItem(key, value);
			item.setType(type);
			items.add(item);
		}
		
		return items;
	}
	
	public void recoverValues(String passcode) {
		Iterator<String> it = container.keySet().iterator();
		List<TypedKeyValueItem> satos = SatoUtil.systemPropertiesAndEnvironmentVaribables();
		while(it.hasNext()) {
			String key = it.next();
			String origin = getIgnorecase(key);
			String temp = TrumpUtil.decodeMixedTextBySIRAP(origin, passcode);
			String finale = SatoUtil.occupyCoins(temp, satos);
			container.put(key, finale);
		}
	}
	
	public MexMap(Map<String, String> container) {
		if(container != null) {
			this.container = container;
		}
	}

	public void put(String key, String value) {
		container.put(key, value);
	}
	
	public TypedKeyValueItem getTypedItem(String key) {
		String criteria = "^" + key + "$";
		List<TypedKeyValueItem> sato = CollUtil.filter(listOf(), criteria);
		if(EmptyUtil.isNullOrEmpty(sato)) {
			return null;
		}
		if(sato.size() > 1) {
			String msg = "More than one matched items for '{0}':\n{1}";
			XXXUtil.alert(msg, key, StrUtil.connectWithLineSeparator(sato));
		}
		
		return sato.get(0);
	}

	public String getIgnorecase(String key) {
		TypedKeyValueItem item = getTypedItem(key);
		if(item != null) {
			return item.getValueX();
		} else {
			return null;
		}
	}
	
	public TypedKeyValueItem getEntryIgnorecase(String key) {
		String criteria = "^" + key + "$";
		List<TypedKeyValueItem> sato = CollUtil.filter(listOf(), criteria);
		if(EmptyUtil.isNullOrEmpty(sato)) {
			return null;
		}
		if(sato.size() > 1) {
			String msg = "More than one matched items for '{0}':\n{1}";
			XXXUtil.alert(msg, key, StrUtil.connectWithLineSeparator(sato));
		}
		
		return sato.get(0);
	}
	
	public int getNumber(String key) {
		return getNumber(key, 0);
	}
	
	public int getNumber(String key, int defaultIfNull) {
		String strValue = getIgnorecase(key);
		if(strValue == null) {
			return defaultIfNull;
		} else {
			Integer value = MathUtil.toInteger(strValue.trim());
			return value != null ? value : defaultIfNull;
		}
	}

	public String get(String key, String defaultIfNull) {
		String value = getIgnorecase(key);
		return value != null ? value.trim() : defaultIfNull;
	}
	
	public void clear() {
		container.clear();
	}
	
	public void putAll(MexMap another) {
		container.putAll(another.container);
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
			String value = getIgnorecase(key);
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
			String value = getIgnorecase(key);
			entries.add(key + "=" + value);
		}
		
		Collections.sort(entries);
		return entries;
	}
	
	public List<TypedKeyValueItem> detectCircularItems() {
		for(TypedKeyValueItem item : listOf()) {
			List<TypedKeyValueItem> items = check(item.getKey(), Lists.newArrayList(), Lists.newArrayList());
			if(!EmptyUtil.isNullOrEmpty(items)) {
				return items;
			}
		}
		
		return null;
	}
	
	private List<TypedKeyValueItem> check(String key, List<String> keys, List<TypedKeyValueItem> entries) {
		if(keys.contains(key.toLowerCase())) {
			return entries;
		}
		TypedKeyValueItem item = getTypedItem(key);
		if(item == null) {
			return null;
		}

//		D.pl(key, keys, item, entries);
		keys.add(key.toLowerCase());
		entries.add(item);
		return check(item.getValueX(), keys, entries);
	}
}
