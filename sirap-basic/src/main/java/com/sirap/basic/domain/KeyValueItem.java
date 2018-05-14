package com.sirap.basic.domain;

import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class KeyValueItem extends MexItem implements Comparable<KeyValueItem> {

	protected String key;
	protected Object value;

	public KeyValueItem() {

	}
	
	public KeyValueItem(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getValueX() {
		if(value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
	
	public String getKey() {
		return key;
	}

	public boolean isMatched(String keyWord) {
		if(isRegexMatched(key, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(key, keyWord)) {
			return true;
		}
		
		String checkValue = StrUtil.parseParam(":(.+)", keyWord);
		if(checkValue != null) {
			if(isRegexMatched(value.toString(), checkValue)) {
				return true;
			}
			
			if(StrUtil.contains(value.toString(), checkValue)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String toPrint() {
		String options = "";
		return toPrint(options);
	}
	
	public String toPrint(String options) {
		if(OptionUtil.readBooleanPRI(options, "ko", false)) {
			return key;
		}
		String connector = OptionUtil.readString(options, "c", "=");
		String classInfo = "";
		if(!(value instanceof String)) {
			classInfo = value.getClass().getName() + "#";
		}
		String msg = StrUtil.occupy("{0} {3} {2}{1}", key, value, classInfo, connector);
		return msg;
	}

	public String toString() {
		String classInfo = "";
		if(!(value instanceof String)) {
			classInfo = value.getClass().getName() + "#";
		}
		String msg = StrUtil.occupy("{0}={2}{1}", key, value, classInfo);
		return msg;
	}
	
	@Override
	public int compareTo(KeyValueItem another) {
		return key.toLowerCase().compareTo(another.key.toLowerCase());
	}
}