package com.sirap.common.domain;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MemoryRecord extends MexItem {

	//2015-03-30
	private Date date;
	private String dateStr;
	private String name;
	
	public MemoryRecord() {
		
	}
	
	public Date getDate() {
		return date;
	}

	@Override
	public boolean isMatched(String keyWord) {
		String month = DateUtil.parseMonthIndex(keyWord);
		if(month != null) {
			String criteria = "-" + month + "-";
			if(StrUtil.contains(dateStr, criteria, 3)) {
				return true;
			}
		}
		
		if(StrUtil.contains(dateStr, keyWord, 2)) {
			return true;
		}
		
		if(StrUtil.contains(name, keyWord)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean parse(String source) {
		//1989-05-29 
		String regex = "((\\d{4})-(\\d{1,2})-(\\d{1,2}))\\s+(.+?)";
		String[] params = StrUtil.parseParams(regex, source);
		if(params == null) {
			return false;
		}

		dateStr = params[0];
		date = DateUtil.construct(params[1], params[2], params[3]);
		name = params[4];

		return true;
	}
	
	@Override
	public List toList(String options) {
		return Lists.newArrayList(dateStr, name);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(dateStr);
		sb.append(" ").append(name);
		
		return sb.toString();
	}
}
