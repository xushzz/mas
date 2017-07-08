package com.sirap.basic.math;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.regex.Matcher;

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
		
		double tempResult = evalSimple(temp);
		BigDecimal bd = MathUtil.divide(tempResult, 1, scale);
		String result = StrUtil.removePointZeroes(bd.toPlainString());

		return result;
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
		content = StrUtil.removePointZeroes(content);
		char start = content.charAt(0);
		if(start == '-') {
			content = "_" + content.substring(1);
		}

		String regexAtLeastOneOperator = "[+\\-x\\*/]";
		Matcher ma = StrUtil.createMatcher(regexAtLeastOneOperator, content);
		if(!ma.find()) {
			String msg = "Expression contains no operator.";
			throw new MexException(msg);
		}

		return content;
	}
	
	public static double evalSimple(String expression) {
		String temp = cover(expression);
		boolean hasPreviousBeenSet = false;
		boolean hasAceBeenSet = false;
		
		Calp previous = null;
		Calp ace = null;
		Calp bax = null;
		
		StringBuffer what = new StringBuffer();
		String operators = "+-*xX/";

		boolean nextShouldBeOperator = false;
		for(int i = 0; i < temp.length(); i++) {
			char ch = temp.charAt(i);
			boolean isOperator = operators.indexOf(ch) >= 0;
			if(nextShouldBeOperator && !isOperator) {
				String msg = "There should be an operator before {0}, index of {1}";
				throw new MexException(StrUtil.occupy(msg, ch, i));
			}
			if(isOperator) {
				if(nextShouldBeOperator) {
					nextShouldBeOperator = false;
				}
				String number = what.toString();
				if(EmptyUtil.isNullOrEmpty(number)) {
					String msg = "There should be a number between {0} and {1}.";
					throw new MexException(StrUtil.occupy(msg, "SHIT", ch));
				}
				what.setLength(0);
				Calp item = createCalp(number, ch);
				if(!hasPreviousBeenSet) {
					hasPreviousBeenSet = true;
					previous = item;
					continue;
				}
				if(!hasAceBeenSet) {
					hasAceBeenSet = true;
					ace = item;
					continue;
				}
				bax = item;
				if(ace.isHeavierThan(previous)) {
					double tempLate = late(ace.number, ace.operator, bax.number);
					ace.number = tempLate;
					ace.operator = bax.operator;
				} else {
					double tempLate = late(previous.number, previous.operator, ace.number);
					previous.number = tempLate;
					previous.operator = ace.operator;
					ace = bax;
				}
			} else {
				if(ch == '(') {
					if(what.length() > 0) {
						String msg = "There should be an operator between {0} and left parenthesis {1}.";
						throw new MexException(StrUtil.occupy(msg, what, ch));
					}
					int nextMatched = indexOfNextMatchedParenthesis(temp, i);
					if(nextMatched < 0) {
						throw new MexException("There exists unmatched parenthesis for the (, index of " + i);
					}
					String subString = temp.substring(i + 1, nextMatched);
					double subResult = evalSimple(subString);
					what.append(subResult);
					nextShouldBeOperator = true;
					i = nextMatched;
				} else {
					what.append(ch);
				}
			}
		}
		
		String number = what.toString();
		if(number.isEmpty()) {
			String msg = "The expression {0} shouldn't end with an operator.";
			throw new MexException(StrUtil.occupy(msg, expression));
		} else {
			Calp item = createCalp(number, '$');
			if(!hasPreviousBeenSet) {
				return item.number;
			}
			if(!hasAceBeenSet) {
				ace = item;
				double tempFinal = late(previous.number, previous.operator, ace.number);
				return tempFinal;
			}
			bax = item;
			if(ace.isHeavierThan(previous)) {
				double tempLate = late(ace.number, ace.operator, bax.number);
				ace.number = tempLate;
				ace.operator = bax.operator;
			} else {
				double tempLate = late(previous.number, previous.operator, ace.number);
				previous.number = tempLate;
				previous.operator = ace.operator;
				ace = bax;
			}
			
			double tempFinal = late(previous.number, previous.operator, ace.number);
			return tempFinal;
		}
	}
	
	private static Calp createCalp(String what, char op) {
		Calp item = new Calp();
		String value = uncover(what.toString());
		item.origin = value;
		item.number = toDouble(value);
		item.operator = op;
		
		return item;
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
			String msg = "Unable to parse {0} as double.";
			throw new MexException(StrUtil.occupy(msg, src));
		}
	}
	
	private static int indexOfNextMatchedParenthesis(String source, int indexOfStartParenthesis) {
		Stack<String> de = new Stack<>();
		int len = source.length();
		for(int i = indexOfStartParenthesis; i < len; i++) {
			char ch = source.charAt(i);
			if(ch == '(') {
				de.push(ch + "");
			} else if(ch == ')') {
				if(de.isEmpty()) {
					throw new MexException("There exists unmatched parenthesis for the ), index of " + i);
				} else {
					de.pop();
					if(de.isEmpty()) {
						return i;
					}
				}
			}
		}
		
		return -1;
	}
}

class Calp {
	public String origin;
	public double number;
	public char operator = '$';
	
	public String toString() {
		String temp = origin + ", " + operator;
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
