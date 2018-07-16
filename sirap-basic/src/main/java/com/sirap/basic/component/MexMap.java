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
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.thirdparty.TrumpHelper;
import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.SatoUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MexMap {
	
	private AlinkMap<String, Object> container = Amaps.newLinkHashMap();
	private String type;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public MexMap() {}
	
	public MexMap(List<String> lines) {
		container.clear();
		AlinkMap<String, String> map = Amaps.ofProperties(lines);
		container.putAll(map);
	}
	
	public List<TypedKeyValueItem> listOf() {
		List<TypedKeyValueItem> items = Lists.newArrayList();
		Iterator<String> it = container.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Object value = container.get(key);
			TypedKeyValueItem item = new TypedKeyValueItem(key, value);
			item.setType(type);
			items.add(item);
		}
		
		return items;
	}
	
	public void recoverValues(String passcode) {
		Iterator<String> it = container.keySet().iterator();
		List<TypedKeyValueItem> satos = SatoUtil.systemPropertiesAndEnvironmentVaribables();
		Map<String, Object> tempMap = Maps.newConcurrentMap();
		while(it.hasNext()) {
			String key = it.next();
			String origin = getIgnorecase(key);
			String temp = TrumpHelper.decodeMixedTextBySIRAP(origin, passcode);
			String finale = SatoUtil.occupyCoins(temp, satos);
			tempMap.put(key, finale);
		}
		
		container.clear();
		container.putAll(tempMap);
	}
	
	public TypedKeyValueItem getTypedItem(String key) {
		String criteria = "^" + key + "$";
		List<TypedKeyValueItem> sato = Colls.filter(listOf(), criteria);
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
		List<TypedKeyValueItem> sato = Colls.filter(listOf(), criteria);
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
	
	public String get(String key) {
		String value = getIgnorecase(key);
		XXXUtil.nullCheck(value, "key: " + key);
		
		return value;
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
	
	public boolean isYes(String key) {
		boolean flag = StrUtil.equals(Konstants.FLAG_YES, getIgnorecase(key));
		return flag;
	}
	
	public boolean isNo(String key) {
		boolean flag = StrUtil.equals(Konstants.FLAG_NO, getIgnorecase(key));
		return flag;
	}
	
	public String toString() {
		return type + "\n" + StrUtil.connectWithLineSeparator(listOf());
	}
}
