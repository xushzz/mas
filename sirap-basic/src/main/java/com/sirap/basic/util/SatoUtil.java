package com.sirap.basic.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.tool.C;

public class SatoUtil {
	
	public static final List<TypedKeyValueItem> SYSTEM_PROPERTIES = systemProperties();
	public static final List<TypedKeyValueItem> ENVIRONMENT_VARIABLES = environmentVaribables();
	
	public static List<TypedKeyValueItem> systemPropertiesAndEnvironmentVaribables() {
		List<TypedKeyValueItem> items = Lists.newArrayList();
		items.addAll(SYSTEM_PROPERTIES);
		items.addAll(ENVIRONMENT_VARIABLES);
		
		return items;
	}
	
	private static List<TypedKeyValueItem> systemProperties() {
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

	private static List<TypedKeyValueItem> environmentVaribables() {
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
	
	public static String occupyCoins(String source, List<TypedKeyValueItem> items) {
		XXXUtil.nullCheck(source, "source");
		
		String regex = "\\$\\{([^\\$\\{\\}]+)\\}";
		
		String temp = source;
		Matcher ma = StrUtil.createMatcher(regex, source);
		while(ma.find()) {
			String whole = ma.group(0);
			String key = ma.group(1);
			String criteria = "^" + key + "$";
			List<TypedKeyValueItem> sato = CollUtil.filter(items, criteria);
			if(EmptyUtil.isNullOrEmpty(sato)) {
				XXXUtil.info("No matched item for '{0}'.", key);
				continue;
			}
			if(sato.size() > 1) {
				String msg = "More than one matched items for '{0}':\n{1}\n";
				XXXUtil.alert(msg, key, StrUtil.connectWithLineSeparator(sato));
			}
			
			String value = sato.get(0).getValueX();
			temp = temp.replace(whole, value);
		}
		
		return temp;
	}
	
	public static String kidOfJavaLibPath(String keyword) {
		String propertyName = "java.library.path";
		return kidOfSystemProperty(propertyName, keyword);
	}
	
	public static String kidOfJavaClassPath(String keyword) {
		String propertyName = "java.class.path";
		return kidOfSystemProperty(propertyName, keyword);
	}
	
	public static String kidOfSystemProperty(String propertyName, String keyword) {
//		D.pl(propertyName);
		String path = System.getProperty(propertyName);
		List<String> items = StrUtil.split(path, File.pathSeparator);
//		D.list(items);
		List<MexItem> desire = CollUtil.filterMix(items, keyword, false);
		if(desire.isEmpty()) {
			C.list(items);
			XXXUtil.info("Value of {0} contains no {1}.", propertyName, keyword);
			return null;
		} else if(desire.size() > 1) {
			C.list(desire);
			XXXUtil.alert("Value of {0} contains more than one match of {1}.", propertyName, keyword);
		}
		
		return desire.get(0).toString();
	}
}
