package com.sirap.basic.domain;

import java.util.Date;
import java.util.List;

import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings({"serial","rawtypes"})
public class MexObject extends MexItem {
	
	protected Object obj;
	
	public MexObject() {
		
	}
	
	public MexObject(Object obj) {
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
	public boolean isMatched(String keyWord, boolean caseSensitive) {
		String source = getString();
		
		if(isRegexMatched(source, keyWord)) {
			return true;
		}
		
		boolean flag = StrUtil.contains(source, keyWord, caseSensitive); 
		return flag; 
	}
	
	@Override
	public String toPrint(String options) {
		StringBuilder sb = new StringBuilder();
		boolean showOrder = OptionUtil.readBoolean(options, "order", false);
		if(showOrder) {
			sb.append("#" + getPseudoOrder() + "  ");
		}
		boolean showLineNumber = OptionUtil.readBoolean(options, "line", false);
		if(showLineNumber) {
			sb.append("L" + getPseudoOrder() + "  ");
		}
		sb.append(getString());
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return getString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((obj == null) ? 0 : obj.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MexObject other = (MexObject) obj;
		if (this.obj == null) {
			if (other.obj != null)
				return false;
		} else if (!this.obj.equals(other.obj))
			return false;
		return true;
	}
}
