package com.sirap.common.domain;

import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class Album extends MexItem {
	private String name;
	private String tag;
	private List<String> links;
	
	public Album(String name, List<String> links) {
		super();
		this.name = name;
		this.links = links;
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
	public Album setTag(String tag) {
		this.tag = tag;
		return this;
	}
	
	public String niceName() {
		String temp2 = "";
		if(!EmptyUtil.isNullOrEmpty(tag)) {
			temp2 = " -" + tag;
		}
		
		String origin = StrUtil.occupy("{0}{1}", name, temp2);
		String temp = FileUtil.generateUrlFriendlyFilename(origin);
		temp = temp.replace("[", "(");
		temp = temp.replace("]", ")");
		
		return temp;
	}
}
