package com.sirap.common.framework.command.target;

import com.sirap.basic.output.PDFParams;
import com.sirap.basic.util.DateUtil;

public class TargetPDF extends Target {
	
	private String path;
	private String fileName;
	
	public TargetPDF(String path, String fileName) {
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
	
	@Override
	public PDFParams getParams() {
		if(params instanceof PDFParams) {
			return (PDFParams)params;
		} else {
			params = new PDFParams();
			return (PDFParams)params;
		}
	}

	@Override
	public String toString() {
		return "TargetPDF [path=" + path + "]";
	}
}
