package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class ChinaJieriExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://tools.2345.com/jieri.htm";
	
	public ChinaJieriExtractor() {
		printFetching = true;
		setUrl(HOMEPAGE);
	}

	@Override
	protected void parse() {
		String regex = "<li><a class=.+?target=\"_blank\">([^<>]+)</a>\\[([^<>]+)\\]</li>";
		Matcher ma = createMatcher(regex);
		String now = DateUtil.displayNow("MM/dd");
		while(ma.find()) {
			String day = ma.group(2);
			String prefix = StrUtil.equals(now, day) ? "*" : " ";
			MexObject mo = new MexObject(prefix + ma.group(2) + " " + ma.group(1));
			mexItems.add(mo);
		}
	}
}
