package com.sirap.ldap.online;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.util.EmptyUtil;

public class SearchParams {
	
	private List<String> returningAttributes = new ArrayList<>();
	private int maxPage = 4;
	private int pageSize = 100;
	
	public SearchParams(List<String> returningAttributes, int maxPage, int pageSize) {
		if(!EmptyUtil.isNullOrEmpty(returningAttributes)) {
			this.returningAttributes.clear();
			this.returningAttributes.addAll(returningAttributes);
		}
		this.maxPage = maxPage;
		this.pageSize = pageSize;
	}
	
	public List<String> getReturningAttributes() {
		return returningAttributes;
	}
	
	public void setReturningAttributes(List<String> returningAttributes) {
		this.returningAttributes.clear();
		if(!EmptyUtil.isNullOrEmpty(returningAttributes)) {
			this.returningAttributes.addAll(returningAttributes);
		}
	}
	
	public void addAttribute(String name) {
		returningAttributes.add(name);
	}
	
	public void removeAllAttributes() {
		returningAttributes.clear();
	}
	
	public void removeAttribute(String name) {
		returningAttributes.remove(name);
	}
	
	public int getMaxPage() {
		return maxPage;
	}
	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}