package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.ZhihuRecord;

public class ZhihuSearchExtractor extends Extractor<ZhihuRecord> {
	
	public static final String HOMEPAGE = "https://www.zhihu.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/search?type=content&q={0}";
	
	public ZhihuSearchExtractor(String param) {
		printFetching = true;
		useGBK();
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(param));
		setUrl(url);
	}
		
	@Override
	protected void parseContent() {
		String regex = "<li class=\"item clearfix.+?</div></div></li>";
		Matcher m = createMatcher(regex);
		String regexLink = "<a target=\"_blank\" href=\"([^\"]+)\" class=\"js-title-link\">";
		String regexQuestion = "<div class=\"title\">(.+?)</div>";
		String regexAnswer = "<script type=\"text\" class=\"content\">(.+?)</script>";
		int count = 0;
		while(m.find()) {
			String raw = m.group();
			String link = StrUtil.findFirstMatchedItem(regexLink, raw);
			if(!StrUtil.startsWith(link, "https")) {
				link = HOMEPAGE + link;
			}
			String question = removeHttpStuff(StrUtil.findFirstMatchedItem(regexQuestion, raw));

			String answer = "SHIT";
			String temp = StrUtil.findFirstMatchedItem(regexAnswer, raw);
			if(temp != null) {
				temp = temp.replaceAll("<br>", "\n");
				answer = removeHttpStuff(temp);
			}
			
			count++;
			ZhihuRecord item = new ZhihuRecord();
			item.setPseudoOrder(count);
			item.setLink(link);
			item.setQuestion(question);
			item.setAnswer(answer);

			mexItems.add(item);
		}
	}
}
