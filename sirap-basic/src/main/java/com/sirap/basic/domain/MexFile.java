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
	private int depth;
	
	public MexFile(File file) {
		this.file = file;
	}

	public MexFile(File file, int depth) {
		this.file = file;
		this.depth = depth;
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
	
	//default
	public String getPath() {
		String value = file.getAbsolutePath();
		if(file.isDirectory()) {
			value += File.separator;
		}
		
		return value;
	}
	
	public String getWindowsPath() {
		return FileUtil.windowsSeparator(getPath());
	}
	
	public String getUnixPath() {
		return FileUtil.unixSeparator(getPath());
	}
	
	public String getShellPath() {
		return FileUtil.shellStyle(getPath());
	}
	
	@Override
	public boolean isMatched(String keyWord, boolean caseSensitive) {
		String temp = getUnixPath();
		
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
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
		
		SizeCriteria quinn = getFileSizeCriteria(keyWord);
		if(quinn != null && quinn.isGood(file.length())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return file.getAbsolutePath();
	}
	
	public String toPrint(String options) {
		StringBuilder sb = new StringBuilder();
		if(OptionUtil.readBooleanPRI(options, "tree", false)) {
			String dent = StrUtil.repeatSpace(depth * 4);
			sb.append(dent);
		}
		if(OptionUtil.readBooleanPRI(options, "win", false)) {
			sb.append(getWindowsPath());
		} else if(OptionUtil.readBooleanPRI(options, "sh", false)) {
			sb.append(getShellPath());
		} else if (OptionUtil.readBooleanPRI(options, "un", false)){
			sb.append(getUnixPath());
		} else {
			sb.append(getPath());
		}

		boolean flagHidden = OptionUtil.readBooleanPRI(options, "hide", false);
		if(flagHidden) {
			if(file.isHidden()) {
				sb.append(" ").append("(H)");
			}
		}

		boolean showKids = OptionUtil.readBooleanPRI(options, "kids", false);
		if(showKids) {
			String kids = getKids();
			if(!EmptyUtil.isNullOrEmpty(kids)) {
				sb.append(" ").append(kids);
			}
		}
		
		boolean showSize = OptionUtil.readBooleanPRI(options, "size", false);
		if(showSize) {
			sb.append("  ");
			sb.append(FileUtil.formatSize(file.length()));
		}
		
		boolean showSizeWithFolder = OptionUtil.readBooleanPRI(options, "sizes", false);
		if(showSizeWithFolder) {
			sb.append("  ");
			sb.append(FileUtil.formatSize(getFileFize()));
		}

		boolean showDate = OptionUtil.readBooleanPRI(options, "date", false);
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
		result = prime * result + ((getUnixPath() == null) ? 0 : getUnixPath().hashCode());
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
		if (getUnixPath() == null) {
			if (other.getUnixPath() != null)
				return false;
		} else if (!getUnixPath().equals(other.getUnixPath()))
			return false;
		
		return true;
	}

	@Override
	public int compareTo(MexFile o) {
		String path = file.getAbsolutePath().toLowerCase();
		String path2 = o.file.getAbsolutePath().toLowerCase();

		return path.compareTo(path2);
	}
}
