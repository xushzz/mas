package com.sirap.basic.domain;

import java.io.File;
import java.util.Date;

import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexFile extends MexItem implements Comparable<MexFile> {
	
	protected File file;
	
	public MexFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}

	public String getName() {
		return file.getName();
	}
	
	public String getPath() {
		return file.getAbsolutePath();
	}
	
	@Override
	public boolean isMatched(String keyWord, boolean caseSensitive) {
		String temp = getPath();
		
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
		SizeCriteria quinn = getSizeCriteria(keyWord);
		if(quinn != null && quinn.isGood(file.length())) {
			return true;
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
		return file.getAbsolutePath();
	}
	
	public String toPrint(String optionsStr) {
		StringBuilder sb = new StringBuilder(file.getAbsolutePath());
		boolean showSize = OptionUtil.readBoolean(optionsStr, "size", false);
		if(showSize) {
			sb.append("  ");
			sb.append(FileUtil.formatFileSize(file.length()));
		}

		boolean showDate = OptionUtil.readBoolean(optionsStr, "date", false);
		if(showDate) {
			sb.append("  ");
			Date lastmodified = new Date(file.lastModified());
			sb.append(DateUtil.displayDate(lastmodified));
		}
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MexFile other = (MexFile) obj;
		if (getPath() == null) {
			if (other.getPath() != null)
				return false;
		} else if (!getPath().equals(other.getPath()))
			return false;
		
		return true;
	}

	@Override
	public int compareTo(MexFile o) {
		String path = file.getAbsolutePath();
		String path2 = o.file.getAbsolutePath();
		
		return path.compareTo(path2);
	}
}
