package com.sirap.basic.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.math.PermutationGenerator;
import com.sirap.basic.tool.C;

public class MathUtil {
	
	public static final AlinkMap<String, Double> TIME_UNITS = Amaps.newLinkHashMap();
	static {
		TIME_UNITS.put("year", 1.0);
		TIME_UNITS.put("month", 12.0);
		TIME_UNITS.put("week", 52.0);
		TIME_UNITS.put("day", 365.0);
		TIME_UNITS.put("hour", 365 * 24.0);
		TIME_UNITS.put("min", 365 * 24 * 60.0);
		TIME_UNITS.put("minute", 365 * 24 * 60.0);
		TIME_UNITS.put("secs", 365 * 24 * 60 * 60.0);
		TIME_UNITS.put("seconds", 365 * 24 * 60 * 60.0);
		TIME_UNITS.put("sec", 365 * 24 * 60 * 60.0);
		TIME_UNITS.put("second", 365 * 24 * 60 * 60.0);
		TIME_UNITS.put("msec", 365 * 24 * 60 * 60 * 1000.0);
		TIME_UNITS.put("milli", 365 * 24 * 60 * 60 * 1000.0);
	}
	
	public static final AlinkMap<String, Double> WEIGHT_UNITS = Amaps.newLinkHashMap();
	static {
		WEIGHT_UNITS.put("kg", 1.0);
		WEIGHT_UNITS.put("lb", 2.2046226);
		WEIGHT_UNITS.put("oz", 35.2739619);
	}
	
	public static final AlinkMap<String, Double> SHORT_DISTANCE_UNITS = Amaps.newLinkHashMap();
	static {
		SHORT_DISTANCE_UNITS.put("mi", 0.9144);
		SHORT_DISTANCE_UNITS.put("yard", 1.0);
		SHORT_DISTANCE_UNITS.put("chi", 2.7432);
		SHORT_DISTANCE_UNITS.put("feet", 3.0);
		SHORT_DISTANCE_UNITS.put("cun", 27.432);
		SHORT_DISTANCE_UNITS.put("inch", 36.0);
		SHORT_DISTANCE_UNITS.put("cm", 91.44);
	}
	
	public static final AlinkMap<String, Double> LONG_DISTANCE_UNITS = Amaps.newLinkHashMap();
	static {
		LONG_DISTANCE_UNITS.put("nmi", 0.8689762);
		LONG_DISTANCE_UNITS.put("mile", 1.0);
		LONG_DISTANCE_UNITS.put("km", 1.609344);
		LONG_DISTANCE_UNITS.put("meter", 1609.344);
	}
	
	public static String setDoubleScale(double a, int scale) {
		BigDecimal bd = toBigDecimal(a);
		BigDecimal newBd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
		
		return newBd.toString();
	}
	
	public static BigDecimal add(double a, double b) {
		BigDecimal bd1 = toBigDecimal(a);
		BigDecimal bd2 = toBigDecimal(b);
		
		return bd1.add(bd2);
	}
	
	public static BigDecimal add(List<BigDecimal> numbers) {
		BigDecimal grand = new BigDecimal(0);
		for(BigDecimal number:numbers) {
			grand = grand.add(number);
		}
		
		return grand;
	}
	
	public static BigDecimal subtract(double a, double b) {
		BigDecimal bd1 = toBigDecimal(a);
		BigDecimal bd2 = toBigDecimal(b);
		
		return bd1.subtract(bd2);
	}
	
	public static BigDecimal multiply(double a, double b) {
		BigDecimal bd1 = toBigDecimal(a);
		BigDecimal bd2 = toBigDecimal(b);
		
		return bd1.multiply(bd2);
	}
	
	public static BigDecimal divide(double a, double b, int scale) {
		return divide(a, b, scale, RoundingMode.HALF_UP);
	}
	
	public static BigDecimal divide(double a, double b, int scale, RoundingMode mode) {
		if(b == 0) {
			return null;
		}
		
		BigDecimal bd1 = toBigDecimal(a);
		BigDecimal bd2 = toBigDecimal(b);
		
		return bd1.divide(bd2, scale, mode);
	}
	
	public static BigDecimal toBigDecimal(Object obj) {
		if(obj == null) {
			return null;
		}
		
		try {
			BigDecimal bd = new BigDecimal(obj.toString());
			return bd;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static List<String> permutation(String source, int targetSize) {
		return permutation(source, targetSize, 1000*1000);
	}
	
	public static List<String> permutation(String source, int targetSize, int maxSize) {
		boolean isSafe = isSafePermutation(source, targetSize, maxSize);
		if(!isSafe) {
			return new ArrayList<String>();
		}
		
		PermutationGenerator nick = new PermutationGenerator(source, targetSize);
		List<String> result = nick.getResult();
		
		return result;
	}
	
	private static boolean isSafePermutation(String source, int targetSize, int maxSize) {
		String count = permutation(source.length(), targetSize);
		if(count == null) {
			return false;
		}
		BigDecimal b1 = toBigDecimal(count);
		if(b1 != null && b1.intValue() > maxSize) {
			C.pl2("The limit[" + maxSize + "] has been reached, current[" + b1.intValue() + "].");
			return false;
		}
		
		return true;
	}
	
	public static String permutation(int numberOfSamples) {
		return permutation(numberOfSamples, numberOfSamples);
	}
	
	public static String permutation(int numberOfSamples, int targetSize) {
		return permutationWithLimit(numberOfSamples, targetSize, 199);
	}
	
	public static String permutationWithLimit(int numberOfSamples, int maxLen) {
		return permutationWithLimit(numberOfSamples, numberOfSamples, maxLen);
	}
	
	public static String permutationWithLimit(int numberOfSamples, int targetSize, int maxLen) {
		if(numberOfSamples <= 0 || targetSize <= 0 || targetSize > numberOfSamples) {
			return null;
		}
		
		BigDecimal grand = toBigDecimal(1);
		for(int i = 0; i < numberOfSamples; i++) {
			if(i >= targetSize) {
				break;
			}
			int current = numberOfSamples - i;
			BigDecimal bd = toBigDecimal(current);
			grand = grand.multiply(bd);
			String temp = grand.toBigInteger().toString();
			if(temp.length() > maxLen) {
				C.pl2("The limit[" + maxLen + "] has been reached, current[" + temp + "].");
				return null;
			}
		}
		
		String result = grand.toBigInteger().toString();
		
		return result;
	}
	
	public static BigDecimal toBigDecimal(String value) {
		BigDecimal bd = null;
		try {
			bd = new BigDecimal(value);
			return bd;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String formatNumber(Object source, int scale) {
		if(source == null) {
			return null;
		}
		
		Number number = toBigDecimal(source);
		if(number == null) {
			return source.toString();
		}
		
		NumberFormat pretty = NumberFormat.getNumberInstance();
		pretty.setMaximumFractionDigits(scale);
		pretty.setMinimumFractionDigits(scale);
		pretty.setRoundingMode(RoundingMode.HALF_UP);
		try {
			String temp = pretty.format(number);
			return temp;
		} catch (Exception ex) {
			return source.toString();
		}
	}
	
	public static Double toDouble(String src) {
		if(src == null) {
			return null;
		}
		
		try {
			return Double.parseDouble(src);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static Integer toInteger(String str) {
		if(str == null) {
			return null;
		}
		
		try {
			return Integer.parseInt(str);
		} catch (Exception exp) {
			return null;
		}
	}
	
	public static Integer toIntegerByRadius(String src, int radius) {
		if(src == null) {
			return null;
		}
		
		try {
			Integer value = Integer.parseInt(src, radius);
			return value;
		} catch (Exception ex) {
			return null;
		}
	}

	public static Integer toInteger(String str, Integer def) {
		Integer temp = toInteger(str);
		if(temp != null) {
			return temp;
		} else {
			return def;
		}
	}
	
	public static double average(List<Integer> numbers) {
		int sum = 0;
		for(Integer value: numbers) {
			if(value != null) {
				sum += value;
			}
		}
		
		double avg = sum / (numbers.size() + 0.0);
		return avg;
	}
	
	
	public static String toPrettyString(double value, int scale) {
		String str = setDoubleScale(value, scale);
		return StrUtil.removePointZeroes(str);
	}
	
	public static String temperature(double value, String unit, int scale) {
		XXXUtil.checkRange(scale, 0, 99);
		String degreeFahrenheit = "Fa";
		String degreeCelsius = "Ce";
		double targetValue = 0;
		String targetUnit = null;
		if(StrUtil.equals(degreeFahrenheit, unit)) {
			targetValue = (value - 32) / 1.8;
			targetUnit = degreeCelsius;
		} else if(StrUtil.equals(degreeCelsius, unit)) {
			targetValue = 1.8 * value + 32;
			targetUnit = degreeFahrenheit;
		} else {
			XXXUtil.alert("Illegal unit {0}, should be one of {1}, {2}.", unit, "Fa, Ce");
		}
		
		String va = setDoubleScale(targetValue, scale);
		String king = StrUtil.removePointZeroes(va) + " " + targetUnit;
		
		return king;
	}
	
	public static List<String> weight(double value, String unit, int scale) {
		XXXUtil.checkRange(scale, 0, 99);
		AlinkMap<String, Double> map = WEIGHT_UNITS;
		
		List<String> keys = Lists.newArrayList(map.keySet());

		String paramKey = unit.toLowerCase();
		Double paramFactor = map.get(paramKey);
		if(paramFactor == null) {
			XXXUtil.alert("Illegal unit {0}, should be one of {1}.", unit, keys);
		}

		List<String> values = Lists.newArrayList();
		for(int i = 0; i < keys.size(); i++) {
			String currentKey = keys.get(i);
			Double currentFactor = map.get(currentKey);
			Double king = currentFactor * value / paramFactor;
			String va = setDoubleScale(king, scale);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}
	
	public static List<String> shortDistance(double value, String unit, int scale) {
		XXXUtil.checkRange(scale, 0, 99);
		AlinkMap<String, Double> map = SHORT_DISTANCE_UNITS;
		
		List<String> keys = Lists.newArrayList(map.keySet());

		String paramKey = unit.toLowerCase();
		Double paramFactor = map.get(paramKey);
		if(paramFactor == null) {
			XXXUtil.alert("Illegal unit {0}, should be one of {1}.", unit, keys);
		}

		List<String> values = Lists.newArrayList();
		for(int i = 0; i < keys.size(); i++) {
			String currentKey = keys.get(i);
			Double currentFactor = map.get(currentKey);
			Double king = currentFactor * value / paramFactor;
			String va = setDoubleScale(king, scale);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}

	public static List<String> longDistance(double value, String unit, int scale) {
		XXXUtil.checkRange(scale, 0, 99);
		AlinkMap<String, Double> map = LONG_DISTANCE_UNITS;
		
		List<String> keys = Lists.newArrayList(map.keySet());

		String paramKey = unit.toLowerCase();
		Double paramFactor = map.get(paramKey);
		if(paramFactor == null) {
			XXXUtil.alert("Illegal unit {0}, should be one of {1}.", unit, keys);
		}

		List<String> values = Lists.newArrayList();
		for(int i = 0; i < keys.size(); i++) {
			String currentKey = keys.get(i);
			Double currentFactor = map.get(currentKey);
			Double king = currentFactor * value / paramFactor;
			String va = setDoubleScale(king, scale);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}

	public static List<String> fileSize(double value, String unit, int scale) {
		XXXUtil.checkRange(scale, 0, 99);
		Map<String, Double> map = new LinkedHashMap<>();
		Double base = 1.0;
		String units = Konstants.FILE_SIZE_UNIT;
		for(int k = 0; k < units.length(); k++) {
			String key = k == 0 ? "B" : (units.charAt(k) + "B");
			double fixedValue = base * Math.pow(Konstants.FILE_SIZE_STEP, units.length() - 1 - k);
			map.put(key, fixedValue);
		}
		
		List<String> keys = Lists.newArrayList(map.keySet());

		String paramKey = unit.toUpperCase();
		Double paramFactor = map.get(paramKey);
		if(paramFactor == null) {
			XXXUtil.alert("Illegal unit {0}, should be one of {1}.", unit, keys);
		}

		List<String> values = Lists.newArrayList();
		for(int i = 0; i < keys.size(); i++) {
			String currentKey = keys.get(i);
			Double currentFactor = map.get(currentKey);
			Double king = currentFactor * value / paramFactor;
			if(king < 0.0001) {
				continue;
			}
			String va = setDoubleScale(king, scale);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}
	
	/****
	 * 15 hour
	 * @param value
	 * @param unit
	 * @return
	 */
	public static String dhmsStrOfTime(double value, String unit) {
		double currentFactor = TIME_UNITS.get("second");
		double paramFactor = TIME_UNITS.get(unit.toLowerCase());
		double temp = currentFactor * value / paramFactor;
		if(temp > Integer.MAX_VALUE) {
			XXXUtil.alerto("Too big of a value ${origin}, try less than {0} in seconds.", Integer.MAX_VALUE);
		}
		int seconds = (int)temp;

		return dhmsStrOfSeconds(seconds);
	} 

	public static List<String> timeDuration(double value, String unit, int scale) {
		XXXUtil.checkRange(scale, 0, 99);
		Map<String, Double> map = TIME_UNITS;
		
		List<String> keys = Lists.newArrayList(map.keySet());

		String paramKey = unit.toLowerCase();
		Double paramFactor = map.get(paramKey);
		if(paramFactor == null) {
			XXXUtil.alert("Illegal unit {0}, should be one of {1}.", unit, keys);
		}

		List<String> values = Lists.newArrayList();
		for(int i = 0; i < keys.size(); i++) {
			String currentKey = keys.get(i);
			Double currentFactor = map.get(currentKey);
			Double king = currentFactor * value / paramFactor;
			String va = setDoubleScale(king, scale);
			String temp = StrUtil.removePointZeroes(va);
			if(StrUtil.is0(temp)) {
				continue;
			}
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}
	
	/****
	 * 69720 seconds
	 * 8day1hour40minutes11second
	 */
	public static List<Integer> dhmsOfSeconds(int seconds) {
		List<Integer> values = Lists.newArrayList();
		int[] secondsPer = {Konstants.SECONDS_PER_DAY, Konstants.SECONDS_PER_HOUR, Konstants.SECONDS_PER_MINUTE};
		int remain = seconds;
		for(int item : secondsPer) {
			int mod = remain / item;
			remain = remain % item;
			values.add(mod);
		}
		values.add(remain);
		
		return values;
	}
	
	public static String dhmsStrOfSeconds(int seconds) {
		return dhmsStrOfSeconds(seconds, false);
	}
	
	public static String dhmsStrOfSeconds(int seconds, boolean inEnglish) {
		List<Integer> items = dhmsOfSeconds(seconds);
		List<String> units = Lists.newArrayList("day", "hour", "min", "second");
		if(!inEnglish) {
			units = Lists.newArrayList(Konstants.CHINESE_DAY_TIAN, Konstants.CHINESE_HOUR_XIAOSHI, Konstants.CHINESE_MINUTE_FEN, Konstants.CHINESE_SECOND_MIAO);
		}
		XXXUtil.shouldBeEqual(items.size(), units.size());
		StringBuffer sb = StrUtil.sb();
		int count = 0;
		for(int i = 0 ; i < items.size(); i++) {
			Integer item = items.get(i);
			if(item != 0) {
				if(inEnglish) {
					if(count != 0) {
						sb.append(" ");
					}
					sb.append(item).append(" ").append(units.get(i));
					boolean isPlural = item > 1;
					if(isPlural) {
						sb.append("s");
					}
					count++;
				} else {
					sb.append(item).append(units.get(i));
				}
			}
		}
		
		String temp = sb.toString();
		if(temp.isEmpty()) {
			temp = 0 + units.get(3);
		}
		
		return temp;
	}
	
	public static boolean isBetween(double target, double good, double bad) {
		double min = good, max = bad;
		if(good > bad) {
			min = bad;
			max = good;
		}
		
		boolean flag = target <= max && target >= min;
		return flag;
	}

	public static String factorialExpressionOf(int number) {
		XXXUtil.shouldBeTrue(number >= 0);
		
		if(number <= 1) {
			return number + "!";
		}
		
		String total = "";
		byte temp = 1;
		while(temp < number) {
			total += temp + "*";
			temp++;
		}
		total += number;
		
		return total;
	}

	public static BigDecimal factorialOf(int number) {
		XXXUtil.shouldBeTrue(number >= 0);
		
		BigDecimal total = toBigDecimal(1);
		int temp = number;
		while(temp > 1) {
			total = total.multiply(toBigDecimal(temp--));
		}
		
		return total;
	}
	
	public static int fibonacciCoins(int amount, List<Integer> units) {
		int count = 0;
		if(units.contains(amount)) {
			count += 1;
		}

		int half = amount / 2;
		for(int i = 1; i <= half; i++) {
			int ka = i;
			int kb = amount - i;
			count += fibonacciCoins(ka, units) * fibonacciCoins(kb, units);
		}
		
		return count;
	}
	
	public static int[] kIntsOf(int value, int k) {
		int[] arr = new int[k];
		Arrays.fill(arr, value);
		
		return arr;
	}

	/***
	 * 1. no space
	 * @param source "1,5,3,5-7,23"
	 * @return "1,5,3,6,7,23"
	 */
	public static List<Integer> parsePrintPageNumbers(String source, int maxPageNumber) {
		int maxLength = String.valueOf(Integer.MAX_VALUE).length() + 1;
		List<Integer> numbers = new ArrayList<>();
		List<String> items = StrUtil.split(source, ',');
		String tempRegex = "(\\d{1,{0}}|(\\d{1,{0}})\\s*\\-\\s*(\\d{1,{0}}))";
		String regex = StrUtil.occupy(tempRegex, maxLength);
		for(String item : items) {
			String[] params = StrUtil.parseParams(regex, item);
			if(params == null) {
				throw new MexException("Illegal page numbers expression: " + item + " from " + source + ".");
			}
			
			String fixedValue = params[0];
			String rangeA = params[1];
			String rangeB = params[2];
			
			if(rangeA == null) {
				Integer value = MathUtil.toInteger(fixedValue);
				if(value > maxPageNumber) {
					throw new MexException("The page number " + value + " should not exceed max page number " + maxPageNumber + ".");
				}
				if(!numbers.contains(value)) {
					numbers.add(value);
				}
			} else {
				Integer start = MathUtil.toInteger(rangeA);
				Integer end = MathUtil.toInteger(rangeB);
				if(start <= end) {
					if(end > maxPageNumber) {
						throw new MexException("The page number " + end + " should not exceed max page number " + maxPageNumber + ".");
					}
					for(int i = start; i <= end; i++) {
						int value = i;
						if(!numbers.contains(value)) {
							numbers.add(value);
						}
					}
				} else {
					if(start > maxPageNumber) {
						throw new MexException("The page number " + start + " should not exceed max page number " + maxPageNumber + ".");
					}
					for(int i = start; i >= end; i--) {
						int value = i;
						if(!numbers.contains(value)) {
							numbers.add(value);
						}
					}
				}
			}
		}
		
		return numbers;
	}
}
