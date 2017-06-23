package com.sirap.extractor.impl;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;
import com.sirap.extractor.domain.ZhihuRecord;

public class ZhihuExtractor extends Extractor<ZhihuRecord> {
	
	public static final String URL_TEMPLATE = "https://www.zhihu.com/search?type=content&q={0}";
	
	public ZhihuExtractor(String param) {
		printFetching = true;
		useGBK();
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(param));
		setUrl(url);
	}
		
	@Override
	protected void parseContent() {
		String regex = "<li class=\"item clearfix.+?</div></div></div></div></li>";
		Matcher m = createMatcher(regex);
		String regexQNumber = "href=\"/question/(\\d+)\"";
		String regexQuestion = "<div class=\"title\">(.+?)</div>";
		String regexAnswer = "<script type=\"text\" class=\"content\">(.+?)</script>";
		int count = 0;
		while(m.find()) {
			String raw = m.group();
			String qNumber = StrUtil.findFirstMatchedItem(regexQNumber, raw);
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
			item.setQuestionNumber(qNumber);
			item.setQuestion(question);
			item.setAnswer(answer);

			mexItems.add(item);
		}
	}
}
