package com.sirap.common.extractor.impl;

import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class EnglishTranslationExtractor extends Extractor<MexedObject> {
	
	public static final String URL_TEMPLATE = "http://www.iciba.com/{0}";

	public EnglishTranslationExtractor(String param) {
		printFetching = true;
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(param));
		setUrl(url);
	}

	@Override
	protected void parseContent() {
		String regex = "<li class=\"clearfix\">(.*?)</li>";
		List<String> items = StrUtil.findAllMatchedItems(regex, source);
		int order = 0;
		for(String item : items) {
			String temp = HtmlUtil.removeHttpTag(item).trim();
			temp = StrUtil.reduceMultipleSpacesToOne(temp);

			order++;
			mexItems.add(new MexedObject(order + ") " + temp));
		}
	}
}
