package com.sirap.common.framework;

import java.util.LinkedHashMap;
import java.util.Map;

public class Stash {
	public static final String KEY_USER_INPUT_TARGET = "userInputTarget";
	public static final String KEY_START_IN_MILLIS = "startInMillis";
	
	private Stash() {
		
	}
	
	public static Stash instanace;
	
	private static class Holder {
		private static Stash instance = new Stash();
	}
	
	public static Stash g() {
		return Holder.instance;
	}
	
	private Map<Object, Object> map = new LinkedHashMap<>();
	
	public void place(Object key, Object value) {
		map.put(key, value);
	}
	
	public Object readAndRemove(Object key) {
		Object value = map.get(key);
		map.remove(key);
		
		return value;
	}
	
	public Object read(Object key) {
		Object value = map.get(key);
		
		return value;
	}
}
