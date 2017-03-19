package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class Code extends MexItem implements Comparable<Code> {
	private String name;
	private String type;
	private String meaning;
	
	public Code() {
		
	}
	
	public Code(String name, String meaning) {
		this.name = name;
		this.meaning = meaning;
	}
	
	public Code(String name, String type, String meaning) {
		this.name = name;
		this.type = type;
		this.meaning = meaning;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public int compareTo(Code o) {
		return name.compareTo(o.name);
	}

	public boolean isMatched(String keyWord) {
		if(keyWord.length() < 2) {
			return false;
		}
		
		if(StrUtil.contains(name, keyWord, 2)) {
			return true;
		}
		
		if(StrUtil.contains(type, keyWord, 3)) {
			return true;
		}
		
		if(StrUtil.contains(meaning, keyWord, 3)) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((meaning == null) ? 0 : meaning.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Code other = (Code) obj;
		if (meaning == null) {
			if (other.meaning != null)
				return false;
		} else if (!meaning.equals(other.meaning))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append("\t");
		sb.append(type).append("\t");
		sb.append(meaning);
		
		return sb.toString(); 
	}
	
	public boolean parse(String source) {
		String[] info = source.split("\t");
		if(info.length != 3) {
			return false;
		}
		
		setName(info[0].trim());
		setType(info[1].trim());
		setMeaning(info[2].trim());
		
		return true;
	}

	public void print() {
		StringBuffer sb = new StringBuffer();
		sb.append(StrUtil.extendNice(name, 10));
		sb.append(StrUtil.extendNice(type, 20));
		sb.append(StrUtil.extendNice(meaning, 40));
		
		C.pl(sb);
	}
}
