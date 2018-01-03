package com.sirap.extractor.impl;

import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Extractor;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.extractor.konstants.NongliData;

public class ChinaCalendarTextExtractor extends Extractor<MexItem> {
	
	public static final String LIBRARY_THEWIRE = "https://gitee.com/thewire/stamina/raw/master/days";

	public static final String FLAG_NONGLI = "N";
	public static final String FLAG_GONGLI = "G";
	public static final String FLAG_LEAP = "R";
	public static final String FLAG_REGULAR = "X";
	
	private boolean isGongli;
	private String year;
	private String month;
	private String day;
	private boolean isLeap;

	public ChinaCalendarTextExtractor(String library, String urlInfo, boolean isGongli, String year, String month, String day, boolean isLeapMonth) {
		String urlBase = library != null ? library : LIBRARY_THEWIRE;
		XXXUtil.checkRange(Integer.parseInt(urlInfo), 1899, 2101);
		setUrl(StrUtil.useSeparator(library, urlInfo + ".txt"));
		setUrl(StrUtil.occupy("{0}/{1}.txt", urlBase, urlInfo));

		this.isGongli = isGongli;
		this.year = year;
		this.month = month;
		this.day = day;
		this.isLeap = isLeapMonth;

		useGBK().useList();
		printFetching = true;
	}
	
	private String createMexCriteria() {
		String cretin = "";
		if(isGongli) {
			cretin = "rx:^\\s*" + year;
			if(!EmptyUtil.isNullOrEmpty(month)) {
				cretin += month;
			}
			if(!EmptyUtil.isNullOrEmpty(day)) {
				cretin += day;
			}
		} else {
			if(!EmptyUtil.isNullOrEmpty(year)) {
				cretin += "y" + year + "&";
			}
			if(!EmptyUtil.isNullOrEmpty(month)) {
				XXXUtil.checkMonthRange(Integer.parseInt(month));
				String prefix = isLeap ? FLAG_LEAP : FLAG_REGULAR;
				cretin += "m" + prefix + month + "&";
			}
			if(!EmptyUtil.isNullOrEmpty(day)) {
				XXXUtil.checkRange(Integer.parseInt(day), 1, 30);
				cretin += "d" + day + "&";
			}
			if(isLeap) {
				cretin += FLAG_LEAP + "&";
			}
		}

		return cretin;
	}
	
	@Override
	protected void parse() {
		List<MexItem> tempItems = Lists.newArrayList();
		for(String line : sourceList) {
			if(EmptyUtil.isNullOrEmpty(line)) {
				continue;
			}
			if(isGongli) {
				tempItems.add(new MexObject(line));
			} else {
				NongliItem item = new NongliItem();
				if(item.parse(line)) {
					tempItems.add(item);
				}
			}
		}
		
		mexItems = CollUtil.filter(tempItems, createMexCriteria());
	}

	@SuppressWarnings("serial")
	class NongliItem extends MexItem {
		private String origin;
		private String year;
		private String month;
		private String day;
		private boolean isLeap;
		
		@Override
		public boolean parse(String record) {
			String regex = "(\\d{4})[^,\\s\\d]\\s([^,\\s\\d]{2,3})\\s([^,\\s\\d]{2})\\s";
			Matcher ma = StrUtil.createMatcher(regex, record);
			if(ma.find()) {
				origin = record;
				year = ma.group(1).trim();
				String monthTemp = ma.group(2).trim();
				int monthIndex = 0;
				isLeap = monthTemp.startsWith("闰");
				if(isLeap) {
					monthIndex = 1;
				}
				
				String prefix = isLeap ? FLAG_LEAP : FLAG_REGULAR; 
				String monthChinese = monthTemp.charAt(monthIndex) + "";
				month = prefix + NongliData.MONTHS.get(monthChinese);
				String dayChinese = ma.group(3).trim();
				day = NongliData.DAYS.get(dayChinese);
				if(day == null) {
					String charHigh = dayChinese.charAt(0) + "";
					String numberHigh = NongliData.DAYS.get(charHigh);
					String charLow = dayChinese.charAt(1) + "";
					String numberLow = NongliData.MONTHS.get(charLow);
					day = numberLow.replace("0", numberHigh);
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public boolean isMatched(String keyWord) {
			if(!EmptyUtil.isNullOrEmpty(year) && StrUtil.equals(keyWord, "y" + year)) {
				return true;
			}
			if(!EmptyUtil.isNullOrEmpty(month) && StrUtil.equals(keyWord, "m" + month)) {
				return true;
			}
			if(!EmptyUtil.isNullOrEmpty(day) && StrUtil.equals(keyWord, "d" + day)) {
				return true;
			}
			if(isLeap && StrUtil.equals(keyWord, FLAG_LEAP)) {
				return true;
			}
			
			return false;
		}
		
		@Override
		public String toPrint() {
			return origin;
		}
		
		@Override
		public String toString() {
			return origin + ", " + year + ", " + month + ", " + day + ", " + isLeap;
		}
	}

}