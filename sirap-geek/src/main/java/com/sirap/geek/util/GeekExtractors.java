package com.sirap.geek.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;

public class GeekExtractors {
	
	public static List<String> shortOfProvinces() {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				String href = "http://home.51.com/mildzhao/diary/item/10047763.html";
				return href;
			}

			@Override
			protected void parse() {
				String regex = "\\[</font>(.+?)\\]</font>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = getPrettyText(ma.group(1));
					int len = temp.length();
					if(len == 1) {
						mexItems.add(temp);
					} else {
						mexItems.addAll(StrUtil.split(temp, '|'));
					}
				}
				C.pl(StrUtil.connect(mexItems));
			}
		};
		
		return neymar.process().getItems();	
	}
	
	public static List<String> wikiEpisodes() {
		List<String> list = Lists.newArrayList();
//		list.add("https://en.wikipedia.org/wiki/List_of_Arrow_episodes");
		list.add("https://en.wikipedia.org/wiki/List_of_Boardwalk_Empire_episodes");
//		list.add("https://en.wikipedia.org/wiki/List_of_Breaking_Bad_episodes");
//		list.add("https://en.wikipedia.org/wiki/List_of_Friends_episodes");
//		list.add("https://en.wikipedia.org/wiki/List_of_Game_of_Thrones_episodes");
//		list.add("https://en.wikipedia.org/wiki/List_of_Homeland_episodes");
//		list.add("https://en.wikipedia.org/wiki/List_of_The_Sopranos_episodes");
//		list.add("https://en.wikipedia.org/wiki/List_of_24_episodes");
		List<String> items = Lists.newArrayList();
		for(String url : list) {
			items.addAll(wikiEpisodes(url));
		}
		return items;
	}
	
	public static List<String> wikiEpisodes(String url) {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				return url;
			}

			@Override
			protected void parse() {
				String regexDrama = "List of <i>(.+?)</i> episodes";
				String drama = StrUtil.findFirstMatchedItem(regexDrama, source);
				String regex = "<h3><span id=\"Season_(\\d+)_.+?</h3>.*?(<table .*?</table>)";
				Matcher ma = createMatcher(regex);
				Set<String> one = new LinkedHashSet<>();
				while(ma.find()) {
					String season = StrUtil.padLeft(ma.group(1), 2, "0");
					String table = ma.group(2);
					one.addAll(episodeTitles(drama, season, table));
				}
				mexItems.addAll(one);
			}
			
			private List<String> episodeTitles(String drama, String season, String table) {
				String regexTR = "<td>(\\d{1,2})</td>\\s*(<td .*?</td>)";
				Matcher ma = createMatcher(regexTR, table);
				List<String> items = new ArrayList<>();

				while(ma.find()) {
					String temp = StrUtil.padLeft(ma.group(1), 2, "0");
					String title = getPrettyText(ma.group(2)).replaceAll("^\"", "").replaceAll("\"(\\[.+?\\]|)$", "");
					title = title.replace("?", "");
					String episode = StrUtil.occupy("S{0}E{1}", season, temp);
					String name = StrUtil.occupy("{0}.{1}.{2}", drama, episode, title);
					items.add(name);
				}
				
				return items;
			}
		};
		
		return neymar.process().getItems();		
	}

	public static List<String> fetchConciseHttpResponseCodes() {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				String location = "http://www.restapitutorial.com/httpstatuscodes.html";
				location = "E:/Mas/exp/codes.txt";
				return location;
			}

			@Override
			protected void parse() {
				List<String> TYPES = Lists.newArrayList();
				TYPES.add("Informational");
				TYPES.add("Success");
				TYPES.add("Redirection");
				TYPES.add("Client Error");
				TYPES.add("Server Error");
				
				String regex = "<a data-toggle=\"collapse\" data-target=[^<>]+>(\\d{3})([^<>]+)</a>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					String temp = "EGGS.put(\"{0}\", \"{1}, {2}\");";
					int index = Integer.parseInt(ma.group(1)) / 100;
//					D.pl(ma.group(1), index);
					String prefix = TYPES.get(index - 1);
					mexItems.add(StrUtil.occupy(temp, ma.group(1), prefix, ma.group(2).trim()));
				}
			}
		};
		
		return neymar.process().getItems();		
	}

	public static List<ValuesItem> fetchHttpResponseCodes() {
		Extractor<ValuesItem> neymar = new Extractor<ValuesItem>() {
			
			@Override
			public String getUrl() {
				String location = "http://tool.oschina.net/commons?type=5";
				return location;
			}

			@Override
			protected void parse() {
				String regex = "<tr>\\s*<td>\\s*(\\d+)\\s*</td>\\s*<td>([^<>]+)</td>\\s*</tr>";
				Matcher ma = createMatcher(regex);
				while(ma.find()) {
					ValuesItem vi = new ValuesItem();
					vi.add(ma.group(1));
					vi.add(ma.group(2));
					mexItems.add(vi);
				}
			}

		};
		
		return neymar.process().getItems();		
	}
		
	public static List<String> fetchJDK7Api(String apiPath, String methodName) {
		String dent = StrUtil.repeat(' ', 4);
		Extractor<String> neymar = new Extractor<String>() {

			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://tool.oschina.net/uploads/apidocs/jdk_7u4/{0}.html";
				return StrUtil.occupy(temp, apiPath);
			}
			
			@Override
			protected void parse() {
				source = HtmlUtil.removeComment(source);
				String temp = methodName;
				if(StrUtil.equals("...", methodName)) {
					temp = "[^<>]+";
				}
				String regex = "<a name=\"([^\"]+)\">\\s*</a>\\s*<ul [^<>]+>\\s*";
				regex += "<li [^<>]+>\\s*<h4>" + temp + "</h4>(.+?)</li>";
				Matcher ma = createMatcher(regex);
				int count = 0;
				while(ma.find()) {
					count++;
					parseBlock(count, ma.group(1), ma.group(2));
				}
			}
			
			private void parseBlock(int count, String anchor, String block) {
				String regex = "<pre>(.+?)</pre>\\s*<div[^<>]*>(.+?)</div>(\\s*<dl>(.+?)</dl>|)";
				Matcher ma = createMatcher(regex, block);
				while(ma.find()) {
					String temp = getUrl() + "#" + encodeURLParam(anchor);
					mexItems.add(count + ") " +getPrettyText(ma.group(1)) + dent + temp);
					List<String> cats = StrUtil.splitByRegex(ma.group(2), "<p>");
					boolean theFirst = true;
					for(String cat : cats) {
						String goodCat = getPrettyText(cat);
						if(EmptyUtil.isNullOrEmpty(goodCat)) {
							continue;
						}
						if(!theFirst) {
							mexItems.add("");
						}
						mexItems.add(goodCat);
						theFirst = false;
					}
					String dallas = ma.group(3);
					parseDallas(dallas);
				}
			}
			
			private void parseDallas(String dallas) {
				List<String> dogs = StrUtil.split(dallas, "<dt>");
				for(String dog : dogs) {
					if(EmptyUtil.isNullOrEmpty(dog)) {
						continue;
					}
					boolean hasMeaning = false;
					List<String> legs = StrUtil.split(dog, "</dt>");
					for(String leg : legs) {
						hasMeaning = true;
						boolean isClaw = false;
						String regex = "<dd>(.+?)</dd>";
						Matcher ma = createMatcher(regex, leg);
						while(ma.find()) {
							String temp = ma.group(0);
							mexItems.add(dent + getPrettyText(temp));
							isClaw = true;
						}
						
						if(!isClaw) {
							String goodLeg = getPrettyText(leg);
							if(!EmptyUtil.isNullOrEmpty(goodLeg)) {
								mexItems.add(goodLeg);
							}
						}
					}
					if(hasMeaning) {
						mexItems.add("");
					}
				}
			}
		};
		
		return neymar.process().getItems();
	}
}
