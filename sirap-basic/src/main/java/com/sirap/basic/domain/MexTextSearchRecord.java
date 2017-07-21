package com.sirap.basic.domain;

import java.util.List;

import com.sirap.basic.component.MexedOption;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexTextSearchRecord extends MexObject {
	
	private String fullFilename;
	private int lineNumber;
	
	public MexTextSearchRecord() {
		
	}
	
	public MexTextSearchRecord(Object obj) {
		this.obj = obj;
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
	public boolean isMatched(String keyWord, boolean caseSensitive) {
		String source = String.valueOf(obj);
		
		if(isRegexMatched(source, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(source, keyWord, caseSensitive)) {
			return true;
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
		StringBuilder sb = new StringBuilder("$$$");
		sb.append(fullFilename).append("  ");
		sb.append("#" + lineNumber).append("  ");
		sb.append(obj.toString());
		
		return sb.toString();
	}
}
