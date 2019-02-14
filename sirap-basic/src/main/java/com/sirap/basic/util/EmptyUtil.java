package com.sirap.basic.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class EmptyUtil {

	@SuppressWarnings("rawtypes")
	public static boolean isNullOrEmpty(Collection col) {
		return col == null || col.isEmpty();
	}
	public static boolean isNullOrEmpty(Object[] arr) {
		return arr == null || arr.length == 0;
	}
	public static boolean isNull(Object obj) {
		return obj == null;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isNullOrEmpty(Map map) {
		return map == null || map.isEmpty();
	}

	public static boolean isNullOrEmpty(String str) {
		if(str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isNotEmpty(String str) {
		if(str == null || str.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public static List filter(List lines) {
		List items = Lists.newArrayList();
		for(Object line : lines) {
			if(line == null || isNullOrEmpty(line + "")) {
				continue;
			}
			items.add(line);
		}
		
		return items;
		
		
	}

	public static boolean isNullOrEmptyOrBlank(String str) {
		if(str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNullOrEmptyOrBlankOrLiterallyNull(String str) {
		if(str == null || str.trim().length() == 0 || str.trim().equalsIgnoreCase("null")) {
			return true;
		} else {
			return false;
		}
	}
}
