package com.sirap.basic.domain;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.DateUtil;
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
	public long getSuperSize() {
		if(folderSize == -1) {
			folderSize = FileUtil.sizeOf(file.getAbsolutePath());
		}
		
		return folderSize;
	}
	
	public Integer countOfKids() {
		if(file.isDirectory()) {
			String[] files = file.list();
			if(files != null) {
				return files.length;
			}
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
		String temp = getPath();
		
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
		
		String sae = seasonAndEpisodeOf(keyWord);
		if(sae != null && StrUtil.contains(temp, sae)) {
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
	
	@Override
	public List toList(String options) {
		List<String> list = Lists.newArrayList();
		
		String dent = "";
		if(OptionUtil.readBooleanPRI(options, "t", false)) {
			dent = StrUtil.spaces(depth * 4);
		}
		
		String filepath = "";
		if(OptionUtil.readBooleanPRI(options, "win", false)) {
			filepath = getWindowsPath();
		} else if(OptionUtil.readBooleanPRI(options, "sh", false)) {
			filepath = getShellPath();
		} else if (OptionUtil.readBooleanPRI(options, "un", false)){
			filepath = getUnixPath();
		} else {
			filepath = getPath();
		}

		String kids = "";
		if(OptionUtil.readBooleanPRI(options, "k", false)) {
			Integer count = countOfKids();
			if(count != null) {
				kids = " (" + countOfKids() + ")";
			}
		}
		
		String hidden = "";
		if(OptionUtil.readBooleanPRI(options, "h", false)) {
			if(file.isHidden()) {
				hidden = " (H)";
			}
		}
		
		list.add(dent + filepath + kids + hidden);
		
		boolean showSuperSize = OptionUtil.readBooleanPRI(options, "ss", false);
		boolean showSize = OptionUtil.readBooleanPRI(options, "s", false);
		if(showSuperSize) {
			showSize = false;
		}
		
		if(showSize) {
			list.add(FileUtil.formatSize(file.length()));
		}
		
		if(showSuperSize) {
			list.add(FileUtil.formatSize(getSuperSize()));
		}

		if(OptionUtil.readBooleanPRI(options, "d", false)) {
			Date lastmodified = new Date(file.lastModified());
			list.add(DateUtil.displayDate(lastmodified, DateUtil.DATE_TIME));
		}
		
		return list;
	}
	
	public String toPrint(String options) {
		List<String> items = toList(options);
		StringBuffer sb = sb();
		for(String item : items) {
			sb.append(item).append(" ");
		}
		
		return trimRight(sb.toString());
	}
	
	public String seasonAndEpisodeOf(String floats) {
		Matcher ma = StrUtil.createMatcher("(\\d{1,2})\\.(\\d{1,2})", floats);
		if(ma.find()) {
			String season = StrUtil.padLeft(ma.group(1), 2, "0");
			String episode = StrUtil.padLeft(ma.group(2), 2, "0");
			String criteria = StrUtil.occupy("S{0}E{1}", season, episode);
			
			return criteria;
		}
		
		return null;
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
