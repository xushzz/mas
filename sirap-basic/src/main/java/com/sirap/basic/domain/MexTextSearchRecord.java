package com.sirap.basic.domain;

import com.sirap.basic.util.FileUtil;
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
	
	public String getShortFileNameWithoutExtension() {
		String name = FileUtil.extractFilenameWithoutExtension(fullFilename);
		return name;
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

	public String toPrint(String options) {
		boolean showFullFilePath = OptionUtil.readBooleanPRI(options, "full", false);
		boolean showLineNumber = OptionUtil.readBooleanPRI(options, "line", false);
		boolean showShortFileName = OptionUtil.readBooleanPRI(options, "short", false);
		StringBuilder sb = new StringBuilder();
		if(showFullFilePath) {
			sb.append(fullFilename).append(" ");
		}
		if(showShortFileName) {
			sb.append(getShortFileNameWithoutExtension()).append(" ");
		}
		if(showLineNumber) {
			sb.append("L" + lineNumber).append(" ");
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
