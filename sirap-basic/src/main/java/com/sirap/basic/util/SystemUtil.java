package com.sirap.basic.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.TypedKeyValueItem;

public class SystemUtil {
	public static List<TypedKeyValueItem> systemPropertiesAndEnvironmentVaribables() {
		List<TypedKeyValueItem> items = Lists.newArrayList();
		items.addAll(systemProperties());
		items.addAll(environmentVaribables());
		
		return items;
	}
	
	public static List<TypedKeyValueItem> systemProperties() {
		Properties sato = System.getProperties();
		Enumeration<?> en = sato.propertyNames();
		List<TypedKeyValueItem> items = Lists.newArrayList();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = sato.getProperty(key);
			TypedKeyValueItem item = new TypedKeyValueItem(key, value);
			item.setType("System Property");
			items.add(item);
		}
		
		return items;
	}

	public static List<TypedKeyValueItem> environmentVaribables() {
		Map<String,String> sato = System.getenv();
		Iterator<String> it = sato.keySet().iterator();
		List<TypedKeyValueItem> items = Lists.newArrayList();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = sato.get(key);
			TypedKeyValueItem item = new TypedKeyValueItem(key, value);
			item.setType("Environment Variable");
			items.add(item);
		}
		
		return items;
	}
}
