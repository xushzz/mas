package com.sirap.basic.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexFile;

public class FileWalker {
	
	private List<MexFile> allFiles = new ArrayList<>();
	private String directory;
	private boolean includeFolder = true;
	
	public FileWalker(String directory, boolean includeFolder) {
		this.directory = directory;
		this.includeFolder = includeFolder;
	}

	public List<MexFile> listFilesRecursively(int depth) {
		int currentLayer = 1;
		walk(directory, currentLayer, currentLayer + depth);
		
		return allFiles;
	}
	
	private void walk(String dir, int current, int max) {
		File file = new File(dir);
		if(file.isDirectory()) {
			if(includeFolder) {
				allFiles.add(new MexFile(file, current - 1));
			}
		} else {
			allFiles.add(new MexFile(file, current - 1));
			return;
		}

		if(current > max) {
			return;
		}
		
		File[] subFiles = file.listFiles();
		if(subFiles == null) {
			return;
		}
		
		for(int i = 0; i < subFiles.length; i++) {
			String path = subFiles[i].getAbsolutePath();
			walk(path, current + 1, max);
		}
	}
}
