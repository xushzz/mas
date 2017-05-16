package com.sirap.common.command.explicit;

import java.util.List;

import com.sirap.basic.domain.EmailCommandRecord;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.manager.RemoteCommandManager;

public class CommandRemote extends CommandBase {

	private static final String KEY_REMOTE_ENABLED_SWITCH = "rx";
	private static final String KEY_REMOTE_CONFIGURATION = "rc";
	private static final String KEY_REMOTE_COMMANDS = "r..";
	
	@Override
	public boolean handle() {
		if(is(KEY_REMOTE_ENABLED_SWITCH)) {
			boolean flag = !g().isRemoteEnabled();
			g().setRemoteEnabled(flag);
			RemoteCommandManager.g().switchEnability(flag);
			String value = flag ? "enabled" : "disabled";
			C.pl2("Remote " + value + ", " + RemoteCommandManager.g().getKonfigInfo());
			
			return true;
		}
		
		if(is(KEY_REMOTE_CONFIGURATION)) {
			boolean flag = g().isRemoteEnabled();
			String value = flag ? "Enabled" : "Disabled";
			export(value + ", " + RemoteCommandManager.g().getKonfigInfo());
			
			return true;
		}
		
		if(is(KEY_REMOTE_COMMANDS)) {
			List<EmailCommandRecord> records = RemoteCommandManager.g().getAllRemoteCommands();
			export(CollectionUtil.items2PrintRecords(records));
			
			return true;
		}
		
		return false;
	}	
}