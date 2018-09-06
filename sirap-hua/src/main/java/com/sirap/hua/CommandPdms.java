package com.sirap.hua;

import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.OptionUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.db.DBHelper;

public class CommandPdms extends CommandBase {

	private static final String KEY_MENU = "menu";
	
	@Override
	public boolean handle() {
		regex = KEY_MENU + "\\s(.+?)";
		solo = parseParam(regex);
		if(solo != null) {
			List<String> lines = linesOf(solo);
			List<SysMenu> menus = HuaUtils.readMenus(lines);
			C.listSome(menus, 8);
			String rootId = OptionUtil.readString(options, "r", "1");
			SysMenu root = HuaUtils.treeOf(menus, rootId);
			export(root.tree());

			return true;
		}
		
		solo = parseParam("hua\\s+(.+?)");
		if(solo != null) {
			String url = "jdbc:mysql://localhost/pf_pdms";
			String username = "root";
			String password = "ninja";
			String sql = "select id, parent_id, name, href from sys_menu";
			sql = solo;
			List<List> matrix = DBHelper.queryRawList(url, username, password, sql);
			export(matrix);
			
			return true;
		}
		
		return false;
	}
}
