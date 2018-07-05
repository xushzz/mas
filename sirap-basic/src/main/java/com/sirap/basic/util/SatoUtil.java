package com.sirap.basic.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;

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
			List<TypedKeyValueItem> sato = Colls.filter(items, criteria);
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
		String propertyName = Konstants.JAVA_DOT_LIBRARY_DOT_PATH;
		return kidOfSystemProperty(propertyName, keyword);
	}
	
	public static String kidOfJavaClassPath(String keyword) {
		String propertyName = Konstants.JAVA_DOT_CLASS_DOT_PATH;
		return kidOfSystemProperty(propertyName, keyword);
	}
	
	public static String kidOfSystemProperty(String propertyName, String keyword) {
//		D.pl(propertyName);
		String path = System.getProperty(propertyName);
		List<String> items = StrUtil.split(path, File.pathSeparator);
//		D.list(items);
		List<MexItem> desire = Colls.filterMix(items, keyword, false);
		if(desire.isEmpty()) {
			C.list(items);
			XXXUtil.info("None of entries of {0} contains {1}.", propertyName, keyword);
			return null;
		} else if(desire.size() > 1) {
			C.list(desire);
			XXXUtil.alert("More than one entry of {0} contains {1}.", propertyName, keyword);
		}
		
		return desire.get(0).toString();
	}
	
	public static Map<String, String> allExplorers() {
		//C:\\Program Files
		//C:\\Program Files(x86)
		String programfiles = System.getenv("ProgramFiles");
		String programx86 = System.getenv("ProgramFiles(x86)");
		String[] folders = {programx86, programfiles};
		String chrome = "{0}\\Google\\Chrome\\Application\\chrome.exe";
		String ie = "{0}\\internet explorer\\iexplore.exe";
		String firefox = "{0}\\Mozilla Firefox\\firefox.exe";
		String maxthon = "{0}\\Maxthon5\\Bin\\Maxthon.exe";
		String[] files = {chrome, firefox, ie, maxthon};
		Multimap<String, String> mmap = LinkedListMultimap.create();
		for(String folder : folders) {
			for(String file : files) {
				String path = StrUtil.occupy(file, folder);
				D.pl(path);
				if(FileUtil.exists(path)) {
					String exe = FileUtil.extractFilenameWithoutExtension(path);
					if(StrUtil.equals("iexplore", exe)) {
						exe = "ie";
					}
					mmap.put(exe.toLowerCase(), path);
				}
			}
		}
		
		return Amaps.fromMultiMap(mmap);
	}
}
