package com.sirap.common.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedFile;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;

public class FileManager {

	private static FileManager instance;
	private static List<MexedFile> ALL_RECORDS = new ArrayList<MexedFile>();
	private String[] originalPaths;
	private List<String> fixedPaths = new ArrayList<String>();
	
	private volatile boolean isSynchronizing = false;
	
	private FileManager() {
	}
	
	private static class Holder {
		private static FileManager instance = new FileManager();
	}
	
	public static FileManager g() {
		if(instance == null) {
			instance = Holder.instance;
			instance.init();
			instance.getAllRecords(true);
		}
		
		return instance;
	}
	
	public int[] refresh() {
		int before = ALL_RECORDS.size();
		getAllRecords(true);
		int after = ALL_RECORDS.size();
		
		return new int[]{before, after};
	}
	
	private void setSyncFlag(boolean flag) {
		isSynchronizing = flag;
	}
	
	public synchronized List<MexedFile> getAllRecords(boolean isForcibly) {
		if(EmptyUtil.isNullOrEmpty(ALL_RECORDS) || isForcibly) {
			setSyncFlag(true);
			int depth = SimpleKonfig.g().getUserNumberValueOf("v.depth", 2);
			List<File> allFiles = FileUtil.scanFolder(fixedPaths, depth, null, false);
			Set<MexedFile> set = new HashSet<MexedFile>();
			for(File file:allFiles) {
				set.add(new MexedFile(file));
			}
			
			ALL_RECORDS.clear();
			ALL_RECORDS.addAll(set);
			setSyncFlag(false);
		}
		
		return ALL_RECORDS;
	}
	
	private void init() {
		originalPaths = getOriginalPaths();
		
		for(String path:originalPaths) {
			
			String cleverPath = FileUtil.parseFolderPath("", path);
			if(cleverPath == null) {
				continue;
			}
			
			fixedPaths.add(cleverPath);
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
		List<String> folders = Arrays.asList(originalPaths);
		Collections.sort(folders);
		
		return folders;
	}
	
	private void showTipIfNeeded() {
		if(isSynchronizing) {
			C.pl("File cache is synchronizing");
		}
	}
	
	public List<MexedFile> getAllFileRecords() {
		showTipIfNeeded();
		List<MexedFile> files = new ArrayList<MexedFile>(ALL_RECORDS);
		
		Collections.sort(files);
		
		return files;
	}

	public List<MexedFile> getFileRecordsByName(String criteria) {
		showTipIfNeeded();
		List<MexedFile> files = getAllRecords(false);
		MexFilter<MexedFile> filter = new MexFilter<MexedFile>(criteria, files);
		List<MexedFile> items = filter.process();
		
		String fixedCriteria = fixCriteria(criteria);
		filter = new MexFilter<MexedFile>(fixedCriteria, files);
		items.addAll(filter.process());

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
