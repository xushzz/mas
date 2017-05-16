package com.sirap.basic.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;

public class JSONTest {
	String dir = "D:/KDB/tasks/0423_JsonParse/";

	String RANDOM_PREFIX = "@" + RandomUtil.letters(4, true);
	
	String CHAR_CURL_LEFT = RANDOM_PREFIX + "_CL@";
	String CHAR_CURL_RIGHT = RANDOM_PREFIX + "_CR@";
	String CHAR_SQUARE_LEFT = RANDOM_PREFIX + "_SL@";
	String CHAR_SQUARE_RIGHT = RANDOM_PREFIX + "_SR@";
	String CHAR_COMMA = RANDOM_PREFIX + "_CA@";
	String CHAR_COLON = RANDOM_PREFIX + "_CN@";
	
	String CHAR_DB = "\\\"";
	String CHAR_DOUBLE_QUOTE = RANDOM_PREFIX + "_DB@";
	
	String[] CHARS_ARR = {"{", "}", "[", "]", ",", ":"};
	String[] CHARS_OCCUPY_ARR = {CHAR_CURL_LEFT, CHAR_CURL_RIGHT, CHAR_SQUARE_LEFT, CHAR_SQUARE_RIGHT, CHAR_COMMA, CHAR_COLON};

	
	private Map<String, Object> grandMap = new HashMap<>();
	private String lastObjectName;

	@Test
	public void read() {
		String fileName = dir + "E5.txt";
		String source = IOUtil.readFileWithoutLineSeparator(fileName);
		String noQuote = replaceLiteralDoubleQuote(source);
		String noSpecial = replaceSpecialCharsInsideDoubleQuotes(noQuote);
		C.pl(noSpecial);
		
		String mapName = occupyThings(noSpecial);
		C.pl("OLENA:" + mapName);
		C.pl("OKONA:" + lastObjectName);
		boolean goodJson = StrUtil.equals(mapName, "\"" + lastObjectName + "\"" );
		D.pl(grandMap);
		C.pl("good Json? " + goodJson);
		if(goodJson) {
			Object king = grandMap.get(lastObjectName);
			D.pl();
			recoverElement(king);
			D.pl(king);
		}
	}
	
	private Object recoverElement(Object element) {
		if(element instanceof String) {
			String strObj = (String)element;
			
			Map subMap = getSubMap(strObj);
			if(subMap != null) {
				Object obj = recoverElement(subMap);
				return obj;
			}
			
			List subList = getSubList(strObj);
			if(subList != null) {
				Object obj = recoverElement(subList);
				return obj;
			}
		}
		
		if(element instanceof Map) {
			Map map = (Map)element;
			Iterator it = map.keySet().iterator();
			while(it.hasNext()) {
				Object key = it.next();
				Object obj = map.get(key);
				Object obj2 = recoverElement(obj);
				map.put(key, obj2);
			}
		}
		
		if(element instanceof List) {
			List list = (List)element;
			for(int index = 0; index < list.size(); index++) {
				Object obj = list.get(index);
				Object obj2 = recoverElement(obj);
				list.set(index, obj2);
			}
		}
		
		return element;
	}
	
	private Map getSubMap(Object mapName) {
		Object obj = grandMap.get(mapName);
		if(obj instanceof Map) {
			return (Map)obj;
		}
		
		return null;
	}
	
	private List getSubList(Object listName) {
		Object obj = grandMap.get(listName);
		if(obj instanceof List) {
			return (List)obj;
		}
		
		return null;
	}
	
	public String replaceLiteralDoubleQuote(String source) {
		String target = source.replace(CHAR_DB, CHAR_DOUBLE_QUOTE);
		return target;
	}
	
	private String occupySpecialChars(String source) {
		String stuff = source;
		for(int i = 0; i < CHARS_ARR.length; i++) {
			stuff = stuff.replace(CHARS_ARR[i], CHARS_OCCUPY_ARR[i]);
		}
		
		return stuff;
	}
	
	private String recoverSpecialChars(String source) {
		String stuff = source;
		for(int i = 0; i < CHARS_ARR.length; i++) {
			stuff = stuff.replace(CHARS_OCCUPY_ARR[i], CHARS_ARR[i]);
		}
		
		stuff = stuff.replace(CHAR_DOUBLE_QUOTE, CHAR_DB);
		
		return stuff;
	}
	
	public String replaceSpecialCharsInsideDoubleQuotes(String source) {
		String regex = "\"([^\"]*)\"";
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile(regex).matcher(source);
		while(m.find()) {
			String stuff = m.group(0);
			stuff = occupySpecialChars(stuff);
			
			m.appendReplacement(sb, stuff);
		}
		
		m.appendTail(sb);
		
		return sb.toString();
	}
	
//	@Test
	public void testBrack() {
		String source = "[abc],mn,[fg]";
		String target = replaceSpecialCharsInsideSquareBrackets(source);
		C.pl(source);
		C.pl(target);
	}
	
	public String replaceSpecialCharsInsideSquareBrackets(String source) {
		String regex = "\\[([^\\[\\]]*)\\]";
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile(regex).matcher(source);
		while(m.find()) {
			String stuff = m.group(0);
			stuff = stuff.replace(",", CHAR_COMMA);
			D.sink(stuff);
			m.appendReplacement(sb, stuff);
		}
		
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	public String occupyThings(String source) {
		StringBuffer sb = new StringBuffer(source);
		boolean needToOccupy = true;
		
		while(needToOccupy) {
			boolean flagB = containsNoBracketInsideClosestCurls(sb.toString());
			if(flagB) {
				boolean hasChange = occupyCurls(sb);
				if(hasChange) {
					continue;
				}
			}
			
			boolean flagC = containsNoCurlInsideClosestBrackets(sb.toString());
			if(flagC) {
				boolean hasChange = occupyBrackets(sb);
				if(hasChange) {
					continue;
				}
			}
			
			break;
		}
		
		String value = sb.toString();
		
		return value;
	}
	
	private boolean containsNoBracketInsideClosestCurls(String source) {
		String regex = "\\{([^\\{\\}]*)\\}";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.find()) {
			String stuff = m.group(1);
			return stuff.indexOf("[") < 0;
		}
		
		return false;
	}
	
	private boolean containsNoCurlInsideClosestBrackets(String source) {
		String regex = "\\[([^\\[\\]]*)\\]";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.find()) {
			String stuff = m.group(1);
			return stuff.indexOf("{") < 0;
		}
		
		return false;
	}
	
	public boolean occupyCurls(StringBuffer source) {
		String regex = "\\{([^\\{\\}]*)\\}";
		Matcher m = Pattern.compile(regex).matcher(source);
		StringBuffer sb = new StringBuffer();
		boolean hasChange = false;
		if(m.find()) {
			hasChange = true;
			String stuff = m.group(1);
			Map<String, Object> map = createRegularMap(stuff);
			int countOfMap = grandMap.size() + 1;
			String key = RANDOM_PREFIX + "_map@" + countOfMap;
			lastObjectName = key;
			m.appendReplacement(sb, "\"" + key + "\"");
			grandMap.put(key, map);
		}
		
		m.appendTail(sb);
		source.setLength(0);
		source.append(sb.toString());
		
		return hasChange;
	}
	
	public boolean occupyBrackets(StringBuffer source) {
		String regex = "\\[([^\\[\\]]*)\\]";
		Matcher m = Pattern.compile(regex).matcher(source);
		StringBuffer sb = new StringBuffer();
		boolean hasChange = false;
		if(m.find()) {
			hasChange = true;
			String stuff = m.group(1);
			List list = createRegularList(stuff);
			int countOfMap = grandMap.size() + 1;
			String key = RANDOM_PREFIX + "_list@" + countOfMap;
			lastObjectName = key;
			m.appendReplacement(sb, "\"" + key + "\"");
			grandMap.put(key, list);
		}
		
		m.appendTail(sb);
		source.setLength(0);
		source.append(sb.toString());
		
		return hasChange;
	}
	
	public Map<String, Object> createRegularMap(String source) {
		Map<String, Object> map = new HashMap<>();
		
		List<String> items = StrUtil.split(source, ",");
		for(String item : items) {
			 String entry = item.trim();
			 String[] keyValue = entry.split(":");
			 if(keyValue.length != 2) {
				 throw new MexException("This [" + entry + "] is not a legal entry.");
			 }
			 
			 String tempKey = keyValue[0].trim();
			 tempKey = parseKey(tempKey);
			 String key = recoverSpecialChars(tempKey);
			 
			 String tempValue = keyValue[1].trim();
			 Object value = parseValue(tempValue);
			 
			 map.put(key, value);
		}
		
		return map;
	}
	
	//"id": "2014-11\"-04_15-03-38",
	public String parseKey(String source) {
		String key = parseInsideDoubleQuote(source);
		if(key != null) {
			return key;
		} else {
			throw new MexException("This [" + source + "] is not a legal key.");
		}
	}

	public String parseInsideDoubleQuote(String source) {
		String regex = "\"([^\"]*)\"";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.matches()) {
			String key = m.group(1);
			return key;
		} else {
			return null;
		}
	}
	
//	@Test
	public void bracket() {
		String source = "[12, [23], 34]";
		String result = parseInsideSquareBracket(source);
		C.pl(result);
	}
	
	public String parseInsideSquareBracket(String source) {
		String regex = "\\[([^\\[\\]]*)\\]";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.matches()) {
			C.pl();
			String key = m.group(1);
			return key;
		} else {
			return null;
		}
	}
	
	public Object parseValue(String source) {
		BigDecimal bd = MathUtil.toBigDecimal(source);
		if(bd != null) {
			return bd;
		}
		
		if(StrUtil.equals(source, "true")) {
			return Boolean.TRUE;
		} else if(StrUtil.equals(source, "false")) {
			return Boolean.FALSE;
		}
		
		if(StrUtil.equalsCaseSensitive(source, "null")) {
			return null;
		}
		
		String strValue = parseInsideDoubleQuote(source);
		if(strValue != null) {
			String stuff = recoverSpecialChars(strValue);
			return stuff;
		}
		
		throw new MexException("This [" + source + "] is not a legal value.");
	}
	
	private List createRegularList(String listValue) {
		List<String> items = StrUtil.split(listValue, ",");
		List list = new ArrayList();
		for(String item : items) {
			Object subValue = parseValue(item.trim());
			list.add(subValue);
		}
		
		return list;
	}
}
