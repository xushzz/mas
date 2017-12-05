package com.sirap.extractor.konstants;

import java.util.HashMap;

import com.google.common.collect.Maps;

public class NongliData {
	public static final HashMap<String, String> DAYS = Maps.newHashMap();
	static {
		DAYS.put("初十", "10");
		DAYS.put("二十", "20");
		DAYS.put("三十", "30");
		DAYS.put("初", "0");
		DAYS.put("十", "1");
		DAYS.put("廿", "2");
	}

	public static final HashMap<String, String> MONTHS = Maps.newHashMap();
	static {
		MONTHS.put("正", "01");
		MONTHS.put("一", "01");
		MONTHS.put("二", "02");
		MONTHS.put("三", "03");
		MONTHS.put("四", "04");
		MONTHS.put("五", "05");
		MONTHS.put("六", "06");
		MONTHS.put("七", "07");
		MONTHS.put("八", "08");
		MONTHS.put("九", "09");
		MONTHS.put("十", "10");
		MONTHS.put("冬", "11");
		MONTHS.put("腊", "12");
	}
}
