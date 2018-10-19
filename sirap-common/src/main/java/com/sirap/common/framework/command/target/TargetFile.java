package com.sirap.common.framework.command.target;

import java.util.List;

import com.sirap.basic.tool.D;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public abstract class TargetFile extends Target {
	
	protected String folderpath;
	protected String filename;
	
	public String getFolderpath() {
		return folderpath;
	}

	public void setFolderpath(String folderpath) {
		this.folderpath = folderpath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTimestampPath() {
		String newfilename = DateUtil.timestamp() + "_" + filename;
		return StrUtil.useSeparator(folderpath, newfilename);
	}

	public String getFilePath() {
		return StrUtil.useSeparator(folderpath, filename);
	}

	@Override
	public String toString() {
		return D.jst(this);
	}

	public String topInfo(List records) {
		String topInfo = getCommand();
		if(records != null && records.size() > 5) {
			topInfo = "(" + records.size() + ") " + topInfo;
		}
		
		return topInfo;
	}
}
