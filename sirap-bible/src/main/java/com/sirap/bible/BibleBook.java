package com.sirap.bible;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class BibleBook extends MexItem {
	
	private int order;
	private String name;
	private int maxChapter;
	
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

	public String getNameWithNiceOrder() {
		String value = StrUtil.extendLeftward(order + "", 2, "0") + " " + name;
		return value;
	}

	public String getName() {
		return name;
	}

	public int getMaxChapter() {
		return maxChapter;
	}

	public boolean isMatched(String keyWord) {
		String temp = name.replaceAll("\\s+", "");
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(name, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		temp = maxChapter + "";
		if(isRegexMatched(temp, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(temp, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		return name + ", " + maxChapter;
	}
}
