package com.sirap.basic.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.math.PermutationGenerator;
import com.sirap.basic.tool.C;

public class MathUtil {
	
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
	
	public static String temperature(double value, String unit) {
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
		
		String va = setDoubleScale(targetValue, 2);
		String king = StrUtil.removePointZeroes(va) + " " + targetUnit;
		
		return king;
	}
	
	public static List<String> weight(double value, String unit) {
		Map<String, Double> map = new LinkedHashMap<>();
		map.put("kg", 1.0);
		map.put("lb", 2.2046226);
		map.put("oz", 35.2739619);
		
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
			String va = setDoubleScale(king, 2);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}
	
	public static List<String> distance(double value, String unit) {
		Map<String, Double> map = new LinkedHashMap<>();
		map.put("yard", 1.0);
		map.put("chi", 2.7432);
		map.put("feet", 3.0);
		map.put("cun", 27.432);
		map.put("inch", 36.0);
		map.put("cm", 91.44);
		
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
			String va = setDoubleScale(king, 6);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}

	public static List<String> longDistance(double value, String unit) {
		Map<String, Double> map = new LinkedHashMap<>();
		map.put("nmi", 0.8689762);
		map.put("mile", 1.0);
		map.put("km", 1.609344);
		
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
			String va = setDoubleScale(king, 6);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}

	public static List<String> fileSize(double value, String unit) {
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
			String va = setDoubleScale(king, 6);
			values.add(StrUtil.removePointZeroes(va) + " " + currentKey);
		}
		
		return values;
	}
}
