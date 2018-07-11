package com.sirap.common.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.SimpleKonfig;

public class CommandHelp extends CommandBase {

	private static final String KEY_BASIC = "Basic";
	private static final String KEY_GUEST = "Guest";
	private static final String KEY_EMAIL = "Email";
	private static final String KEY_TASK = "Task";
	private static final String TEMPLATE_HELP = "help/Help_{0}.txt";
	private static final String TEMPLATE_HELP_XX = "help/Help_{0}_{1}.txt";
	private static final String HELP_WEB_URL = "https://raw.githubusercontent.com/acesfullmike/masrun/master/help.txt";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean handle() {
		
		solo = parseParam("[?|'](.*?)");
		if(solo != null) {
			if(OptionUtil.readBooleanPRI(options, "w", false)) {
				FileOpener.playThing(HELP_WEB_URL, "page.viewer", true);
				return true;
			}
			List<String> allKeys = new ArrayList<>();
			allKeys.add(KEY_GUEST);
			
//			String temp = g().getValueOf("help.keys");
//			List<String> keys = StrUtil.splitByRegex(temp);
//			if(keys.isEmpty()) {
//				keys = getCommandNames();
//			}
			List<String> keys = getCommandNames();
			for(String key : keys) {
				if(EmptyUtil.isNullOrEmptyOrBlank(key)) {
					continue;
				}
				allKeys.add(key);
//				String tempName = StrUtil.occupy(TEMPLATE_HELP, key);
//				String tempName = getHelpFileName(key);
//				if(tempName != null) {
//					allKeys.add(key);
//				} else {
//					D.pl("Missing help file for " + key);
//				}
			}
			
			if(isEmailEnabled()) {
				allKeys.add(KEY_EMAIL);
 			}
			allKeys.add(KEY_TASK);
			allKeys.add(KEY_BASIC);
			List lines = new ArrayList<>();
			int maxLen = StrUtil.maxLengthOf(allKeys);
			Map<String, Object> allHelpMeanings = getAllHelpMeanings();
			for(String key : allKeys) {
//				String fileName = getHelpFileName(key);
				String fileName = StrUtil.occupy(TEMPLATE_HELP, key);
				if(EmptyUtil.isNullOrEmpty(fileName)) {
					C.pl(StrUtil.occupy("Help file for [{0}] doesn't exist.", key));
					continue;
				}
				String prefix = StrUtil.padRight(key, maxLen + 2);
				InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
				List<String> records = IOUtil.readLinesFromStream(ins, g().getCharsetInUse());
				lines.addAll(occupyDollarKeys(Colls.addPrefix(records, prefix), allHelpMeanings));
			}

			if(!solo.isEmpty()) {
				lines = Colls.filterMix(lines, solo, isCaseSensitive());
				sortByVersionDateInfo(lines);
			}
			if(!EmptyUtil.isNullOrEmpty(lines)) {
				lines.add("");
			}
			lines.add(versionAndCopyright());
			export(lines);
			
			return true;
		}
		
		return false;
	}
	
	private List<String> occupyDollarKeys(List<String> results, Map<String, Object> allHelpMeanings) {
		List<String> items = new ArrayList<>();
		for(String temp : results) {
			String item = StrUtil.occupyKeyValues(temp, allHelpMeanings);
			items.add(item);
		}
		
		return items;
	}
	
	private String getHelpFileName(String key) {
		String lang = g().getLocale().toString();
		String filePath = null;
		if(!EmptyUtil.isNullOrEmpty(lang)) {
			String temp = StrUtil.occupy(TEMPLATE_HELP_XX, key, lang);
			if(IOUtil.isSourceExist(temp)) {
				filePath = temp;
			}
		}
		
		if(filePath == null) {
			String temp = StrUtil.occupy(TEMPLATE_HELP, key);
			if(IOUtil.isSourceExist(temp)) {
				filePath = temp;
			}
		}
		
		return filePath;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getAllHelpMeanings() {
		Map<String, Object> allEntries = new HashMap<>();
		List<String> items = SimpleKonfig.g().getCommandClassNames();
		String fieldName = "helpMeanings";
		for(String className : items) {
			Object obj = ObjectUtil.readFieldValue(className, fieldName);
			allEntries.putAll((Map<String, Object>)obj);
		}
		
		Object obj = ObjectUtil.readFieldValue(getClass().getName(), fieldName);
		allEntries.putAll((Map<String, Object>)obj);
		
		return allEntries;
	}
	
	//sort by modification date, such as ver=10/31/2017
	private void sortByVersionDateInfo(List<Object> lines) {

		Collections.sort(lines, new Comparator<Object>() {
			@Override
			public int compare(Object va, Object vb) {
				Date d1 = getDateInfo(va + "");
				Date d2 = getDateInfo(vb + "");
				if(d1 == null && d2 == null) {
					return 0;
				}
				
				if(d1 != null && d2 == null) {
					return 1;
				}
				
				if(d1 == null && d2 != null) {
					return -1;
				}
				
				return d1.compareTo(d2);
			}
			
			private Date getDateInfo(String line) {
				Matcher ma = StrUtil.createMatcher("=(\\d{1,2})/(\\d{1,2})/(\\d{4})", line);
				if(ma.find()) {
					Date date = DateUtil.construct(ma.group(3), ma.group(1), ma.group(2));
					return date;
				}
				
				return null;
			}
		});
	}
}
