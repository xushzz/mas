package com.sirap.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;

public class CommandHelp extends CommandBase {

	private static final String KEY_BASIC = "Basic";
	private static final String KEY_GUEST = "Guest";
	private static final String KEY_EMAIL = "Email";
	private static final String KEY_TASK = "Task";
	private static final String TEMPLATE_HELP = "/help/Help_{0}.txt";
	private static final String TEMPLATE_HELP_XX = "/help/Help_{0}_{1}.txt";
	
	@Override
	public boolean handle() {
		
		solo = parseSoloParam("[?|'](.*?)");
		if(solo != null) {
			List<String> allKeys = new ArrayList<>();
			allKeys.add(KEY_GUEST);
			
			String temp = g().getValueOf("help.keys");
			List<String> keys = StrUtil.splitByRegex(temp);
			if(keys.isEmpty()) {
				keys = getCommandNames();
			}
			for(String key : keys) {
				if(EmptyUtil.isNullOrEmptyOrBlank(key)) {
					continue;
				}
				
				String tempName = getHelpFileName(key);
				if(tempName != null) {
					allKeys.add(key);
				}
			}
			
			if(isEmailEnabled()) {
				allKeys.add(KEY_EMAIL);
 			}
			allKeys.add(KEY_TASK);
			allKeys.add(KEY_BASIC);
			
			List<String> results = new ArrayList<>();
			int maxLen = StrUtil.maxLengthOf(allKeys) + 2;
			Map<String, Object> allHelpMeanings = getAllHelpMeanings();
			for(String key : allKeys) {
				String fileName = getHelpFileName(key);
				String prefix = StrUtil.padLeft(key, maxLen, " ");
				List<String> items = FileUtil.readResourceFilesIntoList(fileName, prefix);
				if(!EmptyUtil.isNullOrEmpty(items)) {
					items = occupyDollarKeys(items, allHelpMeanings);
					results.addAll(items);
				}
			}

			if(!EmptyUtil.isNullOrEmpty(results)) {
				if(!solo.isEmpty()) {
					List<MexObject> items = CollectionUtil.search(results, solo);
					results = CollectionUtil.items2PrintRecords(items);
				}
				results.add("");
				results.add(versionAndCopyright());
				export(results);
			}
			
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
			} else {
				C.pl(StrUtil.occupy("Help file for [{0}] doesn't exist.", key));
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
}
