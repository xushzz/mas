package com.sirap.basic.component;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MexedOption extends MexItem {

	private String name;
	private Object value;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/****
	 * +printSource
	 * +UseParallelGC
	 * ParallelGCThreads=20
	 * motto=Nothing is free
	 */
	@Override
	public boolean parse(String source) {
		String regex = "([+\\-])(\\w+)";
		String[] params = StrUtil.parseParams(regex, source);
		
		if(params != null) {
			value = StrUtil.equals("+", params[0]);
			name = params[1];

			return true;
		}
		
		regex = "(\\w+)\\s*=\\s*(.+)";
		params = StrUtil.parseParams(regex, source);
		if(params != null) {
			name = params[0];
			value = params[1];

			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "MexedOption [name=" + name + ", value=" + value + "]";
	}
}
