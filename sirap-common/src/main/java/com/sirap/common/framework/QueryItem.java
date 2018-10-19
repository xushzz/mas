package com.sirap.common.framework;

import java.util.List;

public class QueryItem {
	private String command;
	private String options;
	private List result;
	
	public QueryItem() {
		
	}
	
	public QueryItem(String command, String options, List result) {
		this.command = command;
		this.options = options;
		this.result = result;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public List getResult() {
		return result;
	}
	public void setResult(List result) {
		this.result = result;
	}
}
