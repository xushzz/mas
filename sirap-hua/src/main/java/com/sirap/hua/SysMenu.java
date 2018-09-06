package com.sirap.hua;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.ValuesItem;
import com.sirap.basic.util.StrUtil;

public class SysMenu extends MexItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String parent;
	private String name;
	private String href;
	
	private List<SysMenu> kids = Lists.newArrayList();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
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
	public void addKid(SysMenu menu) {
		kids.add(menu);
	}

	@Override
	public boolean parse(String record) {
		// 0ca004d6b1bf4bcab9670a5060d82a55 , 3c92c17886944d0687e73e286cada573 , 树结构 , /test/testTree
		List<String> items = StrUtil.split(record);
		if(items.size() > 3) {
			id = items.get(0);
			parent = items.get(1);
			name = items.get(2);
			href = items.get(3);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean parse(List items) {
		if(items == null) {
			return false;
		}
		// 0ca004d6b1bf4bcab9670a5060d82a55 , 3c92c17886944d0687e73e286cada573 , 树结构 , /test/testTree
		if(items.size() > 3) {
			id = items.get(0) + "";
			parent = items.get(1) + "";
			name = items.get(2) + "";
			href = items.get(3) + "";
			return true;
		}
		
		return false;
	}
	
	public List<String> tree() {
		List<String> allItems = Lists.newArrayList();
		tree(allItems, 0, this);
		return allItems;
	}
	
	private void tree(List<String> allItems, int depth, SysMenu menu) {
		String dent = StrUtil.spaces(4 * depth);
		StringBuffer sb = StrUtil.sb();
		sb.append(dent).append(menu.getName());
		String href = menu.href;
		if(href != null && !StrUtil.equals(href, "null")) {
			sb.append(" ").append(href);
		}
		sb.append(" [").append(menu.getId()).append("]");
		
		allItems.add(sb.toString());

		for(SysMenu kid : menu.kids) {
			tree(allItems, depth + 1, kid);
		}
	}
	
	public String toString() {
		ValuesItem vi = new ValuesItem();
		
		vi.add(id);
		vi.add(parent);
		vi.add(name);
		vi.add(href);
		
		return vi.toString();
	}
	
}
