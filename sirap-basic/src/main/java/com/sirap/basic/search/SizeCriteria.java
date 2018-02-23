package com.sirap.basic.search;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class SizeCriteria extends MexItem {
	
	private static final long serialVersionUID = 1L;

	public static final String EQUAL = "=";
	public static final String GREATER = ">";
	public static final String LESS = "<";
	public static final String ABOUT = "~";
	
	public static final float DIFF_EQUAL = 0.01f;
	public static final float DIFF_ABOUT = 0.1f;
	
	protected String operator;
	protected double value;
	
	public String getOperator() {
		return operator;
	}

	public double getValue() {
		return value;
	}
	
	public SizeCriteria() {}
	
	public SizeCriteria(String record) {
		if(!parse(record)) {
			XXXUtil.alert("Not able to parse: " + record);
		}
	}

	@Override
	public boolean parse(String record) {
		String regex = "([=><~])" + Konstants.REGEX_FLOAT;
		String[] params = StrUtil.parseParams(regex, record);
		if(params != null) {
			operator = params[0];
			value = Double.parseDouble(params[1]);
			
			return true;
		}
		
		return false;
	}
	
	public boolean isGood(double size) {
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
			double[] range = minAndMax(value, DIFF_ABOUT);
			boolean flag = (size < range[1] && size > range[0]);
			return flag;
		}
		
		if(EQUAL.equals(operator)) {
			double[] range = minAndMax(value, DIFF_EQUAL);
			boolean flag = (size < range[1] && size > range[0]);
			return flag;
		}
		
		throw new UnsupportedOperationException("No such operator like " + operator);
	}
	
	private double[] minAndMax(double base, float diffPerc) {
		double max = value * (1 + diffPerc);
		double min = value * (1 - diffPerc);
		
		return new double[]{min, max};
	}

	@Override
	public String toString() {
		return "SizeOperative [operator=" + operator + ", value=" + value + "]";
	}
}
