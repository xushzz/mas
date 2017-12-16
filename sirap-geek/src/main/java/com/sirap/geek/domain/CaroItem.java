package com.sirap.geek.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class CaroItem extends MexItem implements Comparable<CaroItem> {
	
	private String fileName;
	private String dateStr;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	@Override
	public boolean parse(String record) {
		String regex = "(\\d{8}_\\d{6})";
		String temp = StrUtil.findFirstMatchedItem(regex, record);
		if(temp == null) {
			return false;
		}
		
		fileName = record;
		dateStr = temp;
		
		return true;
	}
	
	@Override
	public String toString() {
		return fileName + " " + dateStr;
	}

	@Override
	public int compareTo(CaroItem caro) {
		return dateStr.compareTo(caro.dateStr);
	}
}