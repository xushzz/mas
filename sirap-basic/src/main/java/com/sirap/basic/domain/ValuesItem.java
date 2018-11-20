package com.sirap.basic.domain;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.json.JsonUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class ValuesItem extends MexItem {
	
	protected List<Object> values = Lists.newArrayList();
	
	@SafeVarargs
	public static <E> ValuesItem of(E... elements) {
		ValuesItem vi = new ValuesItem();
		for(E obj : elements) {
			vi.values.add(obj);
		}
		
		return vi;
	}
	
	public static <E> ValuesItem of(Iterable<? extends E> elements) {
		ValuesItem vi = new ValuesItem();
		Collections.addAll(vi.values, Lists.newArrayList(elements));
		
		return vi;
	}
	
	public static <E> ValuesItem of(Iterator<? extends E> elements) {
		ValuesItem vi = new ValuesItem();
		Collections.addAll(vi.values, Lists.newArrayList(elements));
		
		return vi;
	}
	
	public ValuesItem addAll(List list) {
		values.addAll(list);
		return this;
	}
	
	public int size() {
		return values.size();
	}
	
	public boolean inRange(int index) {
		return index >=0 && index < values.size();
	}
	
	public boolean isEmpty() {
		return values.isEmpty();
	}
	
	public boolean isNotEmpty() {
		return !values.isEmpty();
	}
	
	public List<Object> getValues() {
		return values;
	}

	public Object getByIndex(int index) {
		return values.get(index);
	}

	public String stringAt(int index) {
		return String.valueOf(values.get(index));
	}
	
	public void add(Object value) {
		values.add(value);
	}
	
	public String toPrint() {
		String options = "";
		return toPrint(options);
	}
	
	@Override
	public List toList(String options) {
		return values;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		for(Object item : values) {
			if(StrUtil.contains(item + "", keyWord)) {
				return true;
			}
			
			if(isRegexMatched(item + "", keyWord)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toJson() {
		return JsonUtil.toJson(values);
	}
	
	@Override
	public String toPrettyJson(int depth) {
		return JsonUtil.toPrettyJson(values, depth);
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
		String conn = OptionUtil.readString(options, "c", ", ");
		if(StrUtil.equals(Konstants.NEWLINE_SHORT, conn)) {
			conn = Konstants.NEWLINE;
		}
		String result = "";
		if(showOrder(options)) {
			result = getPseudoOrder() + ") ";
		}
		result += StrUtil.connect(values, conn);

		return result;
	}
	
	@Override
	public String toString() {
		return toPrint();
	}
}