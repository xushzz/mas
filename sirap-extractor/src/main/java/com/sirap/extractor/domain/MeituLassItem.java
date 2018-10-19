package com.sirap.extractor.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MeituLassItem extends MexItem implements Comparable<MeituLassItem> {
	
	private String name;
	private String path;
	private String intro;
	private MeituOrgItem org;
	
	public MeituLassItem(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public MeituLassItem(String name) {
		this.name = name;
	}
	
	public MeituLassItem() {
	}
	
	public MeituOrgItem getOrg() {
		return org;
	}

	public void setOrg(MeituOrgItem org) {
		this.org = org;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
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
		MeituLassItem other = (MeituLassItem) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public int lassId() {
		String temp = StrUtil.parseParam("t/(\\d+)", path);
		int id = temp != null ? Integer.parseInt(temp) : 99999;
		
		return id;
	}

	@Override
	public int compareTo(MeituLassItem o) {
		int va = lassId();
		int vb = o.lassId();
		
		return va - vb;
	}
	
	@Override
	public List toList(String options) {
		return Lists.newArrayList(path, name, intro, org);
	}
	
	@Override
	public String toPrint(String options) {
		return StrUtil.connWithCommaSpace(toList());
	}
	
	@Override
	public String toString() {
		return toPrint("");
	}
}
