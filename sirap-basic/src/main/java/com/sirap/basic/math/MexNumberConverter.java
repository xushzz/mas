package com.sirap.basic.math;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;
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
			
			Long dec = parseIntByType(exp, type);
			if(dec == null) {
				continue;
			}
			
			decList.add(Long.toString(dec));
			hexList.add(Long.toHexString(dec).toUpperCase());
			octList.add(Long.toOctalString(dec));
			String binStr = Long.toBinaryString(dec);
			binList.add(binStr);
			maxLen.add(binStr.length());
		}
	}
	
	private Long toLongByRadius(String src, int radius) {
		try {
			return Long.parseLong(src, radius);
		} catch (Exception ex) {
			return null;
		}
	}
	
	private Long parseIntByType(String source, String type) {
		if(StrUtil.existsIgnoreCase(HEXDECIMAL.split(","), type)) {
			return toLongByRadius(source, 16);
		} else if(StrUtil.existsIgnoreCase(DECIMAL.split(","), type)) {
			return toLongByRadius(source, 10);
		} else if(StrUtil.existsIgnoreCase(OCTAL.split(","), type)) {
			return toLongByRadius(source, 8);
		} else if(StrUtil.existsIgnoreCase(BINARY.split(","), type)) {
			return toLongByRadius(source, 2);
		} else {
			return toLongByRadius(source, 10);
		}
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
			String value = StrUtil.padRight(item, len);
			sb.append(value);
		}
		
		return sb.toString();
	}
}
