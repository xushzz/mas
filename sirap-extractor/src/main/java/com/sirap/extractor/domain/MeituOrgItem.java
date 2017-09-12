package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MeituOrgItem extends MexItem implements Comparable<MeituOrgItem> {

	private String name = "SIRAP";
	private String path;
	
	public MeituOrgItem(String path) {
		this.path = path;
	}
	
	public MeituOrgItem(String name, String path) {
		this.name = name;
		this.path = path;
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

	public String getShortPath() {
		String temp = path.replaceAll("/index_\\d+\\.html", "");
		return temp;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	public int orgId() {
		String temp = StrUtil.parseParam("x/(\\d+)", path);
		int id = temp != null ? Integer.parseInt(temp) : 99999;
		
		return id;
	}

	@Override
	public int compareTo(MeituOrgItem o) {
		int va = orgId();
		int vb = o.orgId();
		
		return va - vb;
	}
	@Override
	public String toString() {
		String value = getShortPath() + " " + name;
		return value;
	}
}
