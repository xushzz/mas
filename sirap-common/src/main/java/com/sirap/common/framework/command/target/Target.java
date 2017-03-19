package com.sirap.common.framework.command.target;

import com.sirap.basic.output.OuputParams;

public class Target {
	
	private boolean flag;
	private String value;
	protected OuputParams params;
	
	public OuputParams getParams() {
		return params;
	}

	public void setParams(OuputParams params) {
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
