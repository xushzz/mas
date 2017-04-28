package com.sirap.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.impl.WorldTimeBJTimeOrgExtractor;
import com.sirap.common.extractor.impl.XRatesForexRateExtractor;
import com.sirap.common.framework.SimpleKonfig;

public class CommandHelp extends CommandBase {

	private static final String KEY_GUEST = "Guest";
	private static final String KEY_EMAIL = "Email";
	private static final String KEY_TASK = "Task";
	private static final String TEMPLATE_HELP = "/help/Help_{0}.txt";
	private static final String TEMPLATE_HELP_XX = "/help/Help_{0}_{1}.txt";
	
	private static final Map<String, Object> DOLLAR_MEANINGS = new HashMap<>();
	
	static {
		DOLLAR_MEANINGS.put("image.formats", Konstants.IMG_FORMATS);
		DOLLAR_MEANINGS.put("guest.quits", KEY_EXIT);
		DOLLAR_MEANINGS.put("money.forex.url", XRatesForexRateExtractor.URL_X_RATES);
		DOLLAR_MEANINGS.put("timeserver.bjtimes", WorldTimeBJTimeOrgExtractor.URL_TIME);
	}
	
	public boolean handle() {
		
		singleParam = parseParam("[?|'](.*?)");
		if(singleParam != null) {
			List<String> allKeys = new ArrayList<>();
			allKeys.add(KEY_GUEST);
			
			String temp = g().getValueOf("help.keys");
			List<String> keys = StrUtil.splitByRegex(temp);
			if(keys.isEmpty()) {
				keys = generateHelpKeys();
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
			
			List<String> results = new ArrayList<>();
			int maxLen = StrUtil.maxLengthOf(allKeys) + 2;
			for(String key : allKeys) {
				String fileName = getHelpFileName(key);
				String prefix = StrUtil.extend(key, maxLen, " ");
				List<String> items = FileUtil.readResourceFilesIntoList(fileName, prefix);
				if(!EmptyUtil.isNullOrEmpty(items)) {
					items = occupyDollarKeys(items);
					results.addAll(items);
				}
			}

			if(!EmptyUtil.isNullOrEmpty(results)) {
				if(!singleParam.isEmpty()) {
					List<MexedObject> items = CollectionUtil.search(results, singleParam);
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
	
	private List<String> occupyDollarKeys(List<String> results) {
		List<String> items = new ArrayList<>();
		for(String temp : results) {
			String item = StrUtil.occupyKeyValues(temp, DOLLAR_MEANINGS);
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
	

	private List<String> generateHelpKeys() {
		List<String> keys = new ArrayList<String>();
		List<String> commandNodes = SimpleKonfig.g().getCommandNodeItems();
		for(String node:commandNodes) {
			String param = StrUtil.parseParam(".*?\\.Command([a-z]*?)$", node);
			if(param == null) {
				continue;
			}
			keys.add(param);
		}
		return keys;
	}
}
