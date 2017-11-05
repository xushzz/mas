package com.sirap.common.domain;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.OptionUtil;

@SuppressWarnings("serial")
public class Link extends MexItem {
	protected String name = "KY";
	protected String href;
	
	public Link() {
		
	}
	
	public Link(String href) {
		this.href = href;
	}
	
	public Link(String name, String href) {
		this.name = name;
		this.href = href;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	@Override
	public boolean parse(String source) {
		String[] info = source.split("\t");
		if(info.length != 2) {
			return false;
		}

		setName(info[0].trim());
		setHref(info[1].trim());
		
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((href == null) ? 0 : href.hashCode());
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
		Link other = (Link) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public boolean isMatched(String keyWord) {
		if(keyWord.length() < 3) {
			return false;
		}
		
		if(name.toLowerCase().contains(keyWord.toLowerCase())) {
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append("\t");
		sb.append(href);
		
		return sb.toString(); 
	}

	@Override
	public String toPrint(String options) {
		boolean showName = OptionUtil.readBooleanPRI(options, "name", true);
		StringBuffer sb = new StringBuffer();
		if(showName) {
			sb.append(name).append(", ");
		}
		sb.append(href);
		
		return sb.toString();
	}
}
