package com.sirap.extractor.impl;

import java.util.Collections;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.extractor.domain.MMKChannelItem;

public class MMKChannelsExtractor extends Extractor<MMKChannelItem> {

	public static final String HOME = "http://www.manmankan.com/dy2013/jiemubiao";
	
	@Override
	public String getUrl() {
		useGBK();
		printFetching = true;
		
		return HOME;
	}
	
	@Override
	protected void parse() {
		String regex = "<li><a href=\"[^\"]+/(\\d+)/\"[^<>]+>([^<>]+)</a></li>";
		Matcher ma = createMatcher(regex);
		while(ma.find()) {
			int id = Integer.parseInt(ma.group(1));
			String name = ma.group(2);
			mexItems.add(new MMKChannelItem(id, name));
		}
		
		Collections.sort(mexItems);
	}
}