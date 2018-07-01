package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sirap.basic.component.map.AconcMap;
import com.sirap.basic.component.map.AhashMap;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.component.map.AtreeMap;

public class Amaps {

	public static <K, V> AhashMap<K, V> newHashMap() {
		return new AhashMap<K, V>();
	}

	public static <K, V> AtreeMap<K, V> newTreeMap() {
		return new AtreeMap<K, V>();
	}

	public static <K, V> AlinkMap<K, V> newLinkHashMap() {
		return new AlinkMap<K, V>();
	}

	public static <K, V> AconcMap<K, V> newConcurrentHashMap() {
		return new AconcMap<K, V>();
	}
	

	
	public static <K, V> List<String> listOf(Map<K, V> map) {
		return listOf(map, " = ");
	}
	
	public static <K, V> List<String> listOf(Map<K, V> map, String connector) {
		List<String> list = new ArrayList<>();
		Iterator<K> it = map.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			list.add(key + connector + map.get(key));
		}

		return list;
	}

	public static <K, V> V getIgnorecase(Map<K, V> map, K targetKey) {
		Iterator<K> it = map.keySet().iterator();
		while(it.hasNext()) {
			K key = it.next();
			V value = map.get(key);
			if(StrUtil.equals(key.toString(), targetKey.toString())) {
				return value;
			}
		}
		
		return null;
	}
}
