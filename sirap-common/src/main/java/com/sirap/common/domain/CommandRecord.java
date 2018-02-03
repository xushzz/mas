package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class CommandRecord extends MexItem implements Comparable<CommandRecord> {

	private String key;
	private String className;
	private String remark;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	@Override
	public boolean parse(String record) {
		String regex = "((\\d{1,3}),|)(.+?)(,(.*)|)";
		String[] arr = StrUtil.parseParams(regex, record);
		if(arr == null) {
			return false;
		}
		
		String tempOrder = arr[1];
		if(EmptyUtil.isNullOrEmpty(tempOrder)) {
			pseudoOrder = 17;
		} else {
			pseudoOrder = MathUtil.toInteger(tempOrder);
		}
		
		className = arr[2];
		remark = arr[4];
		
		return true;
	}

	@Override
	public boolean isMatched(String keyWord) {
		if(StrUtil.contains(key, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(key, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(className, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(className, keyWord)) {
			return true;
		}
		
		if(StrUtil.contains(remark, keyWord)) {
			return true;
		}
		
		if(isRegexMatched(remark, keyWord)) {
			return true;
		}
		
		return false;
	}

	@Override
	public int compareTo(CommandRecord item) {
		int order2 = item.getPseudoOrder();
		int value = getPseudoOrder() - order2;
		
		return value;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(key).append("=").append(getPseudoOrder()).append(", ").append(className);
		if(remark != null) {
			sb.append(", ").append(remark);
		}
		
		return sb.toString();
	}
}
