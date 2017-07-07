package com.sirap.basic.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class SimCal {
	
	public static String evaluate(String content) {
		return evaluate(content, 2);
	}
	
	public static String evaluate(String content, int scale) {
		String temp = preProcess(content);
		
		double tempResult = evaluateInner(temp);
		BigDecimal bd = MathUtil.divide(tempResult, 1, scale);
		String result = StrUtil.removePointZeroes(bd.toPlainString());

		return result;
	}
	
	private static double evaluateInner(String content) {
		String temp = content;
		String regex = "\\(([^\\(]*?)\\)";
		Matcher ma = StrUtil.createMatcher(regex, temp);
		if(ma.find()) {
			String expression = ma.group(1);
			double tempResult = evalSimple(expression);
			temp = temp.replace(ma.group(), cover(tempResult + ""));
			return evaluateInner(temp);
		} else {
			double value = evalSimple(temp);
			return value;
		}
	}
	
	private static String cover(String value) {
		String temp = value;
		if(temp.startsWith("-")) {
			temp = temp.replace("-", "_");
		}
		
		return temp;
	}
	
	private static String uncover(String value) {
		String temp = value;
		if(temp.startsWith("_")) {
			temp = temp.replace("_", "-");
		}
		
		return temp;
	}
	
	private static String preProcess(String expression) {
		String content = expression.replaceAll("\\s+", "");
		content = content.replaceAll("^0+", "0");
		content = StrUtil.removePointZeroes(content);
		char start = content.charAt(0);
		if(start == '-') {
			content = "_" + content.substring(1);
		}
		
		if(!isMatchedParenthesis(content)) {
			String msg = "Contains unmatched parenthesis";
			throw new MexException(msg);
		}
		
		String regexStartsWith = "^[+\\-x\\*/\\)]";
		Matcher ma = StrUtil.createMatcher(regexStartsWith, content);
		if(ma.find()) {
			String msg = "Expression can't start with [" + ma.group() + "]";
			throw new MexException(msg);
		}

		String regexAtLeastOneOperator = "[+\\-x\\*/]";
		ma = StrUtil.createMatcher(regexAtLeastOneOperator, content);
		if(!ma.find()) {
			String msg = "Expression contains no operator";
			throw new MexException(msg);
		}
		
		String regexEndsWith = "[+\\-x\\*/\\(]$";
		ma = StrUtil.createMatcher(regexEndsWith, content);
		if(ma.find()) {
			String msg = "Expression can't end with [" + ma.group() + "]";
			throw new MexException(msg);
		}

		String regexConnectedOps = "[^+\\-x/\\*\\(]\\(";
		ma = StrUtil.createMatcher(regexConnectedOps, content);
		if(ma.find()) {
			String msg = "There should be an operator between " + ma.group();
			throw new MexException(msg);
		}
		
		regexConnectedOps = "\\)[^+\\-x/\\*\\)]";
		ma = StrUtil.createMatcher(regexConnectedOps, content);
		if(ma.find()) {
			String msg = "There should be an operator between " + ma.group();
			throw new MexException(msg);
		}

		return content;
	}
	
	public static double evalSimple(String expression) {
		String content = cover(expression);
		List<Calp> items = fetchAllItems(content);
		if(items.isEmpty()) {
			throw new MexException("No operands at all, illegal [" + content + "]");
		}
		
		Calp previous = items.get(0);
		if(items.size() == 1) {
			return previous.number;
		}
		Calp ace = items.get(1);
		for(int i = 2; i < items.size(); i++) {
			Calp bax = items.get(i);

			if(ace.isHeavierThan(previous)) {
				double temp = late(ace.number, ace.operator, bax.number);
				ace.number = temp;
				ace.operator = bax.operator;
			} else {
				double temp = late(previous.number, previous.operator, ace.number);
				previous.number = temp;
				previous.operator = ace.operator;
				ace = bax;
			}
		}
		
		double temp = late(previous.number, previous.operator, ace.number);
		return temp;
	}
	
	public static List<Calp> fetchAllItems(String expression) {
		XXXUtil.nullOrEmptyCheck(expression, "expression");
		String temp = expression;
		String regexOperandAndOperator = "^([^+\\-x\\*/]+)([+\\-x\\*/]|)";
		Pattern pa = Pattern.compile(regexOperandAndOperator, Pattern.CASE_INSENSITIVE);
		Matcher ma = pa.matcher(temp);
		List<Calp> items = new ArrayList<>();
		while(ma.find()) {
			String op = ma.group(2);
			Calp item = new Calp();
			String value = uncover(ma.group(1));
			item.number = toDouble(value);
			if(!EmptyUtil.isNullOrEmpty(op)) {
				item.operator = op.charAt(0);
			}
			items.add(item);
			int endIndex = ma.end(2);
			temp = temp.substring(endIndex);
			ma = pa.matcher(temp);
		}

		if(!temp.isEmpty()) {
			String msg = "There should be no operator at the beginning of " + temp;
			throw new MexException(msg);
		}
		
		return items;
	}
	
	private static double late(double a, char operator, double b) {
		if(operator == '+') {
			return a + b;
		} else if(operator == '-') {
			return a - b;
		} else if(operator == 'x' || operator == 'X' || operator == '*') {
			return a * b;
		} else if(operator == '/') {
			if(b == 0) {
				throw new MexException("Zero can't be a divisor.");
			}
			return a / b;
		}
		
		throw new MexException("Illegal operator [" + operator + "]");
	}
	
	private static double toDouble(String src) throws MexException {
		XXXUtil.nullOrEmptyCheck(src, "src");
		try {
			return Double.parseDouble(src);
		} catch (Exception ex) {
			throw new MexException("Unable to parse double " + src);
		}
	}
	
	public static boolean isMatchedParenthesis(String source) {
		Stack<String> de = new Stack<>();
		int len = source.length();
		for(int i = 0; i < len; i++) {
			char ch = source.charAt(i);
			if(ch == '(') {
				de.push(ch + "");
			} else if(ch == ')') {
				if(de.isEmpty()) {
					return false;
				} else {
					de.pop();
				}
			}
		}
		
		return de.isEmpty();
	}
}

class Calp {
	public double number;
	public char operator = '$';
	
	public String toString() {
		String temp = number + ", " + operator;
		return temp;
	}
	
	public boolean isHeavierThan(Calp other) {
		int levelA = level(operator);
		int levelB = level(other.operator);
		
		return levelA > levelB;
	}
	
	private int level(char ch) {
		int value = 0;
		if(ch == '+' || ch == '-') {
			value = 1;
		} else if (ch == 'x' || ch == '*' || ch == '/') {
			value = 2;
		}
		
		return value;
	}
}
