package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
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

	public static <K, V> V getFirst(Map<K, V> map) {
		Iterator<K> it = map.keySet().iterator();
		while(it.hasNext()) {
			K key = it.next();
			V value = map.get(key);
			return value;
		}
		
		return null;
	}
	
	public static <K, V> Map<String, V> fromMultiMap(Multimap<K, V> mmap) {
		Iterator<K> it = mmap.keySet().iterator();
		Map<String, V> map = null;
		if(LinkedListMultimap.class.isInstance(mmap)) {
			map = newLinkHashMap();
		} else if(TreeMultimap.class.isInstance(mmap)) {
			map = newTreeMap();
		} else {
			map = newHashMap();
		}
		while(it.hasNext()) {
			K key = it.next();
			List<V> vs = Lists.newArrayList(mmap.get(key));
			int count = 1;
			for(V value : vs) {
				if(count == 1) {
					map.put(key.toString(), value);
				} else {
					map.put(key.toString() + count, value);
				}
				count++;
			}
		}
		
		return map;
	}
	
	public static AlinkMap<String, String> ofProperties(List<String> props) {
		AlinkMap<String, String> ali = newLinkHashMap();
		
		for (String line : props) {
			String temp = line.trim();
			
			boolean toIgnore = StrUtil.startsWith(temp, "#");
			if(toIgnore) {
				continue;
			}
			
			String regex = "([^=]+)=(.+)";
			String[] params = StrUtil.parseParams(regex, temp);
			if(params == null) {
				continue;
			}
			
			String key = params[0].trim();
			String value = params[1].trim();
			
			if(key.isEmpty() || value.isEmpty()) {
				continue;
			}
			ali.put(key, value);
		}
		
		return ali;
	}
	
	public static <K, V> List<V> getValuesBySomeKeyword(Map<K, V> map, String keyword) {
		List<V> items = Lists.newArrayList();
		Iterator<K> it = map.keySet().iterator();
		while(it.hasNext()) {
			K key = it.next();
			String temp = key.toString();
			if(StrUtil.contains(temp, keyword)) {
				items.add(map.get(key));
			}
		}
		
		return items;
	}
	
	public static <K, V> Map<K, V> createMap(K key, V value) {
		Map<K, V> map = new HashMap<>();
		map.put(key, value);
		
		return map;
	}
}
