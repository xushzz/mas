package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class MexNumberConverter {
	
	public static final String HEXDECIMAL = "h,hex";
	public static final String DECIMAL = "d,dec";
	public static final String OCTAL = "o,oct";
	public static final String BINARY = "b,bin";
	
	private String type;
	
	private List<String> sourceList = new ArrayList<String>();
	private List<String> decList = new ArrayList<String>();
	private List<String> hexList = new ArrayList<String>();
	private List<String> octList = new ArrayList<String>();
	private List<String> binList = new ArrayList<String>();
	private List<Integer> maxLen = new ArrayList<Integer>();
	private List<String> results = new ArrayList<String>();

	public MexNumberConverter(String type, String expression) {
		this.type = type;
		sourceList = StrUtil.splitByRegex(expression, ",|\\s");
		
		process();
		organizeResults();
	}
	
	public List<String> getResult() {
		return results;
	}
	
	private void process() {
		for(String exp : sourceList) {
			if(EmptyUtil.isNullOrEmpty(exp)) {
				continue;
			}
			
			Integer dec = parseIntByType(exp, type);
			if(dec == null) {
				continue;
			}
			
			decList.add(Integer.toString(dec));
			hexList.add(Integer.toHexString(dec).toUpperCase());
			octList.add(Integer.toOctalString(dec));
			String binStr = Integer.toBinaryString(dec);
			binList.add(binStr);
			maxLen.add(binStr.length());
		}
	}
	
	private Integer parseIntByType(String source, String type) {
		Integer value = null;
		if(StrUtil.existsIgnoreCase(HEXDECIMAL.split(","), type)) {
			value = MathUtil.toIntegerByRadius(source, 16);
		} else if(StrUtil.existsIgnoreCase(DECIMAL.split(","), type)) {
			value = MathUtil.toIntegerByRadius(source, 10);
		} else if(StrUtil.existsIgnoreCase(OCTAL.split(","), type)) {
			value = MathUtil.toIntegerByRadius(source, 8);
		} else if(StrUtil.existsIgnoreCase(BINARY.split(","), type)) {
			value = MathUtil.toIntegerByRadius(source, 2);
		} else {
			value = MathUtil.toInteger(source);
		}
		
		return value;
	}
	
	private void organizeResults() {
		if(EmptyUtil.isNullOrEmpty(hexList)) {
			return;
		}
		
		results.add("Hex\t" + listToString(hexList, maxLen));
		results.add("Dec\t" + listToString(decList, maxLen));
		results.add("Oct\t" + listToString(octList, maxLen));
		results.add("Bin\t" + listToString(binList, maxLen));
	}
	
	private String listToString(List<String> items, List<Integer> maxLen) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < maxLen.size(); i++) {
			String item = items.get(i);
			int len = maxLen.get(i) + 2;
			String value = StrUtil.extend(item, len);
			sb.append(value);
		}
		
		return sb.toString();
	}
}
