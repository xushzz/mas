package com.sirap.basic.tool;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

public class FileWalker {
	
	private List<File> matchedFiles = new ArrayList<File>();
	private String directory;
	private FileFilter fileFilter;
	private boolean includeFolder = true;
	
	public FileWalker(String directory) {
		this.directory = directory;
	}
	
	public FileWalker(String directory, String... criterias) {
		this.directory = directory;
		initiateFileFilter(criterias);
	}
	
	public void setIncludeFolder(boolean includeFolder) {
		this.includeFolder = includeFolder;
	}

	private void initiateFileFilter(final String... criterias) {
		fileFilter = new FileFilter() {
			public boolean accept(File file) {
				if(FileUtil.isUndesiredFile(file.getName())) {
					return false;
				}
				
				if(file.isDirectory()) {
					return true;
				}

				if(EmptyUtil.isNullOrEmpty(criterias)) {
					return true;
				}
				
				String filePath = file.getAbsolutePath();
				for(int i = 0; i < criterias.length; i++) {
//					boolean flag = filePath.endsWith(suffixes[i]);
					boolean flag = StrUtil.contains(filePath, criterias[i], 2);
					if(flag) {
						return true;
					}
				}
				
				return false;
			}
		};
	}

	public List<File> listFilesRecursively(int depth) {
		int currentLayer = 1;
		walk(directory, currentLayer, currentLayer + depth);
		
		return matchedFiles;
	}
	
	private void walk(String dir, int current, int max) {
		File file = new File(dir);
		if(file.isDirectory()) {
			if(includeFolder) {
				matchedFiles.add(file);
			}
		} else {
			matchedFiles.add(file);
			return;
		}

		if(current > max) {
			return;
		}
		
		File[] files = file.listFiles(fileFilter);
		if(files == null) {
			return;
		}
		
		for(int i = 0; i < files.length; i++) {
			String path = files[i].getAbsolutePath();
			walk(path, current + 1, max);
		}
	}
}
