package com.sirap.extractor.impl;

import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.extractor.Extractor;

public class WikiSummaryExtractor extends Extractor<MexObject> {
	
	public static final String URL_TEMPLATE = "https://en.wikipedia.org/wiki/{0}";
	
	public WikiSummaryExtractor(String param) {
		printFetching = true;
		String temp = param.replace(' ', '_');
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(temp));
		setUrl(url);
	}

	@Override
	protected void parseContent() {
		String regexSolid = "</table>\\s*(<p>.+?)<h2>Contents</h2>";
		String solid = StrUtil.findFirstMatchedItem(regexSolid, source);
		XXXUtil.nullCheck(solid, ":something must be wrong with " + getUrl());
		
		String regex = "<p>(.+?)</p>";
		List<String> items = StrUtil.findAllMatchedItems(regex, solid, 1);
		XXXUtil.nullCheck(solid, ":found nothing about " + regex);

		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			String temp = getPrettyText(item);

			mexItems.add(new MexObject(temp));
			if(i != items.size() - 1) {
				mexItems.add(new MexObject(""));
			}
		}
	}
}
