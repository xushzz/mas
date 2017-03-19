package com.sirap.common.framework.command.target;

import com.sirap.basic.output.HtmlParams;
import com.sirap.basic.util.DateUtil;

public class TargetHtml extends Target {
	
	private String path;
	private String fileName;
	
	public TargetHtml(String path, String fileName) {
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
	
	public HtmlParams getParams() {
		if(params instanceof HtmlParams) {
			return (HtmlParams)params;
		} else {
			params = new HtmlParams();
			return (HtmlParams)params;
		}
	}

	@Override
	public String toString() {
		return "TargetHtml [path=" + path + ", fileName=" + fileName + "]";
	}
}
