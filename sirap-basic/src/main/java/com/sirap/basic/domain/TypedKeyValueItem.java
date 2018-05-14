package com.sirap.basic.domain;

import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class TypedKeyValueItem extends KeyValueItem {
	
	private String type = "";
	
	public TypedKeyValueItem(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> valueItemInLines(String satos) {
		List<String> items = StrUtil.split(value.toString(), satos);
		
		return items;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		boolean isGood = super.isMatched(keyWord);
		if(isGood) {
			return isGood;
		}
		
		String checkValue = StrUtil.parseParam("#(.+)", keyWord);
		if(checkValue != null) {
			if(isRegexMatched(type, checkValue)) {
				return true;
			}
			
			if(StrUtil.contains(type, checkValue)) {
				return true;
			}
		}

		return false;
	}
	
	public String toPrint(String options) {
		StringBuffer sb = sb();
		if(!EmptyUtil.isNullOrEmpty(type) && OptionUtil.readBooleanPRI(options, "t", true)) {
			sb.append("[" + type + "] ");
		}
		if(OptionUtil.readBooleanPRI(options, "k", true)) {
			sb.append(key).append(" ");
		}
		sb.append(OptionUtil.readString(options, "c", "="));
		if(OptionUtil.readBooleanPRI(options, "v", true)) {
			sb.append(" ");
			if(!(value instanceof String)) {
				sb.append(value.getClass().getName() + "#");
			}
			sb.append(value);
		}
		
		return sb.toString();
	}

	public String toString() {
		String options = "";
		StringBuffer sb = sb();
		if(!EmptyUtil.isNullOrEmpty(type) && OptionUtil.readBooleanPRI(options, "t", true)) {
			sb.append("[" + type + "] ");
		}
		if(OptionUtil.readBooleanPRI(options, "k", true)) {
			sb.append(key).append(" ");
		}
		sb.append(OptionUtil.readString(options, "c", "="));
		if(OptionUtil.readBooleanPRI(options, "v", true)) {
			sb.append(" ");
			if(!(value instanceof String)) {
				sb.append(value.getClass().getName() + "#");
			}
			sb.append(value);
		}
		
		return sb.toString();
	}
	
	@Override
	public int compareTo(KeyValueItem ano) {
		TypedKeyValueItem another = (TypedKeyValueItem)ano;
		int va = type.toLowerCase().compareTo(another.type.toLowerCase());
		if(va == 0) {
			va = key.toLowerCase().compareTo(another.key.toLowerCase());
		}
		
		return va;
	}
}