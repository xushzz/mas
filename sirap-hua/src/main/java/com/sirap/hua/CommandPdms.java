package com.sirap.hua;

import java.util.List;

import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.RandomUtil;
import com.sirap.common.command.CommandBase;

public class CommandPdms extends CommandBase {

	private static final String KEY_MENU = "menu";
	
	@Override
	public boolean handle() {
		regex = KEY_MENU + "\\s(.+?)";
		solo = parseParam(regex);
		if(solo != null) {
			String url = "jdbc:mysql://localhost/pf_pdms";
			String username = "root";
			String password = "ninja";
			List<List> matrix = DBUtil.queryRawList(url, username, password, solo);
			List<SysMenu> menus = HuaUtils.readMenus(matrix);
//			C.listSome(menus, 8);
			String rootId = OptionUtil.readString(options, "r", "1");
			SysMenu root = HuaUtils.treeOf(menus, rootId);
			export(root.tree());

			return true;
		}
		
		solo = parseParam("hua\\s+(.+?)");
		if(solo != null) {
			String url = "jdbc:mysql://localhost/celine";
			String username = "root";
			String password = "ninja";
			List<List> matrix = DBUtil.queryRawList(url, username, password, solo);
			export(matrix);
			
			return true;
		}
		
		if(is("hay")) {
			export(RandomUtil.letters(23));
			
			return true;
		}
		
		return false;
	}
}
