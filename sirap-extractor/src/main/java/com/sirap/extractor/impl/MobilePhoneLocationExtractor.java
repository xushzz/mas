package com.sirap.extractor.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class MobilePhoneLocationExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://www.00cha.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/shouji/?mobile={0}";

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
		
		item = new MexObject(sb.toString());
	}
}
