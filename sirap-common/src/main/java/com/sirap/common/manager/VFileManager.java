package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexFile;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;

public class VFileManager {

	private static VFileManager instance;
	private static List<MexFile> ALL_RECORDS = new ArrayList<MexFile>();
	private String[] originalPaths;
	private List<String> fixedPaths = new ArrayList<String>();
	
	private volatile boolean isSynchronizing = false;
	
	private VFileManager() {
	}
	
	private static class Holder {
		private static VFileManager instance = new VFileManager();
	}
	
	public static VFileManager g() {
		if(instance == null) {
			instance = Holder.instance;
			instance.initFixedPaths();
			instance.getAllRecords(true);
		}
		
		return instance;
	}
	
	public int[] refresh() {
		instance.initFixedPaths();
		int before = ALL_RECORDS.size();
		getAllRecords(true);
		int after = ALL_RECORDS.size();
		
		return new int[]{before, after};
	}
	
	private void setSyncFlag(boolean flag) {
		isSynchronizing = flag;
	}
	
	public synchronized List<MexFile> getAllRecords(boolean isForcibly) {
		if(fixedPaths != null)
		if(EmptyUtil.isNullOrEmpty(ALL_RECORDS) || isForcibly) {
			setSyncFlag(true);
			List<MexFile> allFiles = FileUtil.scanFolders(fixedPaths, false);
			ALL_RECORDS.clear();
			ALL_RECORDS.addAll(new HashSet<MexFile>(allFiles));
			setSyncFlag(false);
		}
		
		return ALL_RECORDS;
	}
	
	private void initFixedPaths() {
		originalPaths = getOriginalPaths();
		fixedPaths.clear();
		for(String path:originalPaths) {
			fixedPaths.add(path);
		}
	}
	
	private String[] getOriginalPaths() {
		List<String> pathNodes = SimpleKonfig.g().getUserValuesByKeyword("v.folder.");
		if(EmptyUtil.isNullOrEmpty(pathNodes)) {
			return new String[0];
		}
		
		String[] paths = StrUtil.connect(pathNodes, ";").split(";");
		
		return paths;
	}
	
	public List<String> getAllFolders() {
		initFixedPaths();
		List<String> folders = Arrays.asList(originalPaths);
		Collections.sort(folders);
		
		return folders;
	}
	
	private void showTipIfNeeded() {
		if(isSynchronizing) {
			C.pl("File cache is synchronizing");
		}
	}
	
	public List<MexFile> getAllFileRecords() {
		showTipIfNeeded();
		List<MexFile> files = new ArrayList<MexFile>(ALL_RECORDS);
		
		Collections.sort(files);
		
		return files;
	}

	public List<MexFile> getFileRecordsByName(String criteria, boolean caseSensitive) {
		showTipIfNeeded();
		List<MexFile> files = getAllRecords(false);
		List<MexFile> items = CollectionUtil.filter(files, criteria, caseSensitive);
		
		String fixedCriteria = fixCriteria(criteria);
		if(!EmptyUtil.isNullOrEmpty(fixedCriteria)) {
			items.addAll(CollectionUtil.filter(files, fixedCriteria));
		}

		Collections.sort(items);
		
		return items;
	}
	
	public static String fixCriteria(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return null;
		}
		String exp = "(\\d{1,2})\\.(\\d{1,2})";
		Matcher m = Pattern.compile(exp, Pattern.CASE_INSENSITIVE).matcher(source);
		if(m.find()) {
			String season = m.group(1);
			if(MathUtil.toInteger(season) == 0) {
				return null;
			}
			String episode = m.group(2);
			if(MathUtil.toInteger(episode) == 0) {
				return null;
			}
			
			season = StrUtil.extendLeftward(season, 2, "0");
			episode = StrUtil.extendLeftward(episode, 2, "0");
			String temp = "S" + season + "E" + episode;
			String criteria = source.replace(m.group(), temp);
			
			return criteria;
		}
		
		return null;
	}
}
