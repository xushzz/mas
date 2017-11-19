package com.sirap.geek.util;

import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class GeekExtractors {

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
				String regex = "<a name=\"([^\"]+)\">\\s*</a>\\s*<ul class=\"blockList\">\\s*";
				regex += "<li class=\"blockList\">\\s*<h4>" + temp + "</h4>(.+?)</li>";
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
					mexItems.add(getPrettyText(ma.group(2)));
					String dallas = ma.group(3);
					parseDallas(dallas);
				}
			}
			
			private void parseDallas(String dallas) {
				List<String> dogs = StrUtil.split(dallas, "<dt>");
				boolean hasMeaning = false;
				for(String dog : dogs) {
					if(EmptyUtil.isNullOrEmpty(dog)) {
						continue;
					}
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
							mexItems.add(getPrettyText(leg));
						}
					}
				}
				if(hasMeaning) {
					mexItems.add("");
				}
			}
		};
		
		return neymar.process().getItems();
	}
}
