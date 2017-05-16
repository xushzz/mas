package com.sirap.basic.domain.xml;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class XmlItem extends MexItem {
	private String tagName;
	private String tagValue;

	public XmlItem() {
		
	}

	public XmlItem(String tagName, String tagValue) {
		this.tagName = tagName;
		this.tagValue = tagValue;
	}
	
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	public String wrapAsXml() {
		String temp = "<{0}>{1}</{0}>";
		String value = StrUtil.occupy(temp, tagName, tagValue);
		
		return value;
	}
	
	public String display() {
		String value = tagName + ": " + tagValue;
		return value;
	}
	
	@Override
	public String toString() {
		return display();
	}
}