package com.sirap.extractor.images;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Album;

public class Netease163Fetcher extends WebsiteImageLinksFetcher {

	@Override
	public Album fetch(String weburl) {
		return netease163Links(weburl);
	}

	//http://news.163.com/photoview/00AP0001/2297233.html
	public static Album netease163Links(String albumUrl) {
		D.at();
		Extractor<Album> frank = new Extractor<Album>() {
			
			@Override
			public String getUrl() {
				useGBK().showFetching();
				return albumUrl;
			}
			
			@Override
			protected void parse() {
				String regex = JsonUtil.createRegexKey("img");
				Matcher ma = createMatcher(regex);
				List<String> links = Lists.newArrayList();
				while(ma.find()) {
					String temp = ma.group(1);
					links.add(temp);
				}
				
				if(!links.isEmpty()) {
					regex = "<h1>([^<>]+)</h1>";
					String title = StrUtil.findFirstMatchedItem(regex, source);
					title = getPrettyText(title);
					item = new Album(title, links);
					item.setTag("ease");
				}
			}
		};
		
		frank.process();
		
		return frank.getItem();
	}

}
