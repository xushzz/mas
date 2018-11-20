package com.sirap.extractor.images;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.domain.Album;

public class SinaFetcher extends WebsiteImageLinksFetcher {

	@Override
	public Album fetch(String albumUrl) {
		if(StrUtil.contains(albumUrl, "sina.com")) {
			return sinaLinks(albumUrl);
		} else if(StrUtil.contains(albumUrl, "sina.cn")) {
			return sinaLinksBySrc(albumUrl);
		} else {
			XXXUtil.alerto("Not yet supported website: ", albumUrl);
			return null;
		}
	}

	public static Album sinaLinks(final String albumUrl) {
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return albumUrl;
			}
			
			@Override
			protected void parse() {
				item = sinaLinksByImageUrl(source);
				if(item == null) {
					item = sinaLinksByDataSrc(source);
				}
				if(item != null) {
					item.setTag("sina");
				}
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}
	
	//http://slide.sports.sina.com.cn/cba/slide_2_792_193598.html
	private static Album sinaLinksByImageUrl(String htmlContent) {
		D.at();
		String regex = JsonUtil.createRegexKey("image_url");
		Matcher ma = StrUtil.createMatcher(regex, htmlContent);
		List<String> links = Lists.newArrayList();
		while(ma.find()) {
			String temp = ma.group(1);
			temp = temp.replace("\\", "");
			links.add(temp);
		}
		
		if(!links.isEmpty()) {
			regex = "<meta itemprop=\"name\" content=\"([^\"]+)\"\\s+/>";
			String title = StrUtil.findFirstMatchedItem(regex, htmlContent);
			
			return new Album(title, links);
		}
		
		return null;
	}
	
	//http://slide.news.sina.com.cn/z/slide_1_64237_325800.html#p=3
	private static Album sinaLinksByDataSrc(String htmlContent) {
		D.at();
		String regex = "data-src=\"([^\"]+)\"";
		Matcher ma = StrUtil.createMatcher(regex, htmlContent);
		List<String> links = Lists.newArrayList();
		while(ma.find()) {
			String temp = ma.group(1);
			temp = temp.replace("\\", "");
			links.add(temp);
		}
		
		if(!links.isEmpty()) {
			regex = "<h1 class=\"name\">([^<>]+)</h1>";
			String title = StrUtil.findFirstMatchedItem(regex, htmlContent);
			
			return new Album(title, links);
		}
		
		return null;
	}

	//https://photo.sina.cn/album_2_786_197690.htm?vt=4&hd=1
	private static Album sinaLinksBySrc(String albumUrl) {
		D.at();
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return useHttps(albumUrl);
			}
			
			@Override
			protected void parse() {
				String regex = "__webURL:'([^']+)'";
				String webUrl = StrUtil.findFirstMatchedItem(regex, source);
				item = sinaLinks(webUrl);
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}
}
