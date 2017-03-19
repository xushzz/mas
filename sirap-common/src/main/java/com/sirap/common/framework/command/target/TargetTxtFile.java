package com.sirap.common.framework.command.target;

import com.sirap.basic.util.DateUtil;

public class TargetTxtFile extends Target {
	
	private String path;
	private String fileName;
	
	public TargetTxtFile(String path, String fileName) {
		this.path = path;
		this.fileName = fileName;
	}
	
	public String getTimestampPath() {
		String temp = DateUtil.timestamp() + "_";
		return path + temp + fileName;
	}

	public String getFilePath() {
		return path + fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return "TargetTxtFile [path=" + path + "]";
	}
}
