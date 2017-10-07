package com.sirap.basic.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexFile;

public class FileDeeper {
	
	private int maxLevel = -1;
	private List<MexFile> maxLevelFiles = new ArrayList<MexFile>();
	private String directory;
	
	public FileDeeper(String directory) {
		this.directory = directory;
	}

	public int howDeep() {
		walk(directory, 0);
		
		return maxLevel;
	}
	
	public List<MexFile> getMaxLevelFiles() {
		if(maxLevel == -1) {
			howDeep();
		}
		
		return maxLevelFiles;
	}

	private void walk(String dir, int current) {
		File file = new File(dir);
		
		if(!file.exists()) {
			return;
		}
		
		if(current > maxLevel) {
			maxLevel = current;
			maxLevelFiles.clear();
			maxLevelFiles.add(new MexFile(file));
		} else if(current == maxLevel) {
			maxLevelFiles.add(new MexFile(file));
		}
		
		if(!file.isDirectory()) {
			return;
		}
		
		File[] files = file.listFiles();
		if(files == null) {
			return;
		}
		
		for(int i = 0; i < files.length; i++) {
			String path = files[i].getAbsolutePath();
			walk(path, current + 1);
		}
	}
}
