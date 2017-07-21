package com.sirap.basic.domain;

import java.util.List;

import com.sirap.basic.component.MexedOption;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexTextSearchRecord extends MexObject {
	
	private String shortFilename;
	private String fullFilename;
	private boolean printSource;
	private int lineNumber;
	
	public MexTextSearchRecord() {
		
	}
	
	public MexTextSearchRecord(Object obj) {
		this.obj = obj;
	}
	
	public boolean isPrintSource() {
		return printSource;
	}

	public void setPrintSource(boolean printSource) {
		this.printSource = printSource;
	}

	public String getShortFilename() {
		return shortFilename;
	}

	public void setShortFilename(String shortFilename) {
		this.shortFilename = shortFilename;
	}

	public String getFullFilename() {
		return fullFilename;
	}

	public void setFullFilename(String fullFilename) {
		this.fullFilename = fullFilename;
	}

	@Override
	public String getString() {
		String temp = String.valueOf(obj);
		if(printSource) {
			temp = fullFilename + " " + getPseudoOrder() + " " + obj;
		}
		
		return temp;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public boolean isMatched(String keyWord) {
		String source = String.valueOf(obj);
		
		if(isRegexMatched(source, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(source, keyWord)) {
			return true;
		}
		
		if(printSource && !EmptyUtil.isNullOrEmpty(shortFilename)) {
			if(isRegexMatched(shortFilename, keyWord)) {
				return true;
			}
			
			if(StrUtil.contains(shortFilename, keyWord)) {
				return true;
			}
		}
		
		return false; 
	}

	public String toPrint(String optionsStr) {
		List<MexedOption> options = OptionUtil.parseOptions(optionsStr);
		boolean showFullFilename = OptionUtil.readBoolean(options, "full", false);
		boolean showLineNumber = OptionUtil.readBoolean(options, "line", false);
		StringBuilder sb = new StringBuilder();
		if(showFullFilename) {
			sb.append(fullFilename).append("  ");
		}
		if(showLineNumber) {
			sb.append("#" + lineNumber).append("  ");
		}
		sb.append(obj.toString());
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		String temp = String.valueOf(obj);
		if(printSource) {
			temp = fullFilename + " " + lineNumber + " " + obj;
		}
		
		return temp;
	}
}
