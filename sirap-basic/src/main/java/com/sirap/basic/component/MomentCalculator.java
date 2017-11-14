package com.sirap.basic.component;

import java.util.Calendar;
import java.util.Date;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class MomentCalculator {
	
	private static final int[][] RANGE_OF_HMS = {{0,23},{0,59},{0,59}};	

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
			int[] byUnit = DateUtil.parseHmsByUnit(timeOnly);
			if(byUnit != null) {
				if(!rangeSensitive || checkRange(byUnit)) {
					return byUnit;
				}
			}
		} else {
			int[] byColon = DateUtil.parseHmsByColon(timeOnly);
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
}
