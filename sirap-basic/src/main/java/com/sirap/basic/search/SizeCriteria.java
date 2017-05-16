package com.sirap.basic.search;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

public class SizeCriteria extends MexItem {
	
	private static final long serialVersionUID = 1L;

	public static final String EQUAL = "=";
	public static final String GREATER = ">";
	public static final String LESS = "<";
	public static final String ABOUT = "~";
	
	public static final float DIFF_EQUAL = 0.01f;
	public static final float DIFF_ABOUT = 0.1f;
	
	private String operator;
	private long value;
	private char unit;
	
	public String getOperator() {
		return operator;
	}

	public long getValue() {
		return value;
	}

	@Override
	public boolean parse(String record) {
		String regex = "([=><~])" + Konstants.REGEX_FLOAT + "([" + Konstants.FILE_SIZE_UNIT + "])";
		String[] params = StrUtil.parseParams(regex, record);
		if(params != null) {
			operator = params[0];
			String baseValue = params[1];
			unit = params[2].toUpperCase().charAt(0);
			value = FileUtil.parseFileSize(baseValue, unit);
			
			return true;
		}
		
		return false;
	}
	
	public boolean isGood(long size) {
		if(operator == null) {
			return false;
		}

		if(GREATER.equals(operator)) {
			return size > value;
		}
		if(LESS.equals(operator)) {
			return size < value;
		}
		if(ABOUT.equals(operator)) {
			long[] range = minAndMax(value, DIFF_ABOUT);
			boolean flag = (size < range[1] && size > range[0]);
			return flag;
		}
		
		if(EQUAL.equals(operator)) {
			long[] range = minAndMax(value, DIFF_EQUAL);
			boolean flag = (size < range[1] && size > range[0]);
			return flag;
		}
		
		throw new UnsupportedOperationException("No such operator like " + operator);
	}
	
	private long[] minAndMax(long base, float diffPerc) {
		long max = (long)(value * (1 + diffPerc));
		long min = (long) (value * (1 - diffPerc));
		
		return new long[]{min, max};
	}

	@Override
	public String toString() {
		return "SizeOperative [operator=" + operator + ", value=" + value + "]";
	}
}
