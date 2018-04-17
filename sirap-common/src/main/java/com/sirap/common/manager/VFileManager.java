package com.sirap.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.sirap.basic.domain.MexFile;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;

public class VFileManager {

	private static VFileManager instance;
	private static List<MexFile> ALL_RECORDS = new ArrayList<MexFile>();
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
		String delimiter = ";";
		List<String> lines = SimpleKonfig.g().getUserValuesByKeyword("v.folder.");
		if(!EmptyUtil.isNullOrEmpty(lines)) {
			List<String> items = StrUtil.split(StrUtil.connect(lines, delimiter), delimiter);
			fixedPaths = EmptyUtil.filter(items);
		}
	}
	
	public List<String> getAllFolders() {
		Collections.sort(fixedPaths);
		
		return fixedPaths;
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
		List<MexFile> items = CollUtil.filter(files, criteria, caseSensitive);

		return items;
	}
}
