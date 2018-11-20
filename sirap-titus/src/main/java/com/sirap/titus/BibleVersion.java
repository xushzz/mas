//package com.sirap.titus;
//
//import java.util.List;
//
//import com.google.common.collect.Lists;
//import com.sirap.basic.domain.MexItem;
//import com.sirap.basic.util.StrUtil;
//
//@SuppressWarnings("serial")
//public class BibleVersion extends MexItem {
//	
//	private String code;
//	private String name;
//	private String full;
//	
//	public BibleVersion(String code, String name, String full) {
//		this.name = name;
//		this.code = code;
//		this.full = full;
//	}
//	
//	public String getCode() {
//		return code;
//	}
//
//	public String getFull() {
//		return full;
//	}
//
//	public String getName() {
//		return name;
//	}
//	public String getHref() {
//		return StrUtil.useSlash(BibleData.HOMEPAGE, code);
//	}
//	
//	@Override
//	public List toList(String options) {
//		return Lists.newArrayList(code, name, full, getHref());
//	}
//	
//	public boolean isMatched(String keyWord, boolean caseSensitive) {
//		if(StrUtil.equals(code, keyWord)) {
//			return true;
//		}
//		
//		if(StrUtil.equals(name, keyWord)) {
//			return true;
//		}
//		
//		if(StrUtil.contains(full, keyWord)) {
//			return true;
//		}
//		
//		if(isRegexMatched(full, keyWord)) {
//			return true;
//		}
//		
//		return false;
//	}
//	
//	public String toString() {
//		return code + ", " + name + ", " + full + ", " + getHref();
//	}
//}
