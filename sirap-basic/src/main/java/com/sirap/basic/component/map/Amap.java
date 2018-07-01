package com.sirap.basic.component.map;

import java.util.Map;

public interface Amap<K, V> extends Map<K, V> {
	public V getIgnorecase(K targetKey);
}
