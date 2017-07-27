package com.sirap.common.framework.command.target;

import com.sirap.basic.output.OutputParams;

public class Target {
	
	private boolean flag;
	private String value;
	protected OutputParams params;
	
	public OutputParams getParams() {
		return params;
	}

	public void setParams(OutputParams params) {
		this.params = params;
	}

	public boolean isFileRelated() {
		return flag;
	}
	
	public void setFileRelated(boolean flag) {
		this.flag = flag;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
