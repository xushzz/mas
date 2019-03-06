package com.sirap.common.domain;

import java.util.Date;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.FileUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class Album extends MexItem {
	private String url;
	private String name;
	private String tag;
	private String when;
	private Date whenDate;
	private List<String> links;
	private List listObj;
	
	public Album(String name, List<String> links) {
		this.name = name;
		this.links = links;
	}
	
	public static Album of(String name, List<String> links) {
		Album al = new Album(name, links);
		
		return al;
	}
	
	public String getUrl() {
		return url;
	}

	public Album setUrl(String url) {
		this.url = url;
		return this;
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

	public String getWhen() {
		return when;
	}

	public Album setWhen(String when) {
		this.when = when;
		return this;
	}
	
	public Date getWhenDate() {
		return whenDate;
	}

	public Album setWhenDate(Date whenDate) {
		this.whenDate = whenDate;
		return this;
	}

	public String getTimeAgo() {
		if(whenDate == null) {
			return null;
		}
		return DateUtil.timeAgo(whenDate);
	}

	public List getListObj() {
		return listObj;
	}

	public Album setListObj(List listObj) {
		this.listObj = listObj;
		return this;
	}
	
}
