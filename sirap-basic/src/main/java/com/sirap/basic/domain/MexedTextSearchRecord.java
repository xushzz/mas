package com.sirap.basic.domain;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexedTextSearchRecord extends MexedObject {
	
	private String shortFilename;
	private String fullFilename;
	private boolean printSource;
	private int lineNumber;
	
	public MexedTextSearchRecord() {
		
	}
	
	public MexedTextSearchRecord(Object obj) {
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

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String getString() {
		String temp = String.valueOf(obj);
		if(printSource) {
			temp = fullFilename + " " + getPseudoOrder() + " " + obj;
		}
		
		return temp;
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
	
	@Override
	public String toString() {
		String temp = String.valueOf(obj);
		if(printSource) {
			temp = fullFilename + " " + lineNumber + " " + obj;
		}
		
		return temp;
	}
}
