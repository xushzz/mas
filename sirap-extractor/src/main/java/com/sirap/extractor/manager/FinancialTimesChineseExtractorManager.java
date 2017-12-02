package com.sirap.extractor.manager;

import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class FinancialTimesChineseExtractorManager extends RssExtractorManager {
	private static FinancialTimesChineseExtractorManager instance;
	
	public static FinancialTimesChineseExtractorManager g() {
		if(instance == null) {
			instance = new FinancialTimesChineseExtractorManager();
		}
		
		return instance;
	}
	
	public List<MexObject> readAllRss() {
		Extractor<MexObject> justin = new Extractor<MexObject>() {
			
			@Override
			public String getUrl() {
				printFetching = true;
				return "http://www.ftchinese.com/channel/rss.html";
			}
			
			@Override
			protected void parseContent() {
				StringBuilder sb = new StringBuilder();
				sb.append("<dt>(.+?)</dt>\\s*");
				sb.append("<dd>[^<>]*<span>\\s*");
				sb.append("(.+?)</dd>");

				Matcher m = createMatcher(sb.toString(), source);
				while(m.find()) {
					String title = HtmlUtil.removeHttpTag(m.group(1)).trim();
					String href = HtmlUtil.removeHttpTag(m.group(2)).trim();
					String type = href.replace("http://www.ftchinese.com/rss/", "");
					mexItems.add(new MexObject(title + ", " + type));
				}
			}
		};
		
		justin.process();
		
		return justin.getItems();
	}
	
	public List<MexObject> fetchRssByType(final String type) {
		XXXUtil.nullCheck(type, "type");

		Extractor<MexObject> justin = new Extractor<MexObject>() {
			
			@Override
			public String getUrl() {
				printFetching = true;
				String temp = "http://www.ftchinese.com/rss/{0}";
				return StrUtil.occupy(temp, type);
			}
			
			@Override
			protected void parseContent() {
				StringBuilder sb = new StringBuilder();
				sb.append("<item>\\s*");
				sb.append(".*?");
				sb.append("<title>(.*?)</title>\\s*");
				sb.append(".*?");
				sb.append("<description>(.*?)</description>\\s*");
				sb.append(".*?");
				sb.append("<link>(.*?)</link>\\s*");
				sb.append(".*?");
				sb.append("<pubDate>(.*?)</pubDate>\\s*");
				sb.append(".*?");
				sb.append("</item>");
				
				Matcher m = createMatcher(sb.toString(), source);
				int count = 0;
				while(m.find()) {
					count++;
					String title = removeCDATA(m.group(1));
					String tempDescription = removeCDATA(m.group(2));
					String when = m.group(4);
					tempDescription = tempDescription.replaceAll("\\s*<br><p>", ", " + when + "\n");
					String description = HtmlUtil.removeHttpTag(tempDescription).trim();
					String link = removeCDATA(m.group(3));
					String template = "#{0} {1}  {2}\n{3}\n";
					
					mexItems.add(new MexObject(StrUtil.occupy(template, count, title, link, description)));
				}
			}
			
			private String removeCDATA(String value) {
				String temp = value.replace("<![CDATA[", "");
				temp = temp.replace("]]>", "");
				
				return temp;
			}
		};
		
		justin.process();
		
		return justin.getItems();
	}
}
