package com.sirap.basic.domain;

@SuppressWarnings("serial")
public class HtmlEntity extends MexItem implements Comparable<HtmlEntity> {
	private String name;
	private int code;
	private String info;
	
	public HtmlEntity(String name, int code, String info) {
		this.name = name;
		this.code = code;
		this.info = info;
	}
	
	public int getCode() {
		return code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		HtmlEntity other = (HtmlEntity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(HtmlEntity bake) {
		return this.code - bake.code;
	}
	
	public String toString() {
		String sa = (char)code + " " + code + " " + name + ", " + info;
		return sa;
	}
}
