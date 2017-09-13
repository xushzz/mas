package com.sirap.extractor.domain;

import com.sirap.basic.domain.MexItem;

@SuppressWarnings("serial")
public class MMKChannelItem extends MexItem implements Comparable<MMKChannelItem> {

	private int id;
	private String name;
	private String category;
	
	public MMKChannelItem(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public int compareTo(MMKChannelItem o) {
		return id - o.id;
	}
	
	@Override
	public String toPrint() {
		String value = printAllButNull(id, name);
		return value;
	}
	
	@Override
	public String toString() {
		String value = printAll(id, name);
		return value;
	}
}
