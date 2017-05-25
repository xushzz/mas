package com.sirap.basic.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.sirap.basic.tool.C;
import com.sirap.basic.tool.D;
import com.sirap.geek.domain.HiredInfo;
import com.sirap.geek.manager.HiredDaysCalculator;

public class DateUtilTest {
	public void parseHired() {
		String record = "1 facebook,2004.10.24-2005.4.29";
		HiredInfo hi = new HiredInfo();
		boolean flag = hi.parse(record);
		if(flag) {
			C.pl(hi);
		} else {
			D.sink();			
		}
	}
	
	@Test
	public void tooLong() {
		long va = 0;
		//va = DateUtil.convertDateStrToLong(null);
//		D.ts(va);
		
		va = DateUtil.convertDateStrToLong("20170224082504036");
//		D.ts(va);
	}
	public void tooDateStr() {
		long va = 1487779200000L;
		va = 0;
		String value = DateUtil.convertLongToDateStr(va, DateUtil.HOUR_Min_Sec_AM_WEEK_DATE);
		C.pl(value);
		
		String datetimeItems = "20170224082504036";
		va = DateUtil.convertDateStrToLong(datetimeItems);
		C.pl(va);
	}
	public void hired() {
		String path = "E:/KDB/tasks/0924_HireCalculator/HIRE.txt";
		List<String> records = IOUtil.readFileIntoList(path);
		HiredDaysCalculator cong = new HiredDaysCalculator(records);
		List<String> list = cong.orderByHiredDays(true);
		C.list(list);
	}
	
	public void f5txt() {
		C.pl(DateUtil.displayNow(DateUtil.DATETIME_F5TXT));
	}
	
	public void setLen() {
		String temp = DateUtil.DATETIME_ALL_TIGHT;
		String source = "20700701";
		int len = temp.length();
		int targetLength = 1;
		C.pl(StrUtil.truncate(source, targetLength));
		C.pl(StrUtil.extend(source, len, "0"));
		C.pl(StrUtil.extendLeftward(source, len, "0"));
	}
	public void crack() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));	
		Date date = DateUtil.calendarToDate(cal);
		C.pl(date);
		C.pl(DateUtil.convertDateStrToLong("20700701"));
		C.pl(DateUtil.convertDateStrToLong("197001010800"));
		date = new Date();
		C.pl(DateUtil.getTZRelatedDate(0, date));
		C.pl(date);
	}
}
