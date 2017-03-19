package com.sirap.basic.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;

@SuppressWarnings("serial")
public class MexedList implements Serializable {
	private List<MexItem> items;
	private List<String> header;
	private List<String> footer;
	
	public MexedList(List<MexItem> items) {
		this.items = items;
	}

	public List<MexItem> getItems() {
		return items;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public List<String> getFooter() {
		return footer;
	}

	public void setFooter(List<String> footer) {
		this.footer = footer;
	}
	
	public List<String> getAllRecords() {
		
		List<String> list = new ArrayList<String>();
		if(header != null) {
			list.addAll(header);
		}
		
		if(items != null) {
			list.addAll(CollectionUtil.items2PrintRecords(items));
		}
		
		if(footer != null) {
			list.addAll(footer);
		}
		
		return list;
	}
	
	public void print() {
		C.listWithoutTotal(getAllRecords());
	}
}
