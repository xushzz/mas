package com.sirap.common.domain;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class InputRecord extends MexItem implements Comparable<InputRecord> {
	
	private String datetime;
	private String input;
	
	public InputRecord() {
		
	}
	
	public InputRecord(String datetime, String input) {
		this.datetime = datetime;
		this.input = input;
	}
	
	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public boolean isMatched(String keyWord) {
		String month = DateUtil.parseMonthIndex(keyWord);
		if(month != null) {
			String criteria = "-" + month + "-";
			if(StrUtil.contains(datetime, criteria, 3)) {
				return true;
			}
		}
		
		if(StrUtil.contains(datetime, keyWord, 3)) {
			return true;
		}
		
		if(isRegexMatched(input, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(input, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean parse(String source) {
		String[] info = source.split("\t");
		if(info.length != 2) {
			return false;
		}

		setDatetime(info[0]);
		setInput(info[1]);
		
		return true;
	}
	
	public void print() {
		C.pl(this);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(datetime);
		sb.append("\t").append(input);
		
		return sb.toString();
	}
	
	@Override
	public List<String> toPDF() {
		List<String> list = new ArrayList<String>();
		list.add(datetime);
		list.add(input);
		
		return list;
	}

	@Override
	public int compareTo(InputRecord a) {
		int value = datetime.compareTo(a.datetime);
		
		return value;
	}

}
