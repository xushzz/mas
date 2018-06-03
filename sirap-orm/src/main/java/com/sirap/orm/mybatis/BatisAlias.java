package com.sirap.orm.mybatis;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.D;

@SuppressWarnings("serial")
public class BatisAlias extends MexItem implements Comparable<BatisAlias> {
	private String alias;
	private String simple;
	private String full;
	
	public BatisAlias(String alias, String simple, String full) {
		this.alias = alias;
		this.simple = simple;
		this.full = full;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getSimple() {
		return simple;
	}
	public void setSimple(String simple) {
		this.simple = simple;
	}
	public String getFull() {
		return full;
	}
	public void setFull(String full) {
		this.full = full;
	}
	@Override
	public int compareTo(BatisAlias o) {
		int value = full.compareTo(o.full);
		
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
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
		BatisAlias other = (BatisAlias) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}

	public String toString() {
		return D.jsp(this, this.getClass());
	}
}
