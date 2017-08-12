package com.sirap.basic.util;

import java.util.Random;

public class RandomUtil {

	public static String digits(int countOfChars) {
		String result = chars(countOfChars, StrUtil.DIGITS);
		
		return result;
	}

	public static Integer digitsStartWithNoZero(int countOfChars) {
		String start = chars(1, "123456798");
		String others = chars(countOfChars - 1, StrUtil.DIGITS);
		
		return Integer.parseInt(start + others);
	}

	public static String letters(int countOfChars) {
		String result = chars(countOfChars, StrUtil.LETTERS);
		
		return result;
	}

	public static String letters(int countOfChars, boolean uppercase) {
		String result = chars(countOfChars, uppercase ? StrUtil.LETTERS_UPPERCASED : StrUtil.LETTERS);
		
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
}
