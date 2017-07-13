package com.sirap.basic.domain;

import java.util.jar.JarEntry;

import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexedJarEntry extends MexItem {
	
	protected JarEntry entry;
	
	public MexedJarEntry(JarEntry entry) {
		this.entry = entry;
	}
	
	public JarEntry getEntry() {
		return entry;
	}

	public String getName() {
		return entry.getName().replaceAll("/$", "");
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		String temp = getName();
		
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord)) {
			return true;
		}
		
		SizeCriteria quinn = getSizeCriteria(keyWord);
		if(quinn != null && quinn.isGood(entry.getSize())) {
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
		return getName();
	}
}
