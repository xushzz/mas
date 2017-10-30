package com.sirap.common.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class TZRecord extends MexItem {
	
	private String id;
	private Integer diff;
	private Date datetime;
	private String dateFormat = DateUtil.HOUR_Min_AM_WEEK_DATE;

	public TZRecord(String id) {
		this.id = id;
	}

	public TZRecord(String id, Integer diff) {
		this.id = id;
		this.diff = diff;
	}

	public TZRecord(String id, Integer diff, Date datetime) {
		this.id = id;
		this.diff = diff;
		this.datetime = datetime;
	}
	
	public String getId() {
		return id;
	}

	public void setDiff(Integer diff) {
		this.diff = diff;
	}
	
	public Integer getDiff() {
		return diff;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public boolean isMatched(String keyWord) {
		
		if(diff != null) {
			String[] targetArr = {StrUtil.signValue(diff), diff + ""};
			 if(StrUtil.existsIgnoreCase(targetArr, keyWord)) {
					return true;
			 }
		}
		
		if(isRegexMatched(id, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(id, keyWord)) {
			return true;
		}
		
		String temp = keyWord.replace(" ", "_");
		
		if(StrUtil.contains(id, temp, 3)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toPrint() {
		return toPrint(Locale.US, 28);
	}
	
	public String toPrint(Locale locale, int maxLen) {
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.padRight(id, maxLen + 2));
		if(diff != null) {
			String signDiff = StrUtil.signValue(diff);
			sb.append(StrUtil.padRight(signDiff, 4));
		}
		if(datetime != null) {
			sb.append(DateUtil.displayDate(datetime, dateFormat, locale));
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		if(diff != null) {
			sb.append(" ").append(diff);
		}
		if(datetime != null) {
			sb.append(" ").append(datetime);
		}
		
		return sb.toString();
	}
	
	@Override
	public List<String> toPDF() {
		return toPDF(Locale.US, true);
	}

	public List<String> toPDF(Locale locale, boolean displayTime) {
		List<String> list = new ArrayList<String>();
		list.add(id);
		list.add(StrUtil.signValue(diff));
		if(datetime != null) {
			list.add(DateUtil.displayDate(datetime, dateFormat));
		}
		
		return list;
	}
}
