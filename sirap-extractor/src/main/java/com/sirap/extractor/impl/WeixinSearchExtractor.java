package com.sirap.extractor.impl;

import java.util.Date;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;

public class WeixinSearchExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://weixin.sogou.com/weixin?type=2&query={0}";
	
	public WeixinSearchExtractor(String keyword) {
		printFetching = true;
		setUrl(StrUtil.occupy(HOMEPAGE, encodeURLParam(keyword)));
	}

	@Override
	protected void parseContent() {
		String regex = "<h3>(.+?)<div class=\"moe-box\">";
		regex = "<a target=\"_blank\" href=\"([^\"]+)\"[^<>]+>(.+?)</a>";
		regex += "\\s*</h3>(.+?)<div class=\"s-p\" t=\"[0-9]+\">";
		regex += "\\s*<a[^<>]+>(.+?)</a>";
		regex += "<span class=\"s2\"><script>[^<>]+'([0-9]+)'[^<>]+</script>";
		Matcher ma = createMatcher(regex);
		int count = 1;
		String prefix = StrUtil.repeat(' ', 3);
		while(ma.find()) {
			String title = getPrettyText(ma.group(2)).trim();
			String url = ma.group(1).replace("&amp;", "&").trim();
			String summary = getPrettyText(ma.group(3)).trim(); 
			String who = getPrettyText(ma.group(4)).trim(); 
			String when = String.format("%tF", new Date(Long.parseLong(ma.group(5) + "000")));
			mexItems.add(new MexObject("#" + (count++) + " " + title + " " +  who + " " + when));
			mexItems.add(new MexObject(prefix + summary));
			mexItems.add(new MexObject(prefix + url));
			mexItems.add(new MexObject(""));
//			mexItems.add(new MexObject(prefix + who + " " + when));
		}
	}
}
