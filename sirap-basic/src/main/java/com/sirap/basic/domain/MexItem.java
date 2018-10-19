package com.sirap.basic.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.search.FileSizeCriteria;
import com.sirap.basic.search.MexFilter;
import com.sirap.basic.search.SizeCriteria;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

@SuppressWarnings("serial")
public abstract class MexItem implements Serializable {

	public static final String SYMBOL_CARET = "^";
	public static final String SYMBOL_USD = "$";
	public static final String SYMBOL_ASTERISK = "*";
	public static final String CHARS_REGEX_ESCAPE = "\\$()*+.[]?^{}|";

	protected boolean caseSensitive;
	protected int pseudoOrder;
	
	public static MexItem ofObject(Object obj) {
		if(obj instanceof List) {
			return ValuesItem.of(obj);
		} 
		
		if(obj instanceof MexItem) {
			return (MexItem)obj;
		}

		return new MexObject(obj);
	}

	public boolean parse(String str) {
		return false;
	}
	
	public boolean parse(List list) {
		return false;
	}
	
	public int getPseudoOrder() {
		return pseudoOrder;
	}

	public void setPseudoOrder(int pseudoOrder) {
		this.pseudoOrder = pseudoOrder;
	}

	public boolean isMexMatched(String mexCriteria) {
		return isMexMatched(mexCriteria, false);
	}

	public boolean isMexMatched(String mexCriteria, boolean sensitive) {
		return isMexMatched(mexCriteria, sensitive, false);
	}
	
	public boolean isMexMatched(String mexCriteria, boolean sensitive, boolean stay) {
		MexFilter<MexItem> filter = new MexFilter<MexItem>(mexCriteria, this);
		filter.setCaseSensitive(sensitive);
		filter.setNoSplit(stay);
		List<MexItem> result = filter.process();
		
		return !EmptyUtil.isNullOrEmpty(result);
	}
	
	public boolean isMatched(String keyWord, boolean caseSensitive) {
		return isMatched(keyWord);
	}
	
	public boolean isMatched(String keyWord) {
		return false;
	}
	
	protected String parseWholeMatched(String keyWord) {
		if(keyWord != null && keyWord.length() > 2 && keyWord.startsWith(SYMBOL_CARET) && keyWord.endsWith(SYMBOL_USD)) {
			String temp = keyWord.substring(1, keyWord.length() - 1);
			return temp;
		}
		
		return null;
	}

	protected String parseLeftMatched(String keyWord) {
		if(keyWord != null && keyWord.length() > 1 && keyWord.startsWith(SYMBOL_CARET) && !keyWord.endsWith(SYMBOL_USD)) {
			return keyWord.substring(1);
		}
		
		return null;
	}
	
	protected String parseRightMatched(String keyWord) {
		if(keyWord != null && keyWord.length() > 1 && keyWord.endsWith(SYMBOL_USD) && !keyWord.startsWith(SYMBOL_CARET)) {
			return keyWord.substring(0, keyWord.length() - 1);
		}
		
		return null;
	}
	
	protected boolean isRegexMatched(String source, String keyWord) {
		
		String wholeMatchedStr = parseWholeMatched(keyWord);
		if(wholeMatchedStr != null && StrUtil.equals(wholeMatchedStr, source)) {
			return true;
		}
		
		String leftMatchedStr = parseLeftMatched(keyWord);
		if(leftMatchedStr != null && StrUtil.startsWith(source, leftMatchedStr)) {
			return true;
		}
		
		String rightMatchedStr = parseRightMatched(keyWord);
		if(rightMatchedStr != null && StrUtil.endsWith(source, rightMatchedStr)) {
			return true;
		}
		
		String genuineRegex = StrUtil.parseParam("rx:(.+)", keyWord);
		if(genuineRegex != null && StrUtil.isRegexFound(genuineRegex, source)) {
			return true;
		}
		
		return false;
	}

	public List toList() {
		return toList("");
	}

	public List toList(String options) {
		String method = D.current().getMethodName();
		XXXUtil.alert("Method {0}.{1}(String) must be overriden.", getClass().getName(), method);
		return null;
	}
	
	public String toJson() {
		return JsonUtil.quote(toString());
	}
	
	public String toPrettyJson(int depth) {
		return JsonUtil.quote(toString());
	}
	
	public String toPrint() {
		return toPrint("");
	}
	
	public String toPrint(String options) {
		String connector = OptionUtil.readString(options, "c", "  ");
		return StrUtil.connect(toList(options), connector);
	}
	
	public static StringBuffer sb() {
		return new StringBuffer();
	}
	
	public SizeCriteria getSizeCriteria(String source) {
		SizeCriteria quinn = new SizeCriteria();
		if(quinn.parse(source)) {
			return quinn;
		} else {
			return null;
		}
	}
	
	public SizeCriteria getFileSizeCriteria(String source) {
		SizeCriteria quinn = new FileSizeCriteria();
		if(quinn.parse(source)) {
			return quinn;
		} else {
			return null;
		}
	}
	
	public boolean showOrder(String options) {
		return OptionUtil.readBooleanPRI(options, "so", false);
	}
	
	protected String trimRight(String stringEndedWithSpaces) {
		if(stringEndedWithSpaces == null) {
			return null;
		}
		return stringEndedWithSpaces.replaceAll("\\s*$", "");

	}
}
