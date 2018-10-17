package com.sirap.common.framework.command.target;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.output.OutputParams;

public abstract class Target {
	
	private boolean flag;
	private String value;
	private String command;
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
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void export(List records, String options, boolean withTimestamp) {
		throw new UnsupportedOperationException("Method must be overriden");
	}
	
	public String toStringItem(Object obj, String options) {
		if(obj == null) {
			return null;
		}
		
		MexItem item;
		if(obj instanceof MexItem) {
			item = (MexItem)obj;
			return item.toPrint(options);
		} else {
			return obj.toString();
		}
	}
	
	public List<String> toStringList(List list, String options) {
		List lines = new ArrayList<String>();
		for(Object obj : list) {
			lines.add(toStringItem(obj, options));
		}
		
		return lines;
	}
}
