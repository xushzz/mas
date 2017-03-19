package com.sirap.common.framework.command.target;

import com.sirap.basic.output.ExcelParams;
import com.sirap.basic.util.DateUtil;

public class TargetExcel extends Target {
	
	private String path;
	private String fileName;
	
	public TargetExcel(String path, String fileName) {
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

	public void setPath(String path) {
		this.path = path;
	}
	
	public ExcelParams getParams() {
		if(params instanceof ExcelParams) {
			return (ExcelParams)params;
		} else {
			params = new ExcelParams();
			return (ExcelParams)params;
		}
	}

	@Override
	public String toString() {
		return "TargetPDF [path=" + path + "]";
	}
}
