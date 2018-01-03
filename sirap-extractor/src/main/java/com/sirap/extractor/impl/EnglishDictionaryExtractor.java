package com.sirap.extractor.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;

public class EnglishDictionaryExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://www.dictionary.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/browse/{0}";

	public EnglishDictionaryExtractor(String param) {
		printFetching = true;
		String url = StrUtil.occupy(URL_TEMPLATE, encodeURLParam(param));
		setUrl(url);
	}
	
	@Override
	protected void parse() {
		String regexMain = "<div class=\"source-data\">(.+?)Dictionary.com Unabridged";
		String main = StrUtil.findFirstMatchedItem(regexMain, source);
		
		if(EmptyUtil.isNullOrEmpty(main)) {
			return;
		}
		
		String regexSection = "<section class=\"def-pbk ce-spot\"(.*?)</section>";
		Matcher m = Pattern.compile(regexSection, Pattern.CASE_INSENSITIVE).matcher(main);
		while(m.find()) {
			String temp = m.group(0);
			parseByWordType(temp);
		}
	}
	
	private void parseByWordType(String section) {
		String regexHeader = "<header class=\"luna-data-header\">(.*?) </header>";
		String header = StrUtil.findFirstMatchedItem(regexHeader, section);
		String wordType = HtmlUtil.removeHttpTag(header);
		wordType = StrUtil.reduceMultipleSpacesToOne(wordType).trim();
		mexItems.add(new MexObject(wordType));
		
		String regexStuff = "<span class=\"def-number\">(.*?)</span>(.*?)</div>";
		Matcher m = Pattern.compile(regexStuff, Pattern.CASE_INSENSITIVE).matcher(section);
		while(m.find()) {
			String order = m.group(1).trim();
			String more = HtmlUtil.removeHttpTag(m.group(2));
			more = StrUtil.reduceMultipleSpacesToOne(more).trim();
			mexItems.add(new MexObject(order + " " + more));
		}
	}
}
