package com.sirap.common.command.explicit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MadeException;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.math.FormulaCalculator;
import com.sirap.basic.math.MexColorConverter;
import com.sirap.basic.math.MexNumberConverter;
import com.sirap.basic.math.SimCal;
import com.sirap.basic.math.Sudoku;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;

public class CommandMath extends CommandBase {

	private static final String LENGTH_OF = "l\\.";
	private static final String KEY_MAXMIN = "max,min";
	private static final String KEY_PERMUTATION = "p=";
	private static final String REGEX_HEX8 = "0x([0-9a-f]{1,16})";
	
	@Override
	public boolean handle() {
		params = parseParams("split\\s+(\\d{1,4})\\s+([\\d\\s,]+)");
		if(params != null) {
			int amount = Integer.parseInt(params[0]);
			List<String> items = StrUtil.split(params[1]);
			List<Integer> units = Lists.newArrayList();
			for(String item : items) {
				units.add(Integer.parseInt(item));
			}
			
			export(MathUtil.fibonacciCoins(amount, units));
			return true;
		}
		
		solo = parseParam("(\\d{1,2})!");
		if(solo != null) {
			short number = Short.valueOf(solo);
			BigDecimal result = MathUtil.factorialOf(number);
			
			String temp = result.toString();
			if(temp.length() > 5) {
				temp += " (" + temp.length() + " digits)";
			}
			List<String> items = Lists.newArrayList();
			if(number > 1) {
				items.add(command + " = " + MathUtil.factorialExpressionOf(number));
			}
			items.add(command + " = " + temp);
			
			export(items);
			
			return true;
		}
		
		solo = parseParam(REGEX_HEX8);
		if(solo != null) {
			export(Long.parseLong(solo, 16));
			
			return true;
		}

		regex = "([\\s_\\d\\.+\\-x\\*/\\(\\)]{2,})";
		params = parseParams("(cal\\s+|)" + regex);
		if(params != null) {
			boolean ignoreIfException = params[0].isEmpty();
			String expression = params[1];
			int scale = OptionUtil.readIntegerPRI(options, "s", 2);
			try {
				String math = SimCal.evaluate(expression, scale);
				if(math != null) {
					String temp = math;
					boolean keepZero = OptionUtil.readBooleanPRI(options, "k", false);
					if(!keepZero) {
						temp = StrUtil.removePointZeroes(temp);
					}
					List<String> results = Lists.newArrayList();
					results.add(expression + " = " + temp);
					if(temp.length() > 5) {
						results.add("Result contains " + temp.length() + " chars.");
					}
					export(results);
					return true;
				}
			} catch (Exception ex) {
				if(ignoreIfException) {
					//
				} else {
					throw new MexException(ex);
				}
			}
		}

		String math = FormulaCalculator.evaluate(command);
		if(math != null) {
			List<String> results = new ArrayList<String>();
			results.add("x=" + math);
			export(results);
			return true;
		}
		
		List<String[]> solutions = Sudoku.evaluate(command);
		if(!EmptyUtil.isNullOrEmpty(solutions)) {
			boolean isMultiple = solutions.size() > 1;
			List<String> results = new ArrayList<String>();
			for(int i = 0; i < solutions.size(); i++) {
				if(isMultiple) {
					results.add("Solution " + (i+1) + ">");
				}
				String[] matrix = solutions.get(i);
				
				for(int m = 0; m < matrix.length; m++) {
					results.add(matrix[m].replace("", " "));
				}
			}
			export(results);
			
			return true;
		}
		
		params = parseParams("(h|hex|d|dec|o|oct|b|bin|)=([\\-a-f|\\d|,|\\s]+)");
		if(params != null) {
			MexNumberConverter salim = new MexNumberConverter(params[0], params[1]);
			List<String> results = salim.getResult();
			export(results);
			return true;
		}
		
		solo = parseParam("#([a-f|\\d|,|\\s]+)");
		if(solo != null) {
			MexColorConverter salim = new MexColorConverter(solo);
			List<String> results = salim.getResult();
			if(!EmptyUtil.isNullOrEmpty(results)) {
				export(results);
				return true;
			}
		}
		
		params = parseParams(KEY_PERMUTATION + "(.+?)\\s*,\\s*(\\d+)");
		if(params != null) {
			String p0 = params[0].trim();
			String p1 = params[1].trim();
			Integer targetSize = MathUtil.toInteger(p1);
			if(targetSize != null) {
				Integer numberOfSamples = MathUtil.toInteger(p0);
				if(numberOfSamples != null) {
					String result = MathUtil.permutationWithLimit(numberOfSamples, targetSize, 99);
					if(result != null) {
						C.pl2("Permutation(" + numberOfSamples + "," + targetSize + ")=" + result);
					}
				}
				
				String source = p0;
				List<String> records = MathUtil.permutation(source, targetSize);
				if(!EmptyUtil.isNullOrEmpty(records)) {
					records.add(C.getTotal(records.size()));
					setIsPrintTotal(false);
					export(records);
				}
				
				return true;
			}
		}
		
		String regexDateStr = "(|\\d{4})\\.(|\\d{1,2})\\.(|\\d{1,2})";
		params = parseParams(regexDateStr + "\\s*-\\s*" + regexDateStr);
		if(params != null) {
			Date d1 = DateUtil.construct(params[0], params[1], params[2]);
			Date d2 = DateUtil.construct(params[3], params[4], params[5]);
			int dayDiff = DateUtil.dayDiff(d1, d2);
			String d1Str = DateUtil.displayDateCompact(d1);
			String d2Str = DateUtil.displayDateCompact(d2);
			export(d1Str + " - " + d2Str + " = " + dayDiff);
			
			return true;
		}
		
		params = parseParams(regexDateStr + "\\s*([+-])\\s*(\\d{1,5})");
		if(params != null) {
			Date d1 = DateUtil.construct(params[0], params[1], params[2]);
			String operator = params[3];
			int dayDiffAbs = MathUtil.toInteger(params[4]);
			int dayDiff = dayDiffAbs;
			if(StrUtil.equals(operator, "-")) {
				dayDiff *= -1;
			}
			
			Date d2 = DateUtil.add(d1, Calendar.DAY_OF_YEAR, dayDiff);

			String d1Str = DateUtil.displayDateCompact(d1);
			String d2Str = DateUtil.displayDateCompact(d2);
			export(d1Str + " " + operator + " " + dayDiffAbs + " = " + d2Str);
			
			
			return true;
		}
		
		String mapkeys = StrUtil.regexOfKeys(MathUtil.WEIGHT_UNITS);
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(" + mapkeys + ")");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.weight(value, params[1], OptionUtil.readIntegerPRI(options, "s", 2)));
			
			return true;
		}
		
		params = parseParams(Konstants.REGEX_SIGN_FLOAT + "\\s*(Fa|Ce)");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.temperature(value, params[1], OptionUtil.readIntegerPRI(options, "s", 2)));
			
			return true;
		}
		
		mapkeys = StrUtil.regexOfKeys(MathUtil.SHORT_DISTANCE_UNITS);
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(" + mapkeys + ")");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.shortDistance(value, params[1], OptionUtil.readIntegerPRI(options, "s", 6)));
			
			return true;
		}
		
		mapkeys = StrUtil.regexOfKeys(MathUtil.TIME_UNITS);
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(" + mapkeys + ")");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			String unit = params[1];
			List<String> items = Lists.newArrayList();
			try {
				items.add(MathUtil.dhmsStrOfTime(value, unit));
				items.addAll(MathUtil.timeDuration(value, unit, OptionUtil.readIntegerPRI(options, "s", 3)));
				export(items);
			} catch (MadeException ex) {
				export(StrUtil.occupyKeyValues(ex.getMessage(), "origin", command));
			}
			
			return true;
		}
		
		mapkeys = StrUtil.regexOfKeys(MathUtil.LONG_DISTANCE_UNITS);
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*(" + mapkeys + ")");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.longDistance(value, params[1], OptionUtil.readIntegerPRI(options, "s", 6)));
			
			return true;
		}
		
		params = parseParams(Konstants.REGEX_FLOAT + "\\s*([KMGTPE]?B)");
		if(params != null) {
			Double value = Double.parseDouble(params[0]);
			export(MathUtil.fileSize(value, params[1], OptionUtil.readIntegerPRI(options, "s", 6)));
			
			return true;
		}
		
		solo = parseParam(LENGTH_OF + "(.+?)");
		if(solo != null) {
			int len = solo.length();
			String value = "len = " + len;
			export(value);
			
			return true;
		}
		
		if(isIn(KEY_MAXMIN)) {
			List<String> items = new ArrayList<>();
			items.add(maxmin("Max of Long", Long.MAX_VALUE));
			items.add(maxmin("Min of Long", Long.MIN_VALUE));
			items.add(maxmin("Max of Integer", Integer.MAX_VALUE));
			items.add(maxmin("Min of Integer", Integer.MIN_VALUE));
			items.add(maxmin("Max of Short", Short.MAX_VALUE));
			items.add(maxmin("Min of Short", Short.MIN_VALUE));
			items.add(maxmin("Max of Byte", Byte.MAX_VALUE));
			items.add(maxmin("Min of Byte", Byte.MIN_VALUE));
			
			export(items);
			
			return true;
		}
				
		return false;
	}
	
	private String maxmin(String displayName, long value) {
		String str = String.valueOf(value);
		int len = str.length();
		if(value < 0) {
			len--;
		}
		
		String temp = "{0} is {1}, {2} chars.";
		String result = StrUtil.occupy(temp, displayName, str, len);
		
		return result;
	}
}