package com.sirap.basic.util;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;

public class MiscUtil {
	
	public static String SHORT_PROVINCES = "京津沪渝宁藏桂新港澳黑滇云吉皖鲁晋粤桂苏赣冀豫浙琼鄂湘甘陇闽川蜀黔贵辽陕秦青台";
	public static String TIANGAN_DIZHI = "甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥";
	public static String CARPLATE_MILITARY_2004 = "京军空海北沈兰济南广成";
	public static String CARPLATE_MILITARY_2012 = "VZKHEBSLJNGC";
	public static String CARPLATE_OTHERS = "村";
	
	public static boolean isEmail(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return false;
		}
		
		String regex ="^([a-zA-Z0-9]*[-_\\.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
		return StrUtil.isRegexMatched(regex, source);
	}
	
	public static List<String> illegalPlates(String filepath) {
		List<String> items = IOUtil.readLines(filepath, Konstants.CODE_UTF8);
		List<String> goods = Lists.newArrayList();
		for(String item : items) {
			String temp = item.replaceAll("[_\\-\\s]", "");
			boolean flag = MiscUtil.isLegalCarplate(temp);
			if(!flag) {
				goods.add(temp);
			}
		}
		
		return goods;
	}
	/***
	 * 黄色牌号代表大车
	 * 兰牌代表小车
	 * 黑牌代表外资企业或者是大使馆
	 * 白底红字：军车
	 * @param source
	 * @return
	 */
	public static boolean isLegalCarplate(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return false;
		}
		
		//local
		String regex = "[" + SHORT_PROVINCES + "][a-z\\d]{5,7}([学警领挂]|)";
		if(StrUtil.isRegexMatched(regex, source)) {
			return true;
		}
		
		//ambassador
		regex = "使\\d{6}";
		if(StrUtil.isRegexMatched(regex, source)) {
			return true;
		}
		
		//military-WJ
		regex = "WJ([" + SHORT_PROVINCES + "]|)[a-z\\d]{5,7}";
		if(StrUtil.isRegexMatched(regex, source)) {
			return true;
		}

		//military-2004
		regex = "[" + CARPLATE_MILITARY_2004 + "][a-z\\d]{4,6}";
		if(StrUtil.isRegexMatched(regex, source)) {
			return true;
		}

		//military-2012
		regex = "[" + CARPLATE_MILITARY_2012 + "][a-z\\d]{4,6}";
		if(StrUtil.isRegexMatched(regex, source)) {
			return true;
		}

		//others
		regex = "[" + CARPLATE_OTHERS + "][a-z\\d]{4,6}";
		if(StrUtil.isRegexMatched(regex, source)) {
			return true;
		}
		
		return false;
	}

	public static String seasonAndEpisode(String source) {
		List<String> items = Lists.newArrayList();
		items.add("S\\d{2}E\\d{2}");
		
		for(String regex : items) {
			Matcher ma = StrUtil.createMatcher(regex, source);
			if(ma.find()) {
				return ma.group(0);
			}
		}
		
		return null;
	}
	
	/***
	 * 
	 * @param origin K12
	 * @return
	 */
	public static int gapsBefore(String origin) {
		String point = StrUtil.findFirstMatchedItem("([a-z]+)(\\d+|)", origin).toUpperCase();
		XXXUtil.nullCheck(point, ":Not a legal column name: " + origin);
		int total = 0;
		int len = point.length();
		for(int start = 0; start < point.length(); start++) {
			char ch = point.charAt(start);
			int chInt = (int)(ch - 'A');
			total += (int)(Math.pow(26, len - start - 1)) * (chInt + 1);
		}
		
		return total - 1;
	}
}
