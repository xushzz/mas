package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.extractor.Extractor;

public class China24JieqiExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://114.xixik.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/24jieqi";
	private String year;
	
	public China24JieqiExtractor() {
		printFetching = true;
		useGBK();
		setUrl(URL_TEMPLATE);
	}
	
	public China24JieqiExtractor(String year) {
		this.year = year;
		printFetching = true;
		useGBK();
		setUrl(URL_TEMPLATE);
	}
	
	@Override
	protected void parseContent() {
		if(year == null) {
			parseGeneral();
		} else {
			parseExactPointsByYear(year);
		}
	}
	
	protected void parseGeneral() {
		String regex = "<td><a href=\"[^<>]+\">\\s*<strong>([^<>]+)</strong></a>";
		regex += "(.+?)<br\\s*/>([^<>]+)</td>";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String point = removeHttpStuff(m.group(1));
			String english = removeHttpStuff(m.group(2));
			String dates = removeHttpStuff(m.group(3));
			String info = point + " " + setLen(dates) + " " + english;
			mexItems.add(new MexObject(info));
		}
	}
	
	private void parseExactPointsByYear(String year) {
		String regex = "<table border=\"1\" bordercolor=\"#cccccc\"";
		regex += ".+?<strong>(\\d{4})\\D+</strong>";
		regex += ".+?</table>";
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String whatYear = m.group(1);
			if(StrUtil.equals(year, whatYear)) {
				String contentByYear = m.group();
				mexItems = parse24Points(contentByYear);

				return;
			}
		}
	}
	
	private List<MexObject> parse24Points(String contentByYear) {
		String regex = "<td bgcolor=\"#EFEFEF\">(.+?)</td><td>(.+?)</td>";
		Matcher m = createMatcher(regex, contentByYear);
		List<MexObject> items = new ArrayList<>();
		while(m.find()) {
			String point = removeHttpStuff(m.group(1));
			String datetime = removeHttpStuff(m.group(2));
			String item = point + " " + datetime;
			items.add(new MexObject(item));
		}
		
		return items;
	}
	
	public static String setLen(String dates) {
		String regex = "\\d{1,2}";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(dates);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String numbers = m.group();
			String fixed = StrUtil.extendLeftward(numbers, 2, "0");
			m.appendReplacement(sb, fixed);
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
}
