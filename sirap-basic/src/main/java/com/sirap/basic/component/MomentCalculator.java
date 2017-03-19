package com.sirap.basic.component;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MathUtil;

public class MomentCalculator {
	
	private static final int LEN_OF_UNIT_VALUES = 3;
	private static final int[][] RANGE_OF_UNIT_VALUES = {{0,23},{0,59},{0,59}};	

	private String source;
	private Calendar theMoment;
	private boolean isRangeCheck;
	private int[] unitValues = new int[LEN_OF_UNIT_VALUES];	
	
	public MomentCalculator(boolean isRangeCheck, String source) {
		this.isRangeCheck = isRangeCheck;
		this.source = source;
		
		if(verify()) {
			calculate();
		}
	}
	
	public Date getTargetMoment() {
		return DateUtil.calendarToDate(theMoment);
	}
	
	public Date recalculateTargetMoment() {
		theMoment.add(Calendar.HOUR_OF_DAY, unitValues[0]);
		theMoment.add(Calendar.MINUTE, unitValues[1]);
		theMoment.add(Calendar.SECOND, unitValues[2]);
		
		return DateUtil.calendarToDate(theMoment);
	}

	private boolean verify() {
		boolean isInputValid = parseByTimeUnits();
		if(isInputValid) {
			if(isRangeCheck) {
				return isRangeValid();
			}
			
			return true;
		}

		isInputValid = parseByFaceValues();
		if(isInputValid) {
			if(isRangeCheck) {
				return isRangeValid();
			}
			
			return true;
		}
		
		return false;
	}
	
	private void calculate() {
		if(isRangeCheck) {
			Calendar present = Calendar.getInstance();
			
			theMoment = Calendar.getInstance();
			theMoment.set(Calendar.HOUR_OF_DAY, unitValues[0]);
			theMoment.set(Calendar.MINUTE, unitValues[1]);
			theMoment.set(Calendar.SECOND, unitValues[2]);
			
			if(theMoment.before(present)) {
				theMoment.add(Calendar.HOUR_OF_DAY, 24);
			}
		} else {
			theMoment = Calendar.getInstance();
			theMoment.add(Calendar.HOUR_OF_DAY, unitValues[0]);
			theMoment.add(Calendar.MINUTE, unitValues[1]);
			theMoment.add(Calendar.SECOND, unitValues[2]);
		}
	}
	
	//5:05,5:04:07
	private boolean parseByFaceValues() {
		String[] values = source.split(":");
		if(values.length > LEN_OF_UNIT_VALUES) {
			return false;
		}
		
		for(int i = 0; i < values.length; i++) {
			String strValue = values[i];
			if(strValue.length() > 3) {
				return false;
			}
			
			Integer integerValue = MathUtil.toInteger(strValue);
			if(integerValue == null) {
				return false;
			}
			
			unitValues[i] = integerValue;
		}
		
		return true;
	}
	
	private boolean parseByTimeUnits() {
		String[] regexes = {"(\\d{1,3})h", "(\\d{1,3})m", "(\\d{1,3})s"};
		
		String temp = source;
		
		for(int i = 0; i < regexes.length; i++) {
			temp = temp.replaceFirst(regexes[i], "");
		}
		
		if(temp.length() > 0) {
			return false;
		}

		boolean isValid = false;
		for(int i = 0; i < regexes.length; i++) {
			Matcher m = Pattern.compile(regexes[i], Pattern.CASE_INSENSITIVE).matcher(source);
			if(m.find()) {
				isValid = true;
				unitValues[i] = Integer.parseInt(m.group(1));
			}
		}
		
		return isValid;
	}
	
	private boolean isRangeValid() {
		for(int i = 0; i < unitValues.length; i++) {
			int unitValue = unitValues[i];
			int[] range = RANGE_OF_UNIT_VALUES[i];
			if(unitValue < range[0] || unitValue > range[1]) {
				return false;
			}
		}
		
		return true;
	}
}

