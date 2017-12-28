package com.sirap.geek.domain;

import java.util.Date;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class CaroItem extends MexItem implements Comparable<CaroItem> {
	
	private String fileName;
	private Date dateInfo;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Date getDateInfo() {
		return dateInfo;
	}

	public void setDateInfo(Date dateInfo) {
		this.dateInfo = dateInfo;
	}

	@Override
	public boolean parse(String record) {
		fileName = record;
		String temp = StrUtil.findFirstMatchedItem("([12][09]\\d{6}_\\d{6})", record);
		if(temp != null) {
			dateInfo = DateUtil.parse("yyyyMMdd_HHmmss", temp);
			return true;
		}
		
		temp = StrUtil.findFirstMatchedItem("(\\d+)", record);
		if(temp != null) {
			String ts = null;
			if(temp.length() == 13) {
				ts = temp;
			} else if (temp.length() == 10) {
				ts = temp + "000";
			}
			if(ts != null) {
				Long milliSecondsSince1970 = Long.parseLong(ts);
				dateInfo = new Date(milliSecondsSince1970);
				return true;
			}
		}
		
		temp = StrUtil.findFirstMatchedItem("(20\\d{6})", record);
		if(temp != null) {
			dateInfo = DateUtil.parse("yyyyMMdd", temp);
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return fileName + " " + DateUtil.displayDate(dateInfo, "yyyyMMdd_HHmmss");
	}

	@Override
	public int compareTo(CaroItem caro) {
		return dateInfo.compareTo(caro.dateInfo);
	}
}