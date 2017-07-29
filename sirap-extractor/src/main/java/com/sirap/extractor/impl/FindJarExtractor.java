package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class FindJarExtractor extends Extractor<MexObject> {
	//+limit:none
	public static final String HOME = "http://www.findjar.com";
	public static final String URL_TEMPLATE = HOME + "/index.x?query={0}+limit:none";
	
	public FindJarExtractor(String param) {
		printFetching = true;
		String temp = param.replace(' ', '_');
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(temp));
		setUrl(url);
	}

	@Override
	protected void parseContent() {
		String regex = "<span class=\"type\">\\[(\\w+)\\]</span>\\s+";
		regex += "<a href=\"([^\"]+)\">(.+?)</a>";
		Matcher ma = createMatcher(regex, source);
		while(ma.find()) {
			String type = ma.group(1);
			String href = ma.group(2);
			href = href.replace("/index.x/..", HOME);
			href = href.replaceAll(";jsessionid=\\w{32}", "");
			String value = HtmlUtil.removeHttpTag(ma.group(3));
			String item = type + " " + value + " " + href;
			MexObject mo = new MexObject(item);
//			D.pl(type, href, value);
			if(!mexItems.contains(mo)) {
				mexItems.add(mo);
			}
		}
	}
}
