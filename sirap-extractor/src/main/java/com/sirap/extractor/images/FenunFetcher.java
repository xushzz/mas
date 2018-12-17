package com.sirap.extractor.images;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.domain.Album;

public class FenunFetcher extends WebsiteImageLinksFetcher {

	@Override
	public Album fetch(String weburl) {
		return of(weburl);
	}
	
	public static Album of(String personUrl) {
		Extractor<Album> frank = new Extractor<Album>() {
    		
			@Override
			public String getUrl() {
				showFetching();
				return personUrl;
			}
			
			@Override
			protected void parse() {
				String regex = "data-large-src=\"(.+?)\"";
				Matcher ma = createMatcher(regex);
				List<String> links = Lists.newArrayList();
				while(ma.find()) {
					links.add(ma.group(1));
				}

				String regex2 = "<h1>(.+?)</h1>";
				String title = StrUtil.findFirstMatchedItem(regex2, source);
				item = new Album(title, links).setTag("feng");
			}
		};
		
		return frank.process().getItem();
	}
}
