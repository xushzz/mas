package com.sirap.basic.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.map.AlinkMap;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class KeyValuesItem extends MexItem {
	
	protected AlinkMap<String, Object> box = new AlinkMap<String, Object>();
	
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
	
	@Override
	public String toJson() {
		return JsonUtil.toJson(box);
	}
	
	@Override
	public String toPrettyJson(int depth) {
		return JsonUtil.toPrettyJson(box, depth);
	}
	
	public String toPrint() {
		String options = "";
		return toPrint(options);
	}
	
	@Override
	public List toList(String options) {
		String temp = OptionUtil.readString(options, "keys", "");
		if(temp.isEmpty()) {
			return Lists.newArrayList(box.values());
		}

		List<String> keys = StrUtil.split(temp, '|');
		List list = Lists.newArrayList();
		for(String key : keys) {
			list.add(box.get(key));
		}
		
		return list;
	}
	
	public String toPrint(String options) {
		String connector = OptionUtil.readString(options, "c", ", ");

		return StrUtil.connect(toList(), connector);
	}

	public String toString() {
		return toPrint("");
	}
}