package com.sirap.common.framework;

import java.util.List;

public class QueryItem {
	private String command;
	private List result;
	
	public QueryItem() {
		
	}
	
	public QueryItem(String command, List result) {
		this.command = command;
		this.result = result;
	}
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public List getResult() {
		return result;
	}
	public void setResult(List result) {
		this.result = result;
	}
}
