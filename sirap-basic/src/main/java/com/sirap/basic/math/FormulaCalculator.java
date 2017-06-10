package com.sirap.basic.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class FormulaCalculator {
	private String expression;
	private String expLeft;
	private String expRight;
	private String result;
	public static final int SCALE = 20;

	private List<BigDecimal> numbersLeft = new ArrayList<BigDecimal>();
	private List<BigDecimal> factorsLeft = new ArrayList<BigDecimal>();

	private List<BigDecimal> numbersRight= new ArrayList<BigDecimal>();
	private List<BigDecimal> factorsRight = new ArrayList<BigDecimal>();

	public FormulaCalculator(String expression) {
		this.expression = expression;
		process();
	}
	
	public String getResult() {
		return result;
	}
	
	private void process() {
		String temp = "([_\\.\\d\\s\\+\\-X]+)";
		String regex = temp + "=" + temp;
		String[] tempArr = StrUtil.parseParams(regex, expression);
//		D.pl(tempArr);
		if(tempArr == null) {
			return;
		}
		
		expLeft = tempArr[0];
		expRight = tempArr[1];
		
		boolean toContinue = parseItems(expLeft, numbersLeft, factorsLeft);
//		D.pl(numbersLeft);
//		D.pl(factorsRight);
		if(!toContinue) {
			return;
		}
		
		toContinue = parseItems(expRight, numbersRight, factorsRight);
//		D.pl(numbersRight);
//		D.pl(factorsRight);
		if(!toContinue) {
			return;
		}
		
		for(BigDecimal number:numbersLeft) {
			numbersRight.add(number.negate());
		}
		numbersLeft.clear();
		
		for(BigDecimal number:factorsRight) {
			factorsLeft.add(number.negate());
		}
		if(factorsLeft.isEmpty()) {
			return;
		}
		factorsRight.clear();
		
		BigDecimal factor = MathUtil.add(factorsLeft);
		BigDecimal number = MathUtil.add(numbersRight);
		if(factor.doubleValue() == 0) {
			String numerator = StrUtil.removePointZeroes(number.toPlainString());
			result = numerator + "/0";
			return;
		}
		
		BigDecimal grand = calc(number, factor, "/");
		String tempResult = StrUtil.removePointZeroes(grand.toPlainString());
		int actualScale = tempResult.length() - tempResult.indexOf(".") - 1;
		if(actualScale < SCALE) {
			result = tempResult;
			return;
		}

		String numerator = StrUtil.removePointZeroes(number.toPlainString());
		String denominator = StrUtil.removePointZeroes(factor.toPlainString());
		result = numerator + "/" + denominator + "=" + tempResult;
	}
	
	public static final String ACCEPTABLE_OPERATORS = "([\\+|\\-|\\*|X|x|/])";

	private boolean parseItems(String exp, List<BigDecimal> numbers, List<BigDecimal> factors) {
		if(exp == null) {
			return false;
		}
		
		List<String> items = new ArrayList<String>();
		String regex = "(|[\\+\\-])([^\\+\\-]+)";
		Matcher m = Pattern.compile(regex).matcher(exp);
		while(m.find()) {
			items.add(m.group());
		}
//		D.shit(items);
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			item = trimSigns(item);
//			D.shit(item);
			Double number = MathUtil.toDouble(item);
			if(number == null) {
				String temp = StrUtil.parseParam("(.*?)x", item);
				if(temp == null) {
					return false;
				}
				
				Double factor = MathUtil.toDouble(temp);
				if(factor == null) {
					if(temp.equals("-")) {
						factor = -1.0;
					} else if(temp.equals("")) {
						factor = 1.0;
					}
				}
				if(factor == null) {
					return false;
				}
				factors.add(MathUtil.toBigDecimal(factor));
			} else {
				numbers.add(MathUtil.toBigDecimal(number));
			}
		}
		
		return true;
	}
	
	public static String trimSigns(String source) {
		int len = source.length();
		if(len < 2) {
			return source;
		}
		
		String temp = source;
		char c1 = source.charAt(0);
		char c2 = source.charAt(1);
		if(c1 == '+') {
			if(c2 == '_') {
				temp = "-" + temp.substring(2);
			} else {
				temp = temp.substring(1);
			}
		} else if(c1 == '-') {
			if(c2 == '_') {
				temp = temp.substring(2);
			} else {
				temp = "-" + temp.substring(1);
			}
		}
		
		return temp;
	}

	private BigDecimal calc(BigDecimal bd1, BigDecimal bd2, String operator) {
		BigDecimal bd3 = null;
		if(operator.equals("+")) {
			bd3 = bd1.add(bd2);
		} else if(operator.equals("-")) {
			bd3 = bd1.subtract(bd2);
		} else if(StrUtil.isRegexMatched("[\\*|x]", operator)) {
			bd3 = bd1.multiply(bd2);
		} else if(operator.equals("/")) {
			if(bd2.doubleValue() == 0) {
				throw new MexException("Zero can't be a divisor.");
			}
			bd3 = bd1.divide(bd2, SCALE, RoundingMode.HALF_UP);
		}
		
		return bd3;
	}
	
	public static String evaluate(String expression) {
		FormulaCalculator nick = new FormulaCalculator(expression);
		String result = nick.getResult();
		
		return result;
	}
}
