package com.sirap.extractor.impl;

import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class IcibaTranslationExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://www.iciba.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/{0}";

	public IcibaTranslationExtractor(String param) {
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
			mexItems.add(new MexObject(order + ") " + temp));
		}
	}
}
