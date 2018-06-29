package com.sirap.basic.util;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.sirap.basic.data.NameData;

public class RandomUtil {

	public static String digits(int countOfChars) {
		String result = chars(countOfChars, StrUtil.DIGITS);
		
		return result;
	}

	public static Long digitsStartWithNoZero(int countOfChars) {
		String start = chars(1, "123456798");
		String others = chars(countOfChars - 1, StrUtil.DIGITS);
		
		return Long.parseLong(start + others);
	}

	public static String letters(int countOfChars) {
		String result = chars(countOfChars, StrUtil.LETTERS);
		
		return result;
	}

	public static String LETTERS(int countOfChars) {
		String result = chars(countOfChars, StrUtil.LETTERS_UPPERCASED);
		
		return result;
	}
	public static String alphanumeric(int countOfChars) {
		String result = chars(countOfChars, StrUtil.ALPHANUMERIC);
		
		return result;
	}
	
	public static String chars(int countOfChars, String source) {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		String temp = source;
		
		while(sb.length() < countOfChars) {
			int index = r.nextInt(temp.length());
			sb.append(temp.charAt(index));
		}
		
		return sb.toString();
	}

	public static int number(int minValue, int maxValue) {
		Random xian = new Random();
		int range = Math.abs(minValue - maxValue) + 1;
		int result = xian.nextInt(range) + Math.min(minValue, maxValue);
		
		return result;
	}
	
	public static int number(int maxValue) {
		Random xian = new Random();
		int result = xian.nextInt(maxValue);
		
		return result;
	}
	
	public static String name() {
		return names(1).get(0);
	}
	
	public static List<String> names(int size) {
		Random xian = new Random();
		List<String> sample = Lists.newArrayList(NameData.EGGS.values());
		int samplesize = sample.size();
		List<String> items = Lists.newArrayList();
		if(samplesize == 0) {
			return items;
		}
		while(items.size() < size) {
			int index = xian.nextInt(samplesize);
			items.add(sample.get(index));
		}
		
		return items;
	}
	
	public static List<String> names(int size, char criteria) {
		String cstr = criteria + "";
		if(!StrUtil.isRegexMatched("[a-z]", cstr)) {
			XXXUtil.alert("Letter from a to z only, not [{0}]", cstr);
		}
		
		Random xian = new Random();
		List<String> sample = Lists.newArrayList(NameData.EGGS.values());
		if(!StrUtil.isRegexFound(criteria + "", StrUtil.connect(sample))) {
			XXXUtil.alert("Sample data contains no letter [{0}] at all", cstr);
		}
		
		int samplesize = sample.size();
		List<String> items = Lists.newArrayList();
		if(samplesize == 0) {
			return items;
		}
		int maxAttempts = Short.MAX_VALUE;
		int count = 0;
		while(items.size() < size) {
			int index = xian.nextInt(samplesize);
			String temp = sample.get(index);
			if(StrUtil.isRegexFound(cstr, temp)) {
				items.add(sample.get(index));
			}
			count++;
			if(count > maxAttempts) {
				XXXUtil.info("Reach max attemps of {0} for [{1}], quit.", maxAttempts, cstr);
				break;
			}
		}
		
		return items;
	}
}
