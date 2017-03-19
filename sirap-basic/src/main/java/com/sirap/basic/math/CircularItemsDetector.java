package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CircularItemsDetector {
	
	private Map<String, String> items;
	
	public CircularItemsDetector(Map<String, String> items) {
		this.items = items;	
	}
	
	public List<String> detect() {
		List<String> entries = new ArrayList<>();	
		
		Iterator<String> it = items.keySet().iterator();
		while(it.hasNext()) {
			String left = it.next();
			List<String> keys = new ArrayList<>();	
			boolean contains = crusade(left, items, keys, entries);
			if(contains) {
				return entries;
			}
		}
		
		return entries;
	}
	
	private boolean crusade(String left, Map<String, String> items, List<String> keys, List<String> entries) {
		if(keys.contains(left)) {
			keys.add(left);
			return true;
		}
		
		keys.add(left);
		String right = items.get(left);
		if(right == null) {
			entries.clear();
			return false;
		}

		entries.add(left + "=" + right);
		return crusade(right, items, keys, entries);
	}
}
