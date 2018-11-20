package com.sirap.titus;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class BibleBook extends MexItem implements Cloneable {
	
	private int id;
	private String type;
	private int maxChapter;
	private String version;
	private String name;
	
	public BibleBook(int id, String type, String name, int maxChapter) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.maxChapter = maxChapter;
	}
	
	public int getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}

	public BibleBook setType(String type) {
		this.type = type;
		return this;
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

	public String getNameWithNiceOrder() {
		String value = StrUtil.padLeft(id + "", 2, "0") + " " + name;
		return value;
	}

	public String getName() {
		return name;
	}

	public int getMaxChapter() {
		return maxChapter;
	}
	
	@Override
	public List toList(String options) {
		List<String> items = Lists.newArrayList();
		items.add("#" + id);
		items.add(type);
		items.add(name);
		items.add(maxChapter + " chapters");
		return items;
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
		
		if(isRegexMatched(type, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(type, keyWord, caseSensitive)) {
			return true;
		}
		
		temp = maxChapter + "";
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord, caseSensitive)) {
			return true;
		}
		
		temp = "#" + id;
		if(StrUtil.equalsCaseSensitive(temp, keyWord)) {
			return true;
		}
		
		temp = "" + id;
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
		return "#" + id + " " + name + ", " + maxChapter + " chapters";
	}
}
