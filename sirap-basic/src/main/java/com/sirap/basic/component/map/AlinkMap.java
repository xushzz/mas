package com.sirap.basic.component.map;

import java.util.LinkedHashMap;

import com.sirap.basic.util.Amaps;
import com.sirap.basic.util.XXXUtil;

public class AlinkMap<K, V> extends LinkedHashMap<K,V> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public V put(K key, V value) {
		XXXUtil.checkDuplication(this, key);
		return super.put(key, value);
	}
	
	public V getIgnorecase(K targetKey) {
		return Amaps.getIgnorecase(this, targetKey);
	}
}
