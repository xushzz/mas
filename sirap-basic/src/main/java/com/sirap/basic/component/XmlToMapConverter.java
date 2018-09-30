package com.sirap.basic.component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.D;
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
		Object crown = analyze(words);
		XXXUtil.shouldBeTrue(crown instanceof Map);
		return (Map)crown;
	}
	
	private List<String> breakIntoList(String wordstr) {
		XmlWordsReader pogba = new XmlWordsReader(wordstr);
		return pogba.read();
	}
	
	
	private Object analyze(List<String> words) {
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
//				D.pla(word, basket);
				String expect = endOf(basket.peek());
				boolean flag = StrUtil.equals(word, expect);
//				D.pl(basket);
				if(!flag) {
					D.pla(word, expect, flag);
				}
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
		
		XXXUtil.shouldBeTrue(basket.isEmpty());
		
		if(groups.size() == 1) {
			List<String> group = groups.get(0);
			Map<String, Object> mars = mapOf(group);
			
			return mars;
		} else {
			List<Object> gists = Lists.newArrayList();
			for(List<String> group : groups) {
				Map<String, Object> mars = mapOf(group);
				gists.add(mars);
			}
			return gists;
		}
	}
	
	private Map<String, Object> mapOf(List<String> group) {
		String name = nameOf(group.get(0));
//		D.list(group);
		List<String> kids = group.subList(1, group.size() - 1);
		Map<String, Object> mars = new LinkedHashMap<>();
		Object gist = analyze(kids);
		mars.put(name, gist);

		return mars;
	}

	private static String nameOf(String text) {
		String temp = text.replaceAll("<|</|>", "");
		return temp;
	}
	
	private static String endOf(String text) {
		XXXUtil.shouldBeTrue(isStart(text));
		String expect = text.replace("<", "</");
		
		return expect;
	}
	
	private static boolean isStart(String text) {
		return StrUtil.isRegexFound("^<[^/]", text);
	}

	private static boolean isEnd(String text) {
		return StrUtil.isRegexFound("^</", text);
	}
	
	@AllArgsConstructor
	class XmlWordsReader {
		
		public XmlWordsReader(String words) {
			this.words = words;
		}
		private String words;
		private List<String> balls = Lists.newArrayList();
		private Stack<Character> barrel = new Stack<>();
		
		public List<String> read() {
			char expect = '>';
			for(int i = 0; i < words.length(); i++) {
				char drip = words.charAt(i);
				if(drip != expect) {
					barrel.push(drip);
					continue;
				}
				if(expect == '>') {
					barrel.push(drip);
					pourWater();
				} else {
					pourWater();
					barrel.push(drip);
				}
				
				expect = nextExpect(expect);
			}
			XXXUtil.shouldBeTrue(barrel.isEmpty());
			
			return balls;
		}
		
		private boolean pourWater() {
			String str = popAllDrips(barrel).trim();
			if(str.isEmpty()) {
				return false;
			}
			
			List<String> pair = niceBlank(str);
			if(pair != null) {
				balls.addAll(pair);
			} else {
				if(StrUtil.startsWith(str, "<")) {
					str = removeAttrs(str);
					str = str.replaceAll("\\s+", "");
					balls.add(str);
				} else {
					balls.add(str);
				}
				
				
				
			}
			
			return true;
		}

		/***
		 * <OK/>
		 * <OK  />
		 * @param text
		 * @return
		 */
		private String nameOfBlank(String text) {
			String regex = "<([^<>]*)\\s*/>";
			String param = StrUtil.parseParam(regex, text);
			
			return param;
		}
		
		private List<String> niceBlank(String word) {
			String name = nameOfBlank(word);
			if(name == null) {
				return null;
			}

			List<String> group = Lists.newArrayList();
			name = removeAttrs(name);
			group.add("<" + name + ">");
			group.add("</" + name + ">");

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
		
		/***
		 * <dependency name="okthen">
		 * <dependency/>
		 * @param text
		 * @return
		 */
		private String removeAttrs(String text) {
			if(isStart(text)) {
				String regex = "\\s+[^<>]+\\s*";
				String temp = text.replaceAll(regex, "");
				
				return temp;
			}
			
			if(isEnd(text)) {
				return text.replaceAll("\\s*", "");
			}
			
			return text;
		}
	}
}

