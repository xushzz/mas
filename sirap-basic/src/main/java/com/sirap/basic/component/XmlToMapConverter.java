package com.sirap.basic.component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
//import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.basic.util.XmlUtil;

import lombok.AllArgsConstructor;

public class XmlToMapConverter {

	@SuppressWarnings("rawtypes")
	public Map toMapFromXmlFile(String xmlPath) {
//		D.pl(xmlPath);
		String origin = IOUtil.readString(xmlPath);
		return toMapFromXmlText(origin);
	}
	@SuppressWarnings("rawtypes")
	public Map toMapFromXmlText(String origin) {
		String text = XmlUtil.removeHeaderCommentCDATA(origin);
//		D.pl(text);
		List<String> words = breakIntoList(text);
//		D.list(words);
		Object crown = process(words);
		XXXUtil.shouldBeTrue(crown instanceof Map);
		return (Map)crown;
	}
	
	private List<String> breakIntoList(String wordstr) {
		XmlWordsReader pogba = new XmlWordsReader(wordstr);
		return pogba.read();
	}
	
	private Object process(List<String> words) {
		if(words.isEmpty()) {
			return "";
		}
		
		if(words.size() == 1) {
			return words.get(0);
		}
		
		Stack<String> basket = new Stack<>();
		Stack<Integer> indexes = new Stack<>();
		List<List<String>> groups = Lists.newArrayList();
		for(int k = 0; k < words.size(); k++) {
			String word = words.get(k);
			if(isStart(word)) {
				basket.push(word);
				indexes.push(k);
				continue;
			}
			if(isEnd(word)) {
				String expect = endOf(basket.peek());
				boolean flag = StrUtil.equals(word, expect);
//				if(!flag) {
//					D.pl(basket);
//					D.pla(word, expect, flag);
//				}
				XXXUtil.shouldBeTrue(flag);
				basket.pop();
				int start = indexes.pop();
				if(basket.isEmpty()) {
					int end = k;
					List<String> group = words.subList(start, end + 1);
					groups.add(group);
				}
			}
		}
//		D.pl(basket);
		XXXUtil.shouldBeTrue(basket.isEmpty());
		
		if(groups.size() == 1) {
			List<String> group = groups.get(0);
			Map<String, Object> mars = mapOfGroup(group);
			
			return mars;
		} else {
			List<Object> gists = Lists.newArrayList();
			for(List<String> group : groups) {
				Map<String, Object> mars = mapOfGroup(group);
				gists.add(mars);
			}
			return gists;
		}
	}
	
	private Map<String, Object> mapOfGroup(List<String> group) {
		String wordx = group.get(0);
		String name = nameOf(wordx);
//		D.pla(name, group.get(0));
//		D.list(group);
		List<String> kids = group.subList(1, group.size() - 1);
		Map<String, Object> mars = new LinkedHashMap<>();
		Map<String, String> attrs = mapOfProperties(wordx);
		if(!EmptyUtil.isNullOrEmpty(attrs)) {
			mars.put("attrs", mapOfProperties(wordx));
		}
		
		Object gist = process(kids);
		mars.put(name, gist);

		return mars;
	}

	private static String endOf(String wordx) {
		XXXUtil.shouldBeTrue(isStart(wordx));
		String name = nameOf(wordx);
		String expect = StrUtil.occupy("</{0}>", name);
		
		return expect;
	}
	
	private static boolean isStart(String text) {
		return StrUtil.isRegexFound("^<[^/]", text);
	}

	private static boolean isEnd(String text) {
		return StrUtil.isRegexFound("^</", text);
	}
	
	/***
	 * <OK>
	 * <OK id="1918">
	 * @param wordx
	 * @return
	 */
	private static String nameOf(String wordx) {
		String regex = "([^\\s<>]+)";
		String name = StrUtil.findFirstMatchedItem(regex, wordx);
		
		return name;
	}
	
	private static Map<String, String> mapOfProperties(String wordx) {
		String regex = "([^\\s=]+)=\"([^\\s=]+)\"";
		Matcher ma = StrUtil.createMatcher(regex, wordx);
		Map<String, String> props = new LinkedHashMap<>();
		while(ma.find()) {
			props.put(ma.group(1), ma.group(2));
		}
		
		return props;
	}
	
	@AllArgsConstructor
	class XmlWordsReader {
		
		public XmlWordsReader(String origin) {
			this.origin = origin;
		}
		private String origin;
		private List<String> words = Lists.newArrayList();
		private Stack<Character> barrel = new Stack<>();
		
		public List<String> read() {
			char expect = '>';
			for(int i = 0; i < origin.length(); i++) {
				char ball = origin.charAt(i);
				if(ball != expect) {
					barrel.push(ball);
					continue;
				}
				if(expect == '>') {
					barrel.push(ball);
					createWord();
				} else {
					createWord();
					barrel.push(ball);
				}
				
				expect = nextExpect(expect);
			}
			XXXUtil.shouldBeTrue(barrel.isEmpty());
			
			return words;
		}
		
		private boolean createWord() {
			String str = popAllDrips(barrel).trim();
			if(str.isEmpty()) {
				return false;
			}
			
			List<String> pair = niceBlank(str);
			if(pair != null) {
				words.addAll(pair);
			} else {
				if(StrUtil.startsWith(str, "<")) {
					words.add(str);
				} else {
					words.add(str);
				}
			}
			
			return true;
		}

		/***
		 * <OK/>
		 * <OK id="1918" />
		 * @param text
		 * @return
		 */
		private List<String> niceBlank(String word) {
			String regex = "<([^\\s<>]+)(|.+?)/>";
			String[] params = StrUtil.parseParams(regex, word);
			if(params == null) {
				return null;
			}

			String name = params[0];
			String start = word.replace("/", "");
			String end = StrUtil.occupy("</{0}>", name);
			List<String> group = Lists.newArrayList(start, end);

			return group;
		}
		
		private char nextExpect(char current) {
			if(current == '>') return '<';
			if(current == '<') return '>';
			return 'x';
		}
		
		private String popAllDrips(Stack<?> waters) {
			StringBuffer sb = StrUtil.sb();
			while(!waters.isEmpty()) {
				sb.append(waters.pop());
			}
			
			String item = sb.reverse().toString();
			
			return item;
		}
	}
}

