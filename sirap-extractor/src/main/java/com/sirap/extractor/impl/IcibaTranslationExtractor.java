package com.sirap.extractor.impl;

import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

public class IcibaTranslationExtractor extends Extractor<ValuesItem> {
	
	public static final String HOMEPAGE = "http://www.iciba.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/{0}";

	public IcibaTranslationExtractor(String param) {
		printFetching = true;
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(param));
		setUrl(url);
	}

	@Override
	protected void parseContent() {
		ValuesItem vi = new ValuesItem();
		
		String regexHead = "<h1 class=\"keyword\">([^<>]+)</h1>";
		Matcher ma = createMatcher(regexHead);
		if(ma.find()) {
			vi.add(getPrettyText(ma.group(1)));
		}
				
		String regex = "<li class=\"clearfix\">(.*?)</li>";
		List<String> items = StrUtil.findAllMatchedItems(regex, source);
		if(!EmptyUtil.isNullOrEmpty(items)) {
			int order = 0;
			for(String item : items) {
				order++;
				vi.add(order + ") " + getPrettyText(item));
			}
			
			item = vi;
		}
		
	}
}
