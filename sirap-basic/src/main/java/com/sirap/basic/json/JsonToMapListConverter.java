package com.sirap.basic.json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;
import com.sirap.basic.util.XXXUtil;

import lombok.Data;

/***
 * https://www.sojson.com/in.html
 * @author carospop
 *
 */
public class JsonToMapListConverter {

	public Object fromJsonFile(String xmlPath) {
//		D.pl(xmlPath);
		String origin = IOUtil.readString(xmlPath);
		return fromJsonText(origin);
	}
	
	public Object fromJsonText(String origin) {
		String text = HtmlUtil.removeBlockComment(origin);
		Object crown = create(text);
		
		return crown;
	}
	
	private Stack<Character> begins = new Stack<>();
	private Stack<Integer> indexes = new Stack<>();
	
	Map<Character, Character> pairs = new LinkedHashMap<>();
	{
		pairs.put('{', '}');
		pairs.put('[', ']');
	}
	
	private char expectOf(char begin) {
		Character end = pairs.get(begin);
		return end;
	}
	
	private Object niceValue(String source) {
		XXXUtil.nullCheckOnly(source);
		
		if(source.isEmpty()) {
			return null;
		}
		
		Number number = StrUtil.numberOf(source);
		if(number != null) {
			return number;
		}
		
		if(StrUtil.equals(source, "true")) {
			return Boolean.TRUE;
		} else if(StrUtil.equals(source, "false")) {
			return Boolean.FALSE;
		}
		
		if(StrUtil.equalsCaseSensitive(source, "null")) {
			return null;
		}
		
		String string = getIfQuotedString(source);
//		D.pla(source, string);
		String nice = applyEscapes(string);
		
		return nice;
	}

	private String getIfQuotedString(String source) {
		String regex = "^\"(.*)\"$";
		String temp = StrUtil.findFirstMatchedItem(regex, source);
		if(temp == null) {
			return null;
		}

		for(int k = 0; k < temp.length(); k++) {
			char ch = temp.charAt(k);
			if(ch == '\"') {
				if(k == 0 || temp.charAt(k - 1) != '\\') {
					XXXUtil.alert("Expect a quoted string but: {0}", source);
				}
			}
		}
		
		return temp;
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
	
	public Object create(String current) {
		XXXUtil.shouldBeNotEmpty(current);
		char firstChar = current.charAt(0);
		
		if(firstChar != '[' && firstChar != '{') {
			return niceValue(current);
		}
		
		int startIndex = 0;
		boolean insideQuotes = false;
		List<String> kids = Lists.newArrayList();
		for(int nowIndex = 0; nowIndex < current.length(); nowIndex++) {
			char ch = current.charAt(nowIndex);
			if(ch == '\"') {
				if(nowIndex > 0 && current.charAt(nowIndex - 1) != '\\') {
					insideQuotes = !insideQuotes;
				}
			}
			
			if(insideQuotes) {
				continue;
			}
			
			if(!begins.isEmpty()) {
				char top = begins.peek();
				char expect = expectOf(top);
				if(ch == expect) {
					begins.pop();
					indexes.pop();
					continue;
				}
			}
			
			if(pairs.containsKey(ch)) {
				begins.push(ch);
				indexes.push(nowIndex);
				continue;
			}
			
			if(ch == ',') {
				//check if begins contains one and only one.
				if(begins.size() == 1) {
					String kid = current.substring(startIndex + 1, nowIndex).trim();
					kids.add(kid);
					startIndex = nowIndex;
				}
			}
		}
		
		String kid = current.substring(startIndex + 1, current.length() - 1).trim();
		kids.add(kid);
		
		if(firstChar == '[') {
			return createList(kids);
		} 
		
		if(firstChar == '{') {
			return createMap(kids);
		}
		
		XXXUtil.alert();
		
		return null;
	}
	
	private Map createMap(List<String> kids) {
		Map mars = new LinkedHashMap<>();
//		D.sink(kids);
		for(String kid : kids) {
			if(kid.trim().isEmpty()) {
				continue;
			}
			KeyValue kv = new KeyValue(kid);
//			D.pla(kid, kv.getKey(), kv.getValue());
			mars.put(kv.getKey(), create(kv.getValue()));
		}
		return mars;
	}
	
	private List createList(List<String> kids) {
		List list = Lists.newArrayList();
		for(String kid : kids) {
			if(kid.trim().isEmpty()) {
				continue;
			}
			Object value = create(kid);
			list.add(value);
		}
		
		return list;
	}
	
	@Data
	class KeyValue {
		private String key;
		private String value;
		
		public KeyValue(String expression) {
			splitByColon(expression);
		}
		
		/***
		 * "t,a:n'k": "di\"ng"
		 * @return
		 */
		private void splitByColon(String source) {
			source = source.trim();
			char quote = '\"';
			for(int k = 1; k < source.length(); k++) {
				char ch = source.charAt(k);
				if(ch == quote) {
					if(source.charAt(k - 1) != '\\') {
						String temp = source.substring(1, k).trim();
//						D.pla("KKK", temp);
						key = applyEscapes(temp);
						String remain = source.substring(k + 1);
						value = remain.replaceAll("^\\s*:\\s*", "").trim();
//						D.pla(remain, value);
						break;
					}
				}
			}
//			D.pl(source);
//			D.pla(key, value);
			XXXUtil.shouldBeNotnull(key);
			XXXUtil.shouldBeNotnull(value);
		}
	}
}

