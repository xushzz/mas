package com.sirap.extractor.impl;

import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.HtmlUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class ChinaCalendarExtractor extends Extractor<ValuesItem> {
	
	public static final String URL_TEMPLATE = "https://gonglinongli.51240.com";

	public ChinaCalendarExtractor(String params) {
		printFetching = true;
		usePost().setUrl(URL_TEMPLATE + "?" + construct(params));
	}
	
	/***
	 * g20190103
	 * g201803
	 * g2012
	 * n20190302 三月初二
	 * n20190302R 闰三月初二
	 * n201903 三月
	 * n201903 闰三月
	 * n201901 正月
	 * n201911 冬月
	 * n202912 腊月
	 * (g|n)\\d{4,8}\\R?)
	 */
	private String construct(String params) {
		String regex = "(g|n)(\\d{4})(\\d{2})(\\d{2})(R?)";
		Matcher m = createMatcher(regex, params);
		if(m.matches()) {
			String flag = m.group(1);
			String year = m.group(2);
			XXXUtil.checkRange(Integer.parseInt(year), 1899, 2100);
			String month = m.group(3);
			if(!m.group(5).isEmpty()) {
				month = "-" + month;
			}
			String day = m.group(4);
			
			String templ = "{0}ongli_nian={1}&{0}ongli_yue={2}&{0}ongli_ri={3}";
			String requestParam = StrUtil.occupy(templ, flag, year, month, day);
			
			return requestParam;
		}
		
		throw new MexException("Illegal param : " + params);
	}

	@Override
	protected void parse() {
		String regex = "<td[^<>]+bgcolor=\"#FFFFFF\"[^<>]+?>(.+?)</td>";
		List<String> items = StrUtil.findAllMatchedItems(regex, source);
		if(EmptyUtil.isNullOrEmpty(items)) {
			XXXUtil.alert("Not found result with param: " + StrUtil.getUrlParams(getUrl()));
		}
		String day = HtmlUtil.removeHttpTag(items.get(0)).replaceAll("\\s+", "");
		String nongli = HtmlUtil.removeHttpTag(items.get(1));
		String animal = HtmlUtil.removeHttpTag(items.get(2));
		String constellation = HtmlUtil.removeHttpTag(items.get(3));
		ValuesItem vi = new ValuesItem();
		List<String> ymd = StrUtil.findAllMatchedItems("\\d+", day);
		vi.add(StrUtil.connect(ymd));
		vi.add(day);
		vi.add(nongli);
		vi.add(animal);
		vi.add(constellation);
		
		item = vi;
	}
}
