package com.sirap.basic.domain;

import java.util.List;
import java.util.zip.ZipEntry;

import com.sirap.basic.component.MexedOption;
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
		return entry.getName().replaceAll("/$", "");
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
			SizeCriteria quinn = getSizeCriteria(keyWord);
			if(quinn != null && quinn.isGood(entry.getSize())) {
				return true;
			}
		}
		
		return false;
	}
	
	private SizeCriteria getSizeCriteria(String source) {
		SizeCriteria quinn = new SizeCriteria();
		if(quinn.parse(source)) {
			return quinn;
		} else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		String sizeWithUnit = FileUtil.formatFileSize(entry.getSize());
		return getJarName() + ", " + getName() + ", " + sizeWithUnit;
	}

	public String toPrint() {
		return toPrint("");
	}
	
	public String toPrint(String optionsStr) {
		List<MexedOption> options = OptionUtil.parseOptions(optionsStr);
		boolean showSize = OptionUtil.readBoolean(options, "size", false);
		boolean showFullpath = OptionUtil.readBoolean(options, "full", true);
		StringBuilder sb = new StringBuilder();
		if(showFullpath) {
			sb.append(getJarName() + "!/");
		}
		sb.append(getName());
		if(showSize && isNormalFile()) {
			sb.append(", ").append(FileUtil.formatFileSize(entry.getSize()));
		}
		
		return sb.toString();
	}
}
