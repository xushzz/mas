package com.sirap.extractor.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

public class China24JieqiExtractor extends Extractor<MexObject> {
	
	public static final String HOMEPAGE = "http://114.xixik.com";
	public static final String URL_TEMPLATE = HOMEPAGE + "/24jieqi";
	private String year;
	private boolean markWhere = true;
	private Map<String, MexObject> points = new HashMap<>();
	
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
	
	
	public void setMarkWhere(boolean markWhere) {
		this.markWhere = markWhere;
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
			String point = getPrettyText(m.group(1));
			String english = getPrettyText(m.group(2));
			String dates = getPrettyText(m.group(3));
			String info = " " + point + " " + setLen(dates) + " " + english;
			MexObject mo = new MexObject(info);
			if(markWhere) {
				String start = start(dates);
				points.put(start, mo);
			}
			mexItems.add(mo);
		}
		
		if(markWhere) {
			markCurrentDay();
		}
	}
	
	private void markCurrentDay() {
		List<String> keys = new ArrayList<>(points.keySet());
		
		String now = DateUtil.displayNow("MMdd");
		int whenIndex = -10;

		Collections.sort(keys);
		for(int i = 0; i < keys.size(); i++) {
			boolean flag = now.compareTo(keys.get(i)) <= 0;
			if(flag) {
				whenIndex = i;
				break;
			}
		}
		
		if(whenIndex <= 0 ) {
			whenIndex = keys.size() - 1;
		} else {
			whenIndex--;
		}
	
		
		String key = keys.get(whenIndex);
		MexObject mo = points.get(key);
		mo.setObj(mo.getObj().toString().replaceAll("^\\s", "*"));
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
				parse24Points(contentByYear);

				return;
			}
		}
	}
	
	private void parse24Points(String contentByYear) {
		String regex = "<td bgcolor=\"#EFEFEF\">(.+?)</td><td>(.+?)</td>";
		Matcher m = createMatcher(regex, contentByYear);
		while(m.find()) {
			String point = getPrettyText(m.group(1));
			String datetime = getPrettyText(m.group(2));
			MexObject mo = new MexObject(" " + point + " " + datetime);
			if(markWhere) {
				String start = start(datetime);
				points.put(start, mo);
			}
			mexItems.add(mo);
		}
		
		if(markWhere) {
			markCurrentDay();
		}
	}
	
	public static String setLen(String dates) {
		String regex = "\\d{1,2}";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(dates);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String numbers = m.group();
			String fixed = StrUtil.padLeft(numbers, 2, "0");
			m.appendReplacement(sb, fixed);
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	public static String start(String dates) {
		List<String> duo = StrUtil.findAllMatchedItems("\\d{1,2}", dates);
		String start = StrUtil.padLeft(duo.get(0), 2, "0") +StrUtil.padLeft(duo.get(1), 2, "0");
		
		return start;
	}
}
