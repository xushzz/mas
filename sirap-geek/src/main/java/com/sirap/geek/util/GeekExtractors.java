package com.sirap.geek.util;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class GeekExtractors {

	public static List<String> fetchConciseHttpResponseCodes() {
		Extractor<String> neymar = new Extractor<String>() {
			
			@Override
			public String getUrl() {
				String location = "http://www.restapitutorial.com/httpstatuscodes.html";
				location = "E:/Mas/exp/codes.txt";
				return location;
			}

			@Override
			protected void parseContent() {
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
			protected void parseContent() {
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
			protected void parseContent() {
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
