package com.sirap.hua;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class HuaUtils {
	public static List<SysMenu> readMenusByLines(List<String> lines) {
		List<SysMenu> items = Lists.newArrayList();
		
		for(String line : lines) {
			SysMenu menu = new SysMenu();
			if(menu.parse(line)) {
				items.add(menu);
			}
		}
		
		return items;
	}
	
	public static List<SysMenu> readMenus(List<List> rows) {
		List<SysMenu> items = Lists.newArrayList();
		
		for(List row : rows) {
			SysMenu menu = new SysMenu();
			if(menu.parse(row)) {
				items.add(menu);
			}
		}
		
		return items;
	}
	
	private static void addKids(SysMenu current, List<SysMenu> menus) {
		String id = current.getId();
		for(SysMenu menu : menus) {
			if(StrUtil.equals(menu.getParent(), id)) {
				addKids(menu, menus);
				current.addKid(menu);
			}
		}
	}
	
	public static SysMenu treeOf(List<SysMenu> menus, String rootId) {
		SysMenu root = null;
		for(SysMenu menu : menus) {
			if(StrUtil.equals(menu.getId(), rootId)) {
				root = menu;
				break;
			}
		}

		if(root == null) {
			XXXUtil.alert("No menu with such id: " + rootId);
		}
		
		addKids(root, menus);
		
		return root;
	}
}
