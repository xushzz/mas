package com.sirap.common.extractor.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class ChinaCalendarExtractor extends Extractor<MexedObject> {
	
	private String calendarInfo;
	
	public static final String URL_TEMPLATE = "https://gonglinongli.51240.com";

	public ChinaCalendarExtractor(String params) {
		printFetching = true;
		setUrl(URL_TEMPLATE);
		setRequestParams(construct(params));
		setMethodPost(true);
	}
	
	private String construct(String params) {
		String regex = "(g|n)(\\d{4})(\\d{2})(\\d{2})";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(params);
		if(m.matches()) {
			String flag = m.group(1);
			String year = m.group(2);
			String month = m.group(3);
			String day = m.group(4);
			
			String templ = "{0}ongli_nian={1}&{0}ongli_yue={2}&{0}ongli_ri={3}";
			String requestParam = StrUtil.occupy(templ, flag, year, month, day);
			
			return requestParam;
		}
		
		throw new MexException("Illegal param : " + params);
	}

	@Override
	protected void parseContent() {
		String regex = "<td[^<>]+bgcolor=\"#FFFFFF\"[^<>]+?>(.+?)</td>";
		List<String> items = StrUtil.findAllMatchedItems(regex, source);
		StringBuffer su = new StringBuffer();
		for(String item : items) {
			String temp = HtmlUtil.removeHttpTag(item).trim();
			temp = StrUtil.reduceMultipleSpacesToOne(temp);
			su.append(temp).append(", ");
		}

		calendarInfo = su.toString().replaceAll(",[^,]+$", "");
	}
	
	public String getCalendarInfo() {
		return calendarInfo;
	}
}
