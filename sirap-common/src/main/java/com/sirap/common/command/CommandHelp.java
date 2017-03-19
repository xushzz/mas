package com.sirap.common.command;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;

public class CommandHelp extends CommandBase {

	private static final String KEY_GUEST = "Guest";
	private static final String KEY_EMAIL = "Email";
	private static final String KEY_TASK = "Task";
	private static final String TEMPLATE_HELP = "/help/Help_{0}.txt";
	private static final String TEMPLATE_HELP_XX = "/help/Help_{0}_{1}.txt";
	
	public boolean handle() {
		
		String singleParam = parseParam("[?|'](.*?)");
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
					results.addAll(items);
				}
			}

			if(!EmptyUtil.isNullOrEmpty(results)) {
				if(!singleParam.isEmpty()) {
					List<MexedObject> items = filter(results, singleParam);
					results = CollectionUtil.items2PrintRecords(items);
				}
				results.add("");
				results.add(versionAndCopyright());
				export(results);
//				if(target instanceof TargetPDF) {
//					int[] cellsWidth = {1, 5};
//					int[] cellsAlign = {0, 0};
//					PDFParams pdfParams = new PDFParams(cellsWidth);
//					pdfParams.setCellsAlign(cellsAlign);
//					target.setParams(pdfParams);
//					List<List<String>> records = new ArrayList<>();
//					for(String record:results) {
//						records.add(splitHelpInfo(record));
//					}
//					export(records);
//				} else {
//					export(results);
//				}
			}
			
			return true;
		}
		
		return false;
	}
	
//	private List<String> splitHelpInfo(String record) {
//		List<String> list = new ArrayList<>();
//		int index = record.indexOf("  ");
//		if(index < 0) {
//			list.add("");
//			list.add(record);
//		} else {
//			String item = record.substring(0, index).trim();
//			String more = record.substring(index).trim();
//			list.add(item);
//			list.add(more);
//		}
//		
//		return list;
//	}
	
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
	
	private List<MexedObject> filter(List<String> records, String criteria) {
		MexFilter<MexedObject> filter = new MexFilter<MexedObject>(criteria, CollectionUtil.toMexedObjects(records));
		List<MexedObject> items = filter.process();
		
		return items;
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
