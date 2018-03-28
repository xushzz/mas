package com.sirap.basic.domain;

import java.util.zip.ZipEntry;

import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexZipEntry extends MexItem {

	protected String jarName;
	protected ZipEntry entry;
	
	public MexZipEntry(ZipEntry entry) {
		this.entry = entry;
	}
	
	public ZipEntry getEntry() {
		return entry;
	}

	public String getName() {
		return entry.getName();
	}
	
	public String getJarName() {
		return jarName;
	}

	public void setJarName(String jarName) {
		this.jarName = jarName;
	}
	
	public boolean isNormalFile() {
		return !entry.isDirectory();
	}
	
	@Override
	public boolean isMatched(String keyWord, boolean caseSensitive) {
		String temp = getName();
		
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
		if(isNormalFile()) {
			SizeCriteria quinn = getFileSizeCriteria(keyWord);
			if(quinn != null && quinn.isGood(entry.getSize())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String sizeWithUnit = FileUtil.formatSize(entry.getSize());
		return getJarName() + ", " + getName() + ", " + sizeWithUnit;
	}

	public String toPrint() {
		return toPrint("");
	}
	
	public String toPrint(String options) {
		boolean showSize = OptionUtil.readBooleanPRI(options, "size", false);
		boolean showFullpath = OptionUtil.readBooleanPRI(options, "full", true);
		StringBuilder sb = new StringBuilder();
		if(showFullpath) {
			sb.append(FileUtil.unixSeparator(getJarName()));
			sb.append("!/");
		}
		sb.append(getName());
		if(showSize && isNormalFile()) {
			sb.append(", ").append(FileUtil.formatSize(entry.getSize()));
		}
		
		return sb.toString();
	}
}
