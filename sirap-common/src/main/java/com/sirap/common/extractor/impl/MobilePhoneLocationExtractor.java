package com.sirap.common.extractor.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class MobilePhoneLocationExtractor extends Extractor<MexedObject> {
	
	public static final String URL_TEMPLATE = "http://www.00cha.com/shouji/?mobile={0}";

	public MobilePhoneLocationExtractor(String repo) {
		printFetching = true;
		String url = StrUtil.occupy(URL_TEMPLATE, repo);
		setUrl(url);
		useGBK();
	}
	
	@Override
	protected void parseContent() {
		String regex = "<font color=\"?red\"?>(.*?)</font>";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String temp = m.group(1);
			temp = HtmlUtil.removeHttpTag(temp);
			temp = temp.replaceAll("\\s", "");
			sb.append(" " + temp);
		}
		
		mexItem = new MexedObject(sb.toString());
	}
}
