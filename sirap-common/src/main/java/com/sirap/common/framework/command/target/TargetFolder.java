package com.sirap.common.framework.command.target;

import java.io.File;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.OptionUtil;

public class TargetFolder extends Target {
	
	private String path;
	
	public TargetFolder(String path) {
		this.path = path;
		this.setFileRelated(true);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	public void export(List records, String options, boolean withTimestamp) {
		List<File> normalFiles = Colls.fileListOf(records);
		
		if(normalFiles.size() > 0) {
			long start = System.currentTimeMillis();
			if(OptionUtil.readBooleanPRI(options, "sync", false)) {
				IOUtil.copyFilesSequentially(normalFiles, path);
			} else {
				IOUtil.copyFiles(normalFiles, path);
			}
			long end = System.currentTimeMillis();
			C.time2(start, end);
		} else {
			C.pl("No normal files detected: ");
			D.list(records);
		}
	}
	
	@Override
	public String toString() {
		return "TargetFolder [path=" + path + "]";
	}
}
