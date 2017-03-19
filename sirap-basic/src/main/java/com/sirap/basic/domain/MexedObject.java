package com.sirap.basic.domain;

import java.util.Date;
import java.util.List;

import com.sirap.basic.util.StrUtil;

@SuppressWarnings({"serial","rawtypes"})
public class MexedObject extends MexItem {
	
	protected Object obj;
	
	public MexedObject() {
		
	}
	
	public MexedObject(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	public String getString() {
		if(obj == null) {
			return null;
		}
		
		String source = obj.toString();
		
		if(obj instanceof List) {
			List items = (List)obj;
			source = StrUtil.connect(items, ", ");
		}
		
		return source;
	}
	
	public Date getDate() {
		if(obj == null || !(obj instanceof Date)) {
			return null;
		}
		
		Date date = (Date)obj;
		
		return date;
	}
	
	@Override
	public boolean parse(String record) {
		this.obj = record;
		return true;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		String source = getString();
		
		if(isRegexMatched(source, keyWord)) {
			return true;
		}
		
		boolean flag = StrUtil.contains(source, keyWord); 
		return flag; 
	}
	
	@Override
	public String toString() {
		return getString();
	}
}
