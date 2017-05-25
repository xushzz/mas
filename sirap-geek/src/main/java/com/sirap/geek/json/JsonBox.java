package com.sirap.geek.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

/**
 * So let us mark this day with remembrance, of who we are and how far we have traveled.
 * @creation 2016-07-25
 * @creator KY
 *
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class JsonBox {
	private static final String RANDOM_PREFIX = "@" + RandomUtil.letters(4) + "_";
	
	private static final String CHAR_CURL_LEFT = RANDOM_PREFIX + "CL@";
	private static final String CHAR_CURL_RIGHT = RANDOM_PREFIX + "CR@";
	private static final String CHAR_SQUARE_LEFT = RANDOM_PREFIX + "SL@";
	private static final String CHAR_SQUARE_RIGHT = RANDOM_PREFIX + "SR@";
	private static final String CHAR_COMMA = RANDOM_PREFIX + "CA@";
	private static final String CHAR_COLON = RANDOM_PREFIX + "CN@";
	
	private static final String CHAR_DB = "\\\"";
	private static final String CHAR_DOUBLE_QUOTE = RANDOM_PREFIX + "DB@";
	
	private static final String[] CHARS_ARR = {"{", "}", "[", "]", ",", ":"};
	private static final String[] CHARS_OCCUPY_ARR = {CHAR_CURL_LEFT, CHAR_CURL_RIGHT, CHAR_SQUARE_LEFT, CHAR_SQUARE_RIGHT, CHAR_COMMA, CHAR_COLON};
	
	private Map<String, Object> grandMap = new LinkedHashMap<>();

	private String fixedText;
	private String finalText;
	private String jsonText;
	private String lastObjectName;
	private Object king;
	
	public JsonBox(String jsonText) {
		this.jsonText = jsonText;
		process();
	}
	
	private void process() {
		if(EmptyUtil.isNullOrEmptyOrBlank(jsonText)) {
			XXXUtil.alert("Illegal json: " + jsonText);
		}
		
		init();
		finalText = occupyThings();
		if(isLegalJson()) {
			king = grandMap.get(lastObjectName);
			recoverElement(king);
			renameKeys();
		} else {
			XXXUtil.alert("Illegal json: [" + jsonText + "]");
		}
	}
	
	private void renameKeys() {
		Map map = new LinkedHashMap();
		Iterator it = grandMap.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			Object obj = grandMap.get(key);
			String key2 = (key + "").replace(RANDOM_PREFIX, ""); 
			map.put(key2, obj);
		}
		
		grandMap.clear();
		grandMap.putAll(map);
	}
	
	private void init() {
		String noQuote = replaceLiteralDoubleQuote(jsonText);
		String noSpecial = replaceSpecialCharsInsideDoubleQuotes(noQuote);
		
		fixedText = noSpecial;
	}

	public Object getKing() {
		return king;
	}

	public boolean isLegalJson() {
		if(lastObjectName == null) {
			return false;
		}
		
		boolean goodJson = StrUtil.equals(finalText, "\"" + lastObjectName + "\"" );
		
		return goodJson;
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
	
	private String replaceLiteralDoubleQuote(String source) {
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
	
	private static String recoverSpecialChars(String source) {
		String stuff = source;
		for(int i = 0; i < CHARS_ARR.length; i++) {
			stuff = stuff.replace(CHARS_OCCUPY_ARR[i], CHARS_ARR[i]);
		}
		
		stuff = stuff.replace(CHAR_DOUBLE_QUOTE, CHAR_DB);
		
		return stuff;
	}
	
	private String replaceSpecialCharsInsideDoubleQuotes(String source) {
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
	
	private String occupyThings() {
		StringBuffer sb = new StringBuffer(fixedText);
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
	
	private static boolean containsNoBracketInsideClosestCurls(String source) {
		String regex = "\\{([^\\{\\}]*)\\}";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.find()) {
			String stuff = m.group(1);
			return stuff.indexOf("[") < 0;
		}
		
		return false;
	}
	
	private static boolean containsNoCurlInsideClosestBrackets(String source) {
		String regex = "\\[([^\\[\\]]*)\\]";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.find()) {
			String stuff = m.group(1);
			return stuff.indexOf("{") < 0;
		}
		
		return false;
	}
	
	private boolean occupyCurls(StringBuffer source) {
		String regex = "\\{([^\\{\\}]*)\\}";
		Matcher m = Pattern.compile(regex).matcher(source);
		StringBuffer sb = new StringBuffer();
		boolean hasChange = false;
		if(m.find()) {
			hasChange = true;
			String stuff = m.group(1);
			Map<String, Object> map = createRegularMap(stuff);
			int countOfMap = grandMap.size() + 1;
			String key = RANDOM_PREFIX + "map@" + countOfMap;
			lastObjectName = key;
			m.appendReplacement(sb, "\"" + key + "\"");
			grandMap.put(key, map);
		}
		
		m.appendTail(sb);
		source.setLength(0);
		source.append(sb.toString());
		
		return hasChange;
	}
	
	private boolean occupyBrackets(StringBuffer source) {
		String regex = "\\[([^\\[\\]]*)\\]";
		Matcher m = Pattern.compile(regex).matcher(source);
		StringBuffer sb = new StringBuffer();
		boolean hasChange = false;
		if(m.find()) {
			hasChange = true;
			String stuff = m.group(1);
			List list = createRegularList(stuff);
			int countOfMap = grandMap.size() + 1;
			String key = RANDOM_PREFIX + "lis@" + countOfMap;
			lastObjectName = key;
			m.appendReplacement(sb, "\"" + key + "\"");
			grandMap.put(key, list);
		}
		
		m.appendTail(sb);
		source.setLength(0);
		source.append(sb.toString());
		
		return hasChange;
	}
	
	private static Map<String, Object> createRegularMap(String source) {
		Map<String, Object> map = new LinkedHashMap<>();
		
		List<String> items = StrUtil.split(source);
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
	private static String parseKey(String source) {
		String key = parseInsideDoubleQuote(source);
		if(key != null) {
			return key;
		} else {
			throw new MexException("This [" + source + "] is not a legal key.");
		}
	}

	private static String parseInsideDoubleQuote(String source) {
		String regex = "\"([^\"]*)\"";
		Matcher m = Pattern.compile(regex).matcher(source);
		if(m.matches()) {
			String key = m.group(1);
			return key;
		} else {
			return null;
		}
	}
	
	private static Object parseValue(String source) {
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
		List<String> items = StrUtil.split(listValue);
		List list = new ArrayList();
		for(String item : items) {
			Object subValue = parseValue(item.trim());
			list.add(subValue);
		}
		
		return list;
	}
}