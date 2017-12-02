package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;

public class MMKProgramExtractor extends Extractor<MexObject> {
	
	public static final String TEMP = "http://www.manmankan.com/dy2013/jiemubiao/{0}/zhou{1}.shtml";
	
	public MMKProgramExtractor(int channelId, String dayOfWeek) {
		printFetching = true;
		useGBK();
		String url = StrUtil.occupy(TEMP, channelId, dayOfWeek);
		setUrl(url);
	}
	
	@Override
	protected void parseContent() {
		String title = StrUtil.findFirstMatchedItem("<title>([^<>]+)</title>", source);
		mexItems.add(new MexObject(title));
		String regex = "<li><em>([^<>]+)</em><span>([^<>]+)</span></li>";
		Matcher ma = createMatcher(regex);
		while(ma.find()) {
			String time = ma.group(1);
			String what = ma.group(2);
			
			MexObject mo = new MexObject(time + " " + what);
			mexItems.add(mo);
		}
	}
}
