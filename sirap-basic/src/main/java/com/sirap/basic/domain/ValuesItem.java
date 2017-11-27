package com.sirap.basic.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class ValuesItem extends MexItem {
	
	protected List<Object> values = Lists.newArrayList();

	public ValuesItem() {

	}
	
	public boolean inRange(int index) {
		return index >=0 && index < values.size();
	}
	
	public Object getByIndex(int index) {
		return values.get(index);
	}
	
	public ValuesItem(Object value) {
		values.add(value);
	}
	
	public void add(Object value) {
		values.add(value);
	}
	
	public String toPrint() {
		String options = "";
		return toPrint(options);
	}
	
	public boolean isMatched(String keyWord) {
		for(Object item : values) {
			if(StrUtil.contains(item + "", keyWord)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean parse(String record) {
		if(EmptyUtil.isNullOrEmptyOrBlank(record)) {
			return false;
		}
		List<String> items = StrUtil.split(record, "|");
		values.clear();
		values.addAll(items);
		
		return true;
	}
	
	public String toPrint(String options) {
		String conn = OptionUtil.readString(options, "conn", ", ");
		String result = StrUtil.connect(values, conn);

		return result;
	}
	
	@Override
	public String toString() {
		return toPrint();
	}
}