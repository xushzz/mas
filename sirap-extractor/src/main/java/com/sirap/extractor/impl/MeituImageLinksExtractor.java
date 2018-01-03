package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.extractor.konstants.MeituKonstants;

public class MeituImageLinksExtractor extends Extractor<MexObject> {

	private String urlInfo;
	private int countOfAlbum;
	
	public MeituImageLinksExtractor(String urlInfo) {
		this.urlInfo = urlInfo;
	}
	
	public int getCountOfAlbum() {
		return countOfAlbum;
	}

	@Override
	public String getUrl() {
		useUTF8();
		printFetching = true;
		String temp = urlInfo.replaceAll("/+$", "");
		String fullUrl = temp;
		if(!fullUrl.startsWith(MeituKonstants.HOME_PAGE)) {
			fullUrl = StrUtil.useSlash(MeituKonstants.HOME_PAGE, fullUrl);
		}
		return fullUrl;
	}
	
	@Override
	protected void parse() {
		String regexCount = "<div class=\"shoulushuliang\">[^<>]+<span>(\\d+)</span>[^<>]+</div>";
		Matcher ma = createMatcher(regexCount);
		if(ma.find()) {
			countOfAlbum = Integer.parseInt(ma.group(1));
		}
		
		String regex = "<img src=\"([^\"]+)\"></a>\\s*<span class=\"shuliang\">(\\d+)P</span>";
		Matcher m = createMatcher(regex);
		while(m.find()) {
			String href = m.group(1);
			int count = Integer.parseInt(m.group(2));
			mexItems.addAll(explode(href, count));
		}
	}
	
	public List<MexObject> explode(String base, int count) {
		List<MexObject> items = new ArrayList<>();
		for(int i = 1; i <= count; i++) {
			String item = base.replaceAll("/0\\.", "/" + i + ".");
			items.add(new MexObject(item));
		}
		
		return items;
	}
}