package com.sirap.basic.tool;

import java.io.File;

public class FileSizeCalculator {
	
	public FileSizeCalculator(String directory) {
		walk(directory);
	}
	
	public long getTotalSize() {
		return total;
	}

	private long total = 0;
	
	private void walk(String dir) {
		File file = new File(dir);
		if(file.isFile()) {
			total += file.length();
			return;
		}

		File[] kids = file.listFiles();
		if(kids == null) {
			return;
		}
		
		for(int i = 0; i < kids.length; i++) {
			walk(kids[i].getAbsolutePath());
		}
	}
}
