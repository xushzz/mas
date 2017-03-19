package com.sirap.basic.math;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MexCalculator {
	
	private boolean selfDenial = false; 
	private String expression;
	private String result;
	
	public MexCalculator(String expression) {
		this.expression = expression;
		handle();
	}
	
	public MexCalculator(String expression, boolean selfDenial) {
		this.expression = expression;
		this.selfDenial = selfDenial;
		handle();
	}
	
	public String getResult() {
		return result;
	}
	
	private void handle() {
		if(selfDenial && !preProcess()) {
			return;
		}
		
		process(expression);
		postProcess();
	}
	
	private boolean preProcess() {
		Matcher m = Pattern.compile(SimpleCalculator.ACCEPTABLE_OPERATORS).matcher(expression);
		return m.find();
	}
	
	private void postProcess() {
		if(selfDenial && expression.equals(result)) {
			result = null;
		}
	}
	
	private void process(String current) {
		String regex = "\\(([^\\(]*?)\\)";
		Matcher m = Pattern.compile(regex).matcher(current);
		if(m.find()) {
			String tempExpression = m.group(1);
			String tempResult = SimpleCalculator.evaluate(tempExpression);
			if(tempResult == null) {
				return;
			}
			current = current.replace(m.group(), disguise(tempResult));
			process(current);
		} else {
			result = SimpleCalculator.evaluate(current);
		}
	}
	
	private String disguise(String value) {
		String temp = value;
		if(temp.startsWith("-")) {
			temp = temp.replace("-", "_");
		}
		
		return temp;
	}
	

	public static String evaluate(String expression, boolean selfDenial) {
		MexCalculator nick = new MexCalculator(expression, selfDenial);
		String result = nick.getResult();
		
		return result;
	}

	public static String evaluate(String expression) {
		MexCalculator nick = new MexCalculator(expression);
		String result = nick.getResult();
		
		return result;
	}
}
