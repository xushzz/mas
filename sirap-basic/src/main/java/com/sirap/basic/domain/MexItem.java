package com.sirap.basic.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public abstract class MexItem implements Serializable {

	public static final String SYMBOL_CARET = "^";
	public static final String SYMBOL_USD = "$";

	protected int pseudoOrder;
	private String wholeMatchedStr;
	private String leftMatchedStr;
	private String rightMatchedStr;
	
	public boolean parse(String record) {
		return false;
	}
	
	public int getPseudoOrder() {
		return pseudoOrder;
	}

	public void setPseudoOrder(int pseudoOrder) {
		this.pseudoOrder = pseudoOrder;
	}

	private boolean requiresWholeMatched(String keyWord) {
		wholeMatchedStr = parseWholeMatched(keyWord);
		return wholeMatchedStr != null;
	}
	
	private boolean isWholeMatched(String target) {
		return StrUtil.equals(wholeMatchedStr, target);
	}
	
	private boolean requiresLeftMatched(String keyWord) {
		leftMatchedStr = parseLeftMatched(keyWord);
		return leftMatchedStr != null;
	}
	
	private boolean isLeftMatched(String target) {
		return StrUtil.startsWith(target, leftMatchedStr);
	}
	
	private boolean requiresRightMatched(String keyWord) {
		rightMatchedStr = parseRightMatched(keyWord);
		return rightMatchedStr != null;
	}
	
	private boolean isRightMatched(String target) {
		return StrUtil.endsWith(target, rightMatchedStr);
	}
	
	public boolean isMatched(String keyWord) {
		return false;
	}
	
	public String parseWholeMatched(String keyWord) {
		if(keyWord != null && keyWord.length() > 2 && keyWord.startsWith(SYMBOL_CARET) && keyWord.endsWith(SYMBOL_USD)) {
			String temp = keyWord.substring(1, keyWord.length() - 1);
			return temp;
		}
		
		return null;
	}

	public String parseLeftMatched(String keyWord) {
		if(keyWord != null && keyWord.length() > 1 && keyWord.startsWith(SYMBOL_CARET) && !keyWord.endsWith(SYMBOL_USD)) {
			return keyWord.substring(1);
		}
		
		return null;
	}
	
	public String parseRightMatched(String keyWord) {
		if(keyWord != null && keyWord.length() > 1 && keyWord.endsWith(SYMBOL_USD) && !keyWord.startsWith(SYMBOL_CARET)) {
			return keyWord.substring(0, keyWord.length() - 1);
		}
		
		return null;
	}
	
	public boolean isRegexMatched(String source, String keyWord) {
		
		if(requiresWholeMatched(keyWord) && isWholeMatched(source)) {
			return true;
		}
		
		if(requiresLeftMatched(keyWord) && isLeftMatched(source)) {
			return true;
		}
		
		if(requiresRightMatched(keyWord) && isRightMatched(source)) {
			return true;
		}
		
		return false;
	}
	
	public String toPrint() {
		return toString();
	}
	
	public String toPrint(Map<String, Object> params) {
		return toPrint();
	}
	
	public List<String> toPDF() {
		List<String> list = new ArrayList<String>();
		list.add(toPrint());
		
		return list;
	}
	
	public void print() {
		C.pl(toPrint());
	}
	
	public void print(Map<String, Object> params) {
		C.pl(toPrint(params));
	}
}
