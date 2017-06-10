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

public class SimpleCalculator {
	private String expression;
	private String result;
	private int scale = 20;
	
	private List<BigDecimal> numbers = new ArrayList<BigDecimal>();
	private List<String> operators = new ArrayList<String>();

	SimpleCalculator(String expression) {
		this.expression = expression;
		process();
	}
	
	String getResult() {
		return result;
	}
	
	private void process() {
		boolean toContinue = parseOperandsAndOperators();
		if(!toContinue) {
			return;
		}
		
		BigDecimal grand = loopStuff();
		if(grand == null) {
			return;
		}
		
		result = StrUtil.removePointZeroes(grand.toPlainString());
	}
	
	public static final String ACCEPTABLE_OPERATORS = "([\\+|\\-|\\*|X|x|/])";

	private boolean parseOperandsAndOperators() {
		if(expression == null) {
			return false;
		}
		
		String regex = ACCEPTABLE_OPERATORS;
		String[] items = expression.split(regex);

		Matcher m = Pattern.compile(regex).matcher(expression);
		while(m.find()) {
			operators.add(m.group());
		}
		
		int numberOfItems = items.length;
		int numberOfOperators = operators.size();
		
		if(numberOfItems != numberOfOperators + 1) {
			return false;
		}
		
		if(numberOfOperators > 0) {
			String firstOperator = operators.get(0);
			String firstItem = items[0].trim();
			
			if(firstItem.isEmpty() && (firstOperator.equals("-") || firstOperator.equals("+"))) {
				items[0] = "0";
			}
		}
		
		for(int i = 0; i < numberOfItems; i++) {
			String item = items[i].trim();
			item = recover(item);
			Double d = MathUtil.toDouble(item);
			if(d == null) {
				return false;
			}
			
			BigDecimal bd = MathUtil.toBigDecimal(d); 
			numbers.add(bd);
		}
		
		return true;
	}
	
	private String recover(String value) {
		String temp = value;
		if(temp.startsWith("_")) {
			temp = temp.replace("_", "-");
		}
		
		return temp;
	}
	
	private BigDecimal loopStuff() {
		List<BigDecimal> numbers2 = new ArrayList<BigDecimal>(numbers);
		List<String> operators2 = new ArrayList<String>(operators);
		
		int index = indexOfNextHeavierOperator(operators2);
		while(index >= 0) {
			String op = operators2.get(index);
			BigDecimal b1 = numbers2.get(index);
			BigDecimal b2 = numbers2.get(index+1);
			BigDecimal b3 = calc(b1, b2, op);
			if(b3 == null) {
				return null;
			}
			numbers2.set(index, b3);
			
			numbers2.remove(index + 1);
			operators2.remove(index);
			
			index = indexOfNextHeavierOperator(operators2);
		}
		
		BigDecimal grand = numbers2.get(0);

		for(int i = 0; i < operators2.size(); i++) {
			String op = operators2.get(i);
			BigDecimal bd = numbers2.get(i+1);
			grand = calc(grand, bd, op);
		}

		return grand;
	}
	
	private int indexOfNextHeavierOperator(List<String> operators) {
		for(int i = 0; i < operators.size(); i++) {
			String op = operators.get(i);
			boolean flag = StrUtil.isRegexMatched("[\\*|x|/]", op);
			if(flag) {
				return i;
			}
		}
		
		return -1;
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
			bd3 = bd1.divide(bd2, scale, RoundingMode.HALF_UP);
		}
		
		return bd3;
	}
	
	public static String evaluate(String expression) {
		SimpleCalculator nick = new SimpleCalculator(expression);
		String result = nick.getResult();
		
		return result;
	}
}
