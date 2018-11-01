package com.sirap.common.domain;

import java.util.List;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class Album extends MexItem {
	private boolean useUnique;
	private String name;
	private String tag;
	private List<String> links;
	
	public Album(String name, List<String> links) {
		super();
		this.name = name;
		this.links = links;
	}
	
	public boolean isUseUnique() {
		return useUnique;
	}

	public void setUseUnique(boolean useUnique) {
		this.useUnique = useUnique;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getLinks() {
		return links;
	}
	public void setLinks(List<String> links) {
		this.links = links;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
