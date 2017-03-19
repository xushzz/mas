package com.sirap.geek.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class CharsetCode extends MexItem {
	private String name;
	private String tightName;
	
	public CharsetCode(String name) {
		this.name = name;
		this.tightName = name.replace("-", "");
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTightName() {
		return tightName;
	}

	public void setTightName(String tightName) {
		this.tightName = tightName;
	}

	public boolean isMatched(String keyWord) {
		if(keyWord.length() < 1) {
			return false;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(tightName, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(tightName, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String space = StrUtil.repeat(' ', 4);
		StringBuffer sb = new StringBuffer();
		sb.append(tightName).append(space);
		sb.append(name);
		
		return sb.toString();
	}
}
