package com.sirap.basic.domain;

import java.io.File;
import java.util.Date;

import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
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

	private long folderSize = -1;
	public long getFileFize() {
		if(folderSize == -1) {
			folderSize = FileUtil.sizeOf(file.getAbsolutePath());
		}
		
		return folderSize;
	}
	
	public String getKids() {
		if(!file.isDirectory()) {
			return null;
		}
		
		String[] files = file.list();
		if(files != null) {
			return "(" + files.length + ")";
		}

		return null;
	}

	public String getName() {
		return file.getName();
	}
	
	public String getPath() {
		return file.getAbsolutePath();
	}
	
	public String getUnixPath() {
		String value = file.getAbsolutePath();
		if(file.isDirectory()) {
			value += File.separator;
		}
		
		return value.replace('\\', '/');
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
		
		temp = getUnixPath();
		
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
		if(StrUtil.equals(keyWord, ":-f") && !file.isFile()) {
			return true;
		}
		
		if(StrUtil.equals(keyWord, ":-d") && !file.isDirectory()) {
			return true;
		}
		
		if(StrUtil.equals(keyWord, ":-h") && !file.isHidden()) {
			return true;
		}
		
		if(StrUtil.equals(keyWord, ":+f") && file.isFile()) {
			return true;
		}
		
		if(StrUtil.equals(keyWord, ":+d") && file.isDirectory()) {
			return true;
		}
		
		if(StrUtil.equals(keyWord, ":+h") && file.isHidden()) {
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
		StringBuilder sb = new StringBuilder(getUnixPath());
		
		boolean flagHidden = OptionUtil.readBooleanPRI(optionsStr, "hide", false);
		if(flagHidden) {
			if(file.isHidden()) {
				sb.append(" ").append("(H)");
			}
		}

		boolean showKids = OptionUtil.readBooleanPRI(optionsStr, "kids", false);
		if(showKids) {
			String kids = getKids();
			if(!EmptyUtil.isNullOrEmpty(kids)) {
				sb.append(" ").append(kids);
			}
		}
		
		boolean showSize = OptionUtil.readBooleanPRI(optionsStr, "size", false);
		if(showSize) {
			sb.append("  ");
			sb.append(FileUtil.formatFileSize(file.length()));
		}
		
		boolean showSizeWithFolder = OptionUtil.readBooleanPRI(optionsStr, "sizes", false);
		if(showSizeWithFolder) {
			sb.append("  ");
			sb.append(FileUtil.formatFileSize(getFileFize()));
		}

		boolean showDate = OptionUtil.readBooleanPRI(optionsStr, "date", false);
		if(showDate) {
			sb.append("  ");
			Date lastmodified = new Date(file.lastModified());
			sb.append(DateUtil.displayDate(lastmodified, DateUtil.DATETIME));
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
