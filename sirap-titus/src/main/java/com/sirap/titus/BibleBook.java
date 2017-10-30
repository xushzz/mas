package com.sirap.titus;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class BibleBook extends MexItem implements Cloneable {
	
	private int order;
	private int maxChapter;
	private String version;
	private String name;
	private String href;
	
	public BibleBook(String name, int maxChapter) {
		this.name = name;
		this.maxChapter = maxChapter;
	}
	
	public BibleBook(int order, String name, int maxChapter) {
		this.order = order;
		this.name = name;
		this.maxChapter = maxChapter;
	}
	
	public int getOrder() {
		return order;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getNameWithNiceOrder() {
		String value = StrUtil.padLeft(order + "", 2, "0") + " " + name;
		return value;
	}

	public String getName() {
		return name;
	}

	public int getMaxChapter() {
		return maxChapter;
	}

	public boolean isMatched(String keyWord, boolean caseSensitive) {
		String temp = name.replaceAll("\\s+", "");
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(name, keyWord, caseSensitive)) {
			return true;
		}
		
		temp = maxChapter + "";
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
		temp = "#" + order;
		if(StrUtil.equalsCaseSensitive(temp, keyWord)) {
			return true;
		}
		
		temp = "" + order;
		if(StrUtil.equalsCaseSensitive(temp, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	public BibleBook clone() { 
		try {
	        return (BibleBook)super.clone();  
		} catch (Exception ex) {
			throw new MexException(ex);
		}
    }
	
	public String toString() {
		return "#" + order + " " + name + ", " + maxChapter + " chapters";
	}
}
