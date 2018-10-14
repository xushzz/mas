package com.sirap.basic.json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.common.collect.Lists;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;

public class JsonToMapListConverter {
	
	private static final Map<Character, Character> PAIRS = new LinkedHashMap<>();
	static {
		PAIRS.put('{', '}');
		PAIRS.put('[', ']');
	}

	public Object fromJsonFile(String xmlPath) {
		return fromJsonText(IOUtil.readString(xmlPath));
	}
	
	public Object fromJsonText(String origin) {
		String text = HtmlUtil.removeBlockComment(origin).trim();
		
		boolean toMap = origin.startsWith("{");
		boolean toList = origin.startsWith("[");	

		if(!toMap && !toList) {
			XXXUtil.alert("Should start with either { or [ but now: {0}", origin);
		}
		
		return parse(text);
	}
	
	public Object parse(String current) {
		XXXUtil.shouldBeNotEmpty(current);
		
		boolean toMap = current.startsWith("{");
		boolean toList = current.startsWith("[");
		
		if(!toMap && !toList) {
			return niceValueOf(current);
		}
		
		Stack<Character> begins = new Stack<>();
		Stack<Integer> indexes = new Stack<>();
		
		int startIndex = 0;
		boolean ledByStartQuote = false;
		List<String> kids = Lists.newArrayList();
		for(int nowIndex = 0; nowIndex < current.length(); nowIndex++) {
			char ch = current.charAt(nowIndex);
			if(ch == '\"') {
				if(StrUtil.isPrecededByEvenBackslashes(current, nowIndex)) {
					ledByStartQuote = !ledByStartQuote;
				}
			}
			
			if(ledByStartQuote) {
				continue;
			}
			
			if(!begins.isEmpty()) {
				char expect = expectOf(begins.peek());
				if(ch == expect) {
					begins.pop();
					if(begins.isEmpty() && nowIndex != current.length() - 1) {
						XXXUtil.alert("Expect empty but: {0}", current.substring(nowIndex + 1));
					}
					indexes.pop();
					continue;
				}
			}
			
			if(PAIRS.containsKey(ch)) {
				begins.push(ch);
				indexes.push(nowIndex);
				continue;
			}
			
			if(ch == ',' && begins.size() == 1) {
				String kid = current.substring(startIndex + 1, nowIndex).trim();
				XXXUtil.shouldBeNotEmpty(kid);
				kids.add(kid);
				startIndex = nowIndex;
			}
		}
		
		if(ledByStartQuote) {
			XXXUtil.alert("Expect the closing of \" but not found.", current);
		}
		
		if(!begins.isEmpty()) {
			char top = begins.peek();
			char expect = expectOf(top);
			XXXUtil.alert("Expect the closing of {0} like {1} but not found.", top, expect, current);
		}
		
		String kid = current.substring(startIndex + 1, current.length() - 1).trim();
		if(!kids.isEmpty()) {
			XXXUtil.shouldBeNotEmpty(kid);
		}
		
		if(!kid.isEmpty()) {
			kids.add(kid);
		}
		
		if(toList) {
			return createList(kids);
		} 
		
		if(toMap) {
			return createMap(kids);
		}
		
		XXXUtil.alert();
		
		return null;
	}
	
	private char expectOf(char begin) {
		return PAIRS.get(begin);
	}
	
	private Object niceValueOf(String source) {
		XXXUtil.shouldBeNotEmpty(source);
		
		Number number = StrUtil.numberOf(source);
		if(number != null) {
			return number;
		}
		
		if(StrUtil.equalsCaseSensitive(source, "true")) {
			return Boolean.TRUE;
		} else if(StrUtil.equalsCaseSensitive(source, "false")) {
			return Boolean.FALSE;
		}
		
		if(StrUtil.equalsCaseSensitive(source, "null")) {
			return null;
		}
		
		String string = quotedValueOf(source);
		
		return string;
	}

	private String quotedValueOf(String source) {
		String regex = "^\"(.*)\"$";
		String temp = StrUtil.findFirstMatchedItem(regex, source);
		if(temp == null) {
			XXXUtil.alert("Expect a quoted string but: {0}", source);
		}
		
		return applyEscapes(temp);
	}
	
	private String applyEscapes(String source) {
		String temp = source;
		temp = temp.replace("\\\"", "\"");
		temp = temp.replace("\\\\", "\\");
		temp = temp.replace("\\b", "\b");
		temp = temp.replace("\\f", "\f");
		temp = temp.replace("\\n", "\n");
		temp = temp.replace("\\r", "\r");
		temp = temp.replace("\\t", "\t");
		temp = XCodeUtil.replaceUnicodes(temp);
		
		return temp;
	}
	
	private Map createMap(List<String> kids) {
		Map mars = new LinkedHashMap<>();
		for(String expression : kids) {
			XXXUtil.shouldBeNotEmpty(expression);
			String[] kv = keyValueOf(expression);
			XXXUtil.shouldBeNotnull(kv);
			mars.put(kv[0], parse(kv[1]));
		}
		
		return mars;
	}
	
	private List createList(List<String> kids) {
		List list = Lists.newArrayList();
		for(String expression : kids) {
			XXXUtil.shouldBeNotEmpty(expression);
			list.add(parse(expression));
		}
		
		return list;
	}
	
	private String[] keyValueOf(String source) {
		source = source.trim();
		char quote = '\"';
		for(int k = 0; k < source.length(); k++) {
			char ch = source.charAt(k);
			if(k == 0) {
				if(ch != quote) {
					XXXUtil.alert("Expect {0} but {1}", quote, ch);
				}
				continue;
			}
			
			if(ch != quote) {
				continue;
			}
			
			if(!StrUtil.isPrecededByEvenBackslashes(source, k)) {
				continue;
			}
			
			String temp = source.substring(1, k).trim();
			String key = applyEscapes(temp);
			String value = StrUtil.parseParam("^\\s*:\\s*(.+?)", source.substring(k + 1));
			if(value == null || value.isEmpty()) {
				XXXUtil.alert("Expect a key-value pair but: {0}", source);
			}
			
			return new String[]{key, value};
		}
		
		XXXUtil.alert("You should not see this: " + source);
		return null;
	}
}

