package com.sirap.extractor.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.extractor.Extractor;

public class BaiduExtractorManager extends RssExtractorManager {
	
	public static final String HOME_BAIDU_BAIKE = "https://baike.baidu.com";
	private static BaiduExtractorManager instance;
	
	public static BaiduExtractorManager g() {
		if(instance == null) {
			instance = new BaiduExtractorManager();
		}
		
		return instance;
	}
	
	public List<MexObject> readAllRss() {
		Extractor<MexObject> justin = new Extractor<MexObject>() {
			
			@Override
			public String getUrl() {
				printFetching = true;
				useGBK();
				return "https://www.baidu.com/search/rss.html";
			}
			
			@Override
			protected void parseContent() {
				StringBuilder sb = new StringBuilder();
				sb.append("<span\\s?[^<>]*>([^<>]+)</span>\\s*");
				sb.append("<input\\s?[^<>]*\\s?value=\"http://news.baidu.com/n\\?cmd=([^\"]+)&class=([^\"]+)&[^\"]+\">");

				Matcher m = createMatcher(sb.toString(), source);
				while(m.find()) {
					String title = m.group(1);
					String code = m.group(2);
					String type = m.group(3);
					mexItems.add(new MexObject(title + " " + code + " " + type));
				}
			}
		};
		
		justin.process();
		
		return justin.getMexItems();
	}
	
	public List<MexObject> fetchRssByType(final String typeInfo) {
		XXXUtil.nullCheck(typeInfo, "type");
		
		String regex = "([14])(\\w+)";
		String[] params = StrUtil.parseParams(regex, typeInfo);
		String type;
		String code;
		if(params != null) {
			code = params[0];
			type = params[1];
		} else {
			code = "1";
			type = typeInfo;
		}
		
		Extractor<MexObject> justin = new Extractor<MexObject>() {
			
			@Override
			public String getUrl() {
				useUTF8();
				printFetching = true;
				String temp = "http://news.baidu.com/n?cmd={0}&class={1}&tn=rss";
				return StrUtil.occupy(temp, code, type);
			}
			
			@Override
			protected void parseContent() {
				StringBuilder sb = new StringBuilder();
				sb.append("<item>\\s*");
				sb.append(".*?");
				sb.append("<title>(.*?)</title>\\s*");
				sb.append(".*?");
				sb.append("<link>(.*?)</link>\\s*");
				sb.append(".*?");
				sb.append("<description>(.*?)</description>\\s*");
				sb.append(".*?");
				sb.append("</item>");
				
				Matcher m = createMatcher(sb.toString(), source);
				int count = 0;
				while(m.find()) {
					count++;
					String title = removeCDATA(m.group(1));
					String href = removeCDATA(m.group(2));
					String temp = removeCDATA(m.group(3));
					String summary = HtmlUtil.removeHttpTag(temp);
					String template = "#{0} {1}  {2}\n{3}\n";
					mexItems.add(new MexObject(StrUtil.occupy(template, count, title, href, summary)));
				}
			}
			
			private String removeCDATA(String value) {
				String temp = value.replace("<![CDATA[", "");
				temp = temp.replace("]]>", "");
				
				return temp;
			}
		};
		
		justin.process();
		
		return justin.getMexItems();
	}
	
	public List<MexObject> fetchBaiduSummary(final String keywordOrUrl, boolean withOtherSameNames) {
		if(StrUtil.isRegexFound("^https?://", keywordOrUrl)) {
			return fetchBaiduSummaryByURL(keywordOrUrl, withOtherSameNames);
		}
		
		if(StrUtil.isRegexFound("^(subview|item)/", keywordOrUrl)) {
			String fullUrl = HOME_BAIDU_BAIKE + "/" + keywordOrUrl;
			return fetchBaiduSummaryByURL(fullUrl, withOtherSameNames);
		}
		
		String realUrl = StrUtil.occupy(HOME_BAIDU_BAIKE + "/item/{0}", Extractor.encodeURLParam(keywordOrUrl));
		return fetchBaiduSummaryByURL(realUrl, withOtherSameNames);
	}
	
	public List<MexObject> fetchBaiduSummaryByURL(final String encodedUrl, boolean withOtherSameNames) {
		
		Extractor<MexObject> justin = new Extractor<MexObject>() {
			
			
			@Override
			public String getUrl() {
				printFetching = true;
				return encodedUrl;
			}

			@Override
			protected void parseContent() {
				String regexError = "<p class=\"sorryCont\">(.+?)</p>";
				String error = StrUtil.findFirstMatchedItem(regexError, source);
				if(error != null) {
					String temp = HtmlUtil.removeHttpTag(error).trim();
					mexItems.add(new MexObject(temp));
					return;
				}
				
				String regexSolid = "label-module=\"lemmaSummary\">(.+?)<div([^<>]+)class=\"basic-info cmn-clearfix\">";
				String solid = StrUtil.findFirstMatchedItem(regexSolid, source);
				if(EmptyUtil.isNullOrEmpty(solid)) {
					regexSolid = "label-module=\"lemmaSummary\">(.+?)<h2 class=\"block-title\">";
					solid = StrUtil.findFirstMatchedItem(regexSolid, source);
					if(EmptyUtil.isNullOrEmpty(solid)) {
						regexSolid = "label-module=\"lemmaSummary\">(.+?)<dl class=\"lemma-reference";
						solid = StrUtil.findFirstMatchedItem(regexSolid, source);
					}
				}
				
				XXXUtil.nullCheck(solid, ":something must be wrong with " + getUrl());
				
				String regex = "<div class=\"para\"[^<>]+>(.+?)</div>";
				List<String> items = StrUtil.findAllMatchedItems(regex, solid, 1);
				XXXUtil.nullCheck(solid, ":found nothing about " + regex);
				
				for(int i = 0; i < items.size(); i++) {
					String item = items.get(i);
					String temp = getPrettyText(item);
					temp = temp.replaceAll("\\[[\\d\\-]+\\]", "");
					mexItems.add(new MexObject(temp));
					if(i != items.size() - 1) {
						mexItems.add(new MexObject(""));
					}
				}
				
				if(withOtherSameNames) {
					String regexOthers = "<li class=\"item\">[^<>]*<a title=['\"]([^'\"]+)['\"] href=['\"]([^'\"]+)['\"].+?</li>";
					Matcher ma = createMatcher(regexOthers);
					List<MexObject> others = new ArrayList<>();
					int count = 0;
					while(ma.find()) {
						count++;
						String title = ma.group(1);
						String href = HOME_BAIDU_BAIKE + ma.group(2).replace("#viewPageContent", "");
						others.add(new MexObject("#" + count + " " + title + " " + href));
					}
					
					if(!others.isEmpty()) {
						mexItems.add(new MexObject(""));
						mexItems.add(new MexObject(StrUtil.occupy("Others {0}:", others.size())));
						mexItems.addAll(others);
					}
				}
			}
		};
		
		justin.process();
		
		return justin.getMexItems();
		
	}
}
