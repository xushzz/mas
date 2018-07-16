package com.sirap.basic.component.map;

import java.util.LinkedHashMap;
import java.util.List;

import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.XXXUtil;

public class AlinkMap<K, V> extends LinkedHashMap<K,V> {

	private static final long serialVersionUID = 1L;
	
	public V put(K key, V value) {
		XXXUtil.checkDuplication(this, key, value);
		return super.put(key, value);
	}
	
	public V getIgnorecase(K targetKey) {
		return Amaps.getIgnorecase(this, targetKey);
	}
	
	public V getX(K key, V ifnull) {
		V value = Amaps.getIgnorecase(this, key);
		if(value == null) {
			return ifnull;
		} else {
			return value;
		}
	}
	
	public V getX(K targetKey) {
		V value = Amaps.getIgnorecase(this, targetKey);
		XXXUtil.nullCheck(value, "Doesn't contain key: " + targetKey);
		return value;
	}
	
	public List<V> getValuesBySomeKeyword(String keyword) {
		return Amaps.getValuesBySomeKeyword(this, keyword);
	}
}