package com.sirap.basic.domain;

import java.util.List;

import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class TypedKeyValueItem extends KeyValueItem {
	
	private String type;
	
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

	public String toPrint(String options) {
		StringBuffer sb = sb();
		if(OptionUtil.readBooleanPRI(options, "t", true)) {
			sb.append("[" + type + "] ");
		}
		if(OptionUtil.readBooleanPRI(options, "k", true)) {
			sb.append(key).append(" ");
		}
		sb.append(OptionUtil.readString(options, "conn", "="));
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
		String classInfo = "";
		if(!(value instanceof String)) {
			classInfo = value.getClass().getName() + "#";
		}
		String msg = StrUtil.occupy("{0}={2}{1}", key, value, classInfo);
		return msg;
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