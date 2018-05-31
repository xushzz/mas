package com.sirap.db.domain;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

@SuppressWarnings("serial")
public class MysqlHelpCategory extends MexItem {
	
	private int id;
	private String name;
	private int parent;
	private List<MysqlHelpCategory> sons;
	private List<MysqlHelpTopic> topics = Lists.newArrayList();
	
	public MysqlHelpCategory(int id, String name, int parent) {
		this.id = id;
		this.name = name;
		this.parent = parent;
	}

	public List<String> list(int kInfo, boolean showTopic) {
		List<String> lines = Lists.newArrayList();
		list(0, lines, kInfo, showTopic);
		
		return lines;
	}
	
	private void list(int depth, List<String> lines, int kInfo, boolean showTopic) {
		int nspace = kInfo > 80 ? 2 : 4;
		if(depth != 0) {
			lines.add(StrUtil.spaces((depth - 1) * nspace) + StrUtil.padLeft("#" + id + "", 3) + " " + name);
		}
		
		if(showTopic) {
			String temp = "{0}. {1} {2}";
			String dent = StrUtil.spaces((depth) * nspace);
			for(MysqlHelpTopic top : topics) {
				String tid = StrUtil.padLeft(top.getId() + "", 3);
				String tname = top.getName();
				String info = kInfo > 0 ? StrUtil.firstK(top.info(), kInfo) : "";
				lines.add(dent + StrUtil.occupy(temp, tid, tname, info));
			}
		}
		
		for(MysqlHelpCategory son : sons) {
			son.list(depth + 1, lines, kInfo, showTopic);
		}
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
	
	public List<MysqlHelpCategory> getSons() {
		return sons;
	}

	public void setSons(List<MysqlHelpCategory> sons) {
		this.sons = sons;
	}
	
	public List<MysqlHelpTopic> getTopics() {
		return topics;
	}

	public void setTopics(List<MysqlHelpTopic> topics) {
		this.topics = topics;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		MysqlHelpCategory other = (MysqlHelpCategory) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MysqlHelpCategory [id=" + id + ", name=" + name + ", parent=" + parent + "]";
	}
}
