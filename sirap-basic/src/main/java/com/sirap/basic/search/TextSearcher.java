package com.sirap.basic.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexTextSearchRecord;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class TextSearcher {
	
//	private static Map<String, TextSearcher> instances = new HashMap<>();
	private List<MexTextSearchRecord> allItems;
	
	private TextSearcher(List<String> folders, String fileNameCriteria) {
		allItems = readAllItems(folders, fileNameCriteria);
	}
//	
//	private synchronized static TextSearcher getInstance(String engineName, List<String> folders, String fileNameCriteria, String charset) {
//		TextSearcher wang = instances.get(engineName);
//		if(wang == null) {
//			wang = new TextSearcher(folders, fileNameCriteria, charset);
//			instances.put(engineName, wang);
//		}
//		
//		return wang;
//	}
	
	public static List<MexTextSearchRecord> search(String foldersStr, String fileNameCriteria, String criteria) {
		List<String> folders = StrUtil.splitByRegex(foldersStr);
		return search(folders, fileNameCriteria, criteria);
	}
	
	public static List<MexTextSearchRecord> search(List<String> folders, String fileNameCriteria, String criteria) {
		TextSearcher wang = new TextSearcher(folders, fileNameCriteria);
		List<MexTextSearchRecord> result = CollUtil.filter(wang.allItems, criteria);
		
		return result;
	}
	
//	@Deprecated
//	public static List<MexObject> searchWithCache(String engineName, String foldersStr, String fileNameCriteria, String contentCriteria, String charset) {
//		List<String> folders = StrUtil.splitByRegex(foldersStr);
//		return searchWithCache(engineName, folders, fileNameCriteria, contentCriteria, charset);
//	}
//	
//	public static List<MexTextSearchRecord> searchWithCache(String engineName, List<String> folders, String fileNameCriteria, String contentCriteria, String charset) {
//		TextSearcher wang = getInstance(engineName, folders, fileNameCriteria, charset);
//		List<MexObject> result = CollectionUtil.filter(wang.allItems, contentCriteria);
//		
//		return result;
//	}
	
	private List<MexTextSearchRecord> readAllItems(List<String> folders, String fileCriteria) {
		List<MexFile> allMexFiles = FileUtil.scanFolders(folders, false, fileCriteria);
		
		List<MexTextSearchRecord> allItems = new ArrayList<>();
		for(MexFile file : allMexFiles) {
			String fileName = file.getPath();
			List<MexTextSearchRecord> items = readEachFile(fileName);
			allItems.addAll(items);
		}
		
		return allItems;
	}
	
	@SuppressWarnings("unchecked")
	private List<MexTextSearchRecord> readEachFile(String filename) {
		if(!FileUtil.isNormalFile(filename)) {
			return Collections.EMPTY_LIST;
		}
		
		List<String> items = IOUtil.readFileIntoList(filename);
		
		List<MexTextSearchRecord> mexItems = new ArrayList<>();
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			MexTextSearchRecord mo = new MexTextSearchRecord(item.trim());
			mo.setLineNumber(i + 1);
			mo.setFullFilename(filename);
			
			mexItems.add(mo);
		}
		
		return mexItems;
	}
}
