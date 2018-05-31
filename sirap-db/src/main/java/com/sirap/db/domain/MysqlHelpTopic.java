package com.sirap.db.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MysqlHelpTopic extends MexItem {
	
	private int id;
	private String name;
	private int parent;
	private String desc;
	private String example;
	
	public MysqlHelpTopic(int id, String name, int parent, String desc, String example) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.desc = desc;
		this.example = example;
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

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
	
	public List<String> list() {
		List<String> items = Lists.newArrayList();
		items.add(StrUtil.occupy("Topic id: {0}", id));
		items.add(StrUtil.occupy("{0} help {1}", "mysql>", name));
		if(!EmptyUtil.isNullOrEmpty(desc)) {
			items.addAll(StrUtil.split(desc.trim(), "\n"));
		}
		if(!EmptyUtil.isNullOrEmpty(example)) {
			items.add("");
			items.add("Examples:");
			items.addAll(StrUtil.split(example.trim(), "\n"));
		}

		return items;
	}
	
	public String info() {
		StringBuffer sb = sb();
		if(!EmptyUtil.isNullOrEmpty(example)) {
			sb.append(example);
		}
		sb.append(" ");
		if(!EmptyUtil.isNullOrEmpty(desc)) {
			sb.append(desc);
		}
		
		String temp = sb.toString();
		temp = temp.replaceAll("mysql>", ">");
		temp = temp.replaceAll("[\\r\\n]", " ");
		temp = temp.replaceAll("\\s{2,}", " ");

		return temp;
	}

	@Override
	public String toString() {
		ValuesItem vi = new ValuesItem(id, name, parent, desc.length(), example.length());
		return vi.toPrint();
	}
}
