package com.sirap.basic.domain;

import java.util.Date;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class HiredInfo extends MexItem {
	
	private String name;
	private Date dateStart;
	private Date dateEnd;
	private int days;
	private String orignalString;
	
	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}
	
	public String getOrignalString() {
		return orignalString;
	}

	private static final String DATE_REGEX = "(|\\d{4})\\.(|\\d{1,2})\\.(|\\d{1,2})";
	private static final String RANGE_DATE_REGEX = DATE_REGEX + "\\s*-\\s*" + DATE_REGEX;
	
	/**
	 * SIRAP CORP, 2004.10.24-2005.4.29
	 * 1) split by at least one comma
	 * 2) ends with expression like 2012.2.3-2016.6.9
	 * @param record
	 * @return
	 */
	public boolean parse(String record) {
		if(record == null) {
			return false;
		}
		
		String[] params = StrUtil.parseParams("(.+),([^,]+)", record);
		if(params == null) {
			return false;
		}
		name = params[0];

		String[] dateMetadata = StrUtil.parseParams(RANGE_DATE_REGEX, params[1]);
		if(dateMetadata == null) {
			return false;
		}

		dateStart = DateUtil.construct(dateMetadata[0], dateMetadata[1], dateMetadata[2]);
		dateEnd = DateUtil.construct(dateMetadata[3], dateMetadata[4], dateMetadata[5]);
		orignalString = record;
		
		return true;
	}
	
	public String toPrint() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getPseudoOrder() + ") ").append(name).append("\t");
		
		String d1Str = DateUtil.displayDate(dateStart, DateUtil.DATE_ONLY_COMMA);
		String d2Str = DateUtil.displayDate(dateEnd, DateUtil.DATE_ONLY_COMMA);
		buffer.append(d1Str).append(" - ").append(d2Str).append(" = ").append(days);
		
		return buffer.toString();
	}

	@Override
	public String toString() {
		return "HiredInfo [name=" + name + ", dateStart="
				+ dateStart + ", dateEnd=" + dateEnd + ", days=" + days
				+ ", orignalString=" + orignalString + "]";
	}
}