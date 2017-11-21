package com.sirap.basic.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexTextLine;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;

public class TextSearcher {
	
//	private static Map<String, TextSearcher> instances = new HashMap<>();
	private List<MexTextLine> allItems;
	
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
	
	public static List<MexTextLine> search(String foldersStr, String fileNameCriteria, String criteria) {
		List<String> folders = StrUtil.splitByRegex(foldersStr);
		return search(folders, fileNameCriteria, criteria);
	}
	
	public static List<MexTextLine> search(List<String> folders, String fileNameCriteria, String criteria) {
		TextSearcher wang = new TextSearcher(folders, fileNameCriteria);
		List<MexTextLine> result = CollUtil.filter(wang.allItems, criteria);
		
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
	
	private List<MexTextLine> readAllItems(List<String> folders, String fileCriteria) {
		List<MexFile> allMexFiles = FileUtil.scanFolders(folders, false, fileCriteria);
		
		List<MexTextLine> allItems = new ArrayList<>();
		for(MexFile file : allMexFiles) {
			String fileName = file.getPath();
			List<MexTextLine> items = readEachFile(fileName);
			allItems.addAll(items);
		}
		
		return allItems;
	}
	
	@SuppressWarnings("unchecked")
	private List<MexTextLine> readEachFile(String filename) {
		if(!FileUtil.isNormalFile(filename)) {
			return Collections.EMPTY_LIST;
		}
		
		List<String> items = IOUtil.readFileIntoList(filename);
		
		List<MexTextLine> mexItems = new ArrayList<>();
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			MexTextLine mo = new MexTextLine(item.trim());
			mo.setLineNumber(i + 1);
			mo.setFullFilename(filename);
			
			mexItems.add(mo);
		}
		
		return mexItems;
	}
}
