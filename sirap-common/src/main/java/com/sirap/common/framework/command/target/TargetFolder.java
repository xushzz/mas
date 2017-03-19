package com.sirap.common.framework.command.target;

public class TargetFolder extends Target {
	
	private String path;
	
	public TargetFolder(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "TargetFolder [path=" + path + "]";
	}
}
