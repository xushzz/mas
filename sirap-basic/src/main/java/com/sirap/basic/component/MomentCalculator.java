package com.sirap.basic.component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MomentCalculator {
	
	private static final int[][] RANGE_OF_HMS = {{0, 23}, {0, 59}, {0, 59}};
	private static final String REGEX_HMS_VALUE = "\\d{1,9}";

	private Calendar theMoment;
	private int[] hms;	
	private int[] hmsRepeat;	
	
	private static MomentCalculator instance;
	public static MomentCalculator g(String timeInfo) {
		if(instance == null) {
			instance = new MomentCalculator(timeInfo);
		}
		
		return instance;
	}
	
	public boolean isRepeatedAlarm() {
		return hmsRepeat != null;
	}
	
	public boolean isValidAlarm() {
		return theMoment != null;
	}
	
	public Date theDate() {
		return theMoment.getTime();
	}

	public long howLongHaveToWait() {
		long duration = theMoment.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		return duration;
	}

	public long getRepeatIntervalMilliSeconds() {
		if(hmsRepeat == null) {
			return -1;
		}
		
		long value = hmsRepeat[0] * Konstants.MILLI_PER_HOUR;
		value += hmsRepeat[1] * Konstants.MILLI_PER_MINUTE;
		value += hmsRepeat[2] * Konstants.MILLI_PER_SECOND;
		
		return value;
	}

	public MomentCalculator() {}
	
	public MomentCalculator(String timeInfo) {
		process(timeInfo);
	}
	
	/***
	 * al+10s 		delay ten seconds
	 * al#10s		right away, repeat every ten seconds
	 * al+#10s  	delay ten seconds, repeat every ten seconds
	 * al+9s#10s  	delay nine seconds, repeat very ten seconds
	 * al@10s 		fixed at 0:0:10s AM
	 * al@    		fixed at next sharp hour
	 * al@@   		fixed at next sharp hour, repeat
	 * @param type
	 * @param timeInfo
	 * @param actionInfo
	 */
	public void process(String timeInfo) {
		String regexTime = "([\\dhms:]+)";
		String delayOrFixedOnly = "(\\+|@)" + regexTime;
		String repeatNow = "#" + regexTime;
		String delayOrFixedSameRepeat = "(\\+|@)#" + regexTime;
		String delayOrFixedAndRepeat = delayOrFixedOnly + repeatNow;

		if(StrUtil.equals("@", timeInfo)) {
			theMoment = DateUtil.nextSharpHourCalendar(1);
			return;
		}
		
		if(StrUtil.equals("@@", timeInfo)) {
			theMoment = DateUtil.nextSharpHourCalendar(1);
			hmsRepeat = new int[] {1, 0, 0};
			return;
		}
		
		String solo = StrUtil.parseParam(repeatNow, timeInfo);
		if(solo != null) {
			theMoment = Calendar.getInstance();
			hmsRepeat = readHMS(solo, false);
			return;
		}

		String[] params = StrUtil.parseParams(delayOrFixedOnly, timeInfo);
		if(params != null) {
			boolean checkRange = isFixed(params[0]);
			hms = readHMS(params[1], checkRange);
			calculate(hms, checkRange);
			return;
		}
		
		params = StrUtil.parseParams(delayOrFixedSameRepeat, timeInfo);
		if(params != null) {
			boolean checkRange = isFixed(params[0]);
			hms = readHMS(params[1], checkRange);
			calculate(hms, checkRange);
			hmsRepeat = hms;
			return;
		}

		params = StrUtil.parseParams(delayOrFixedAndRepeat, timeInfo);
		if(params != null) {
			boolean checkRange = isFixed(params[0]);
			hms = readHMS(params[1], checkRange);
			calculate(hms, checkRange);
			hmsRepeat = readHMS(params[2], false);
			return;
		}
	}
	
	private boolean isFixed(String type) {
		return StrUtil.equals(type, "@");
	}
	
	private int[] readHMS(String timeOnly, boolean rangeSensitive) {
		if(StrUtil.isRegexFound("[hms]", timeOnly)) {
			int[] byUnit = parseHmsByUnit(timeOnly);
			if(byUnit != null) {
				if(!rangeSensitive || checkRange(byUnit)) {
					return byUnit;
				}
			}
		} else {
			int[] byColon = parseHmsByColon(timeOnly);
			if(byColon != null) {
				if(!rangeSensitive || checkRange(byColon)) {
					return byColon;
				}
			}
		}

		XXXUtil.alert("Invalid time info: ", timeOnly);
		return null;
	}
	
	private void calculate(int[] hms, boolean fixedStyle) {
		theMoment = Calendar.getInstance();
		if(fixedStyle) {
			theMoment.set(Calendar.HOUR_OF_DAY, hms[0]);
			theMoment.set(Calendar.MINUTE, hms[1]);
			theMoment.set(Calendar.SECOND, hms[2]);
			
			if(theMoment.before(Calendar.getInstance())) {
				theMoment.add(Calendar.HOUR_OF_DAY, 24);
			}
		} else {
			theMoment.add(Calendar.HOUR_OF_DAY, hms[0]);
			theMoment.add(Calendar.MINUTE, hms[1]);
			theMoment.add(Calendar.SECOND, hms[2]);
		}
	}

	public void recalculateTargetMoment() {
		XXXUtil.nullCheck(hmsRepeat, "hmsRepeat");
		theMoment.add(Calendar.HOUR_OF_DAY, hmsRepeat[0]);
		theMoment.add(Calendar.MINUTE, hmsRepeat[1]);
		theMoment.add(Calendar.SECOND, hmsRepeat[2]);
	}

	private boolean checkRange(int[] values) {
		XXXUtil.shouldBeEqual(values.length, RANGE_OF_HMS.length);
		for(int i = 0; i < values.length; i++) {
			int unitValue = values[i];
			int[] range = RANGE_OF_HMS[i];
			XXXUtil.checkRange(unitValue, range[0], range[1]);
		}
		
		return true;
	}
	
	//5:05,5:04:07
	private int[] parseHmsByColon(String source) {
		String msg = StrUtil.occupy("Invalid time: {0}, should be like {1}.", source, "13:34:54");

		int[] values = new int[3];
		List<String> items = StrUtil.split(source, ":");
		if(items.size() > 3) {
			XXXUtil.alert(msg);
		}

		boolean allEmpty = true;
		for(int k = 0; k < items.size(); k++) {
			String va = items.get(k);
			if(va.isEmpty()) {
				continue;
			}
			allEmpty = false;
			if(StrUtil.isRegexMatched(REGEX_HMS_VALUE, va)) {
				values[k] = Integer.parseInt(va);
			} else {
				XXXUtil.alert(msg);
			}
		}
		
		if(allEmpty) {
			XXXUtil.alert(msg);
		}
		
		return values;
	}
	
	private int[] parseHmsByUnit(String source) {
		String msg = StrUtil.occupy("Invalid time: {0}, should be like {1}.", source, "13h34m54s");
		
		String holder = "&";
		List<String> units = StrUtil.split("h,m,s");
		int[] values = new int[3];
		
		String temp = source;
		if(StrUtil.isRegexMatched(REGEX_HMS_VALUE, source)) {
			values[0] = Integer.parseInt(source);
			return values;
		}
		
		for(int k = 0; k < units.size(); k++) {
			String va = units.get(k);
			String regex = "(" + REGEX_HMS_VALUE +")" + va;
			Matcher ma = StrUtil.createMatcher(regex, temp);
			if(ma.find()) {
				String whole = ma.group(0);
				String number = ma.group(1);
				values[k] = Integer.parseInt(number);
				temp = temp.replaceFirst(whole, holder);
			}
		}
		
		if(!StrUtil.isRegexMatched(holder + "+", temp)) {
			XXXUtil.alert(msg);
		}

		return values;
	}
}
