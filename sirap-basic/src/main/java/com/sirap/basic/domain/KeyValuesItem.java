package com.sirap.basic.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class KeyValuesItem extends MexItem {
	
	protected Map<String, Object> box = new LinkedHashMap<String, Object>();
	
	public KeyValuesItem() {

	}
	
	public KeyValuesItem(String key, Object value) {
		box.put(key, value);
	}
	
	public void add(String key, Object value) {
		box.put(key, value);
	}
	
	public boolean isMatched(String keyWord) {
		List<Object> values = Lists.newArrayList(box.values());
		for(Object item : values) {
			if(StrUtil.contains(item + "", keyWord)) {
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
		String keys = OptionUtil.readString(options, "keys", "");
		String connector = OptionUtil.readString(options, "conn", ", ");
		List<String> list;
		if(keys.isEmpty()) {
			list = Lists.newArrayList(box.keySet());
		} else {
			list = StrUtil.split(keys, '|');
		}
		StringBuffer sb = sb();
		boolean isTheFirstOne = true;
		for(String key : list) {
			if(!isTheFirstOne) {
				sb.append(connector);
			}
			sb.append(box.get(key));
			isTheFirstOne = false;
		}
		
		return sb.toString();
	}

	public String toString() {
		List<String> list = Lists.newArrayList(box.keySet());
		StringBuffer sb = sb();
		boolean isTheFirstOne = true;
		for(String key : list) {
			if(!isTheFirstOne) {
				sb.append(", ");
			}
			sb.append(box.get(key));
			isTheFirstOne = false;
		}
		
		return sb.toString();
	}
}