package com.sirap.common.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class LoginRecord extends MexItem {

	private String datetimeLogin;
	private String datetimeExit;
	private int mins;
	
	public LoginRecord() {
		
	}
	
	public LoginRecord(String datetime) {
		this.datetimeLogin = datetime;
	}
	
	@Override
	public List toList(String options) {
		String temp = datetimeExit != null ? datetimeExit : "";
		return Lists.newArrayList(datetimeLogin, mins, temp);
	}

	public String getDatetimeLogin() {
		return datetimeLogin;
	}

	public void setDatetimeLogin(String datetime) {
		this.datetimeLogin = datetime;
	}

	public String getDatetimeExit() {
		return datetimeExit;
	}

	public void setDatetimeExit(String datetimeExit) {
		this.datetimeExit = datetimeExit;
	}

	public int getMins() {
		return mins;
	}

	public void setMins(int mins) {
		this.mins = mins;
	}

	public void increase() {
		mins++;
	}

	@Override
	public boolean isMatched(String keyWord) {
		String month = DateUtil.parseMonthIndex(keyWord);
		if(month != null) {
			String criteria = "-" + month + "-";
			if(StrUtil.contains(datetimeLogin, criteria, 3)) {
				return true;
			}
		}
		
		if(StrUtil.contains(datetimeLogin, keyWord, 2)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean parse(String source) {
		String[] info = source.split("\t");
		if(info.length < 2) {
			return false;
		}

		String dateStr = info[0];
		setDatetimeLogin(dateStr);

		String minsStr = info[1];
		Integer mins = MathUtil.toInteger(minsStr);
		if(mins == null) {
			return false;
		}
		setMins(mins);
		
		if(info.length >= 3) {
			String dateExit = info[2];
			setDatetimeExit(dateExit);
		} else {
			Date login = DateUtil.parse(DateUtil.DATE_TIME, datetimeLogin, false);
			if(login != null) {
				Date exit = DateUtil.add(login, Calendar.MINUTE, mins);
				datetimeExit = DateUtil.displayDate(exit, DateUtil.DATE_TIME);
			}
		}
		
		return true;
	}
	
	@Override
	public String toPrint(String options) {
		String connector = OptionUtil.readString(options, "c", "  ");
		return StrUtil.connect(toList(), connector);
	}
	
	@Override
	public String toString() {
		return toPrint("");
	}
}
