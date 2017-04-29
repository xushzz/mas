package com.sirap.basic.domain;

import java.io.File;

import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexedFile extends MexItem implements Comparable<MexedFile> {
	
	protected File file;
	
	public MexedFile(File file) {
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
	public boolean isMatched(String keyWord) {
		String temp = getPath();
		
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord)) {
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
	
	public String toString() {
		return file.getAbsolutePath();
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
		MexedFile other = (MexedFile) obj;
		if (getPath() == null) {
			if (other.getPath() != null)
				return false;
		} else if (!getPath().equals(other.getPath()))
			return false;
		
		return true;
	}

	@Override
	public int compareTo(MexedFile o) {
		String path = file.getAbsolutePath();
		String path2 = o.file.getAbsolutePath();
		
		return path.compareTo(path2);
	}
}
