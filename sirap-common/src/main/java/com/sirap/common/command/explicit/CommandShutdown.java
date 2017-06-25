package com.sirap.common.command.explicit;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.common.command.CommandBase;
import com.sirap.common.extractor.WorldTimeBJTimeOrgExtractor;

public class CommandShutdown extends CommandBase {

	private static final String KEY_WINDOWS_LOCK = "l";
	private static final String KEY_WINDOWS_LOGOUT = "out";
	private static final String KEY_WINDOWS_TURNOFF = "off";
	private static final String KEY_WINDOWS_RESTART = "rest";
	
	private static final String VALUE_WINDOWS_LOCK = "rundll32 user32.dll,LockWorkStation";
	private static final String VALUE_WINDOWS_LOGOUT = "shutdown -l -t 1";
	private static final String VALUE_WINDOWS_TURNOFF = "shutdown -s -t 1";
	private static final String VALUE_WINDOWS_RESTART = "shutdown -r -t 1";

	{
		helpMeanings.put("windows.lock", CommandShutdown.VALUE_WINDOWS_LOCK);
		helpMeanings.put("windows.off", CommandShutdown.VALUE_WINDOWS_TURNOFF);
		helpMeanings.put("windows.restart", CommandShutdown.VALUE_WINDOWS_RESTART);
		helpMeanings.put("windows.logout", CommandShutdown.VALUE_WINDOWS_LOGOUT);
	}

	@Override
	public boolean handle() {
		if(is(KEY_WINDOWS_LOCK)) {
			PanaceaBox.execute(VALUE_WINDOWS_LOCK);
			C.pl2("lock computer.");
			
			return true;
		}
		
		if(is(KEY_WINDOWS_TURNOFF)) {
			PanaceaBox.execute(VALUE_WINDOWS_TURNOFF);
			C.pl2("turn off computer immediately.");
			
			return true;
		}
		
		if(is(KEY_WINDOWS_LOGOUT)) {
			PanaceaBox.execute(VALUE_WINDOWS_LOGOUT);
			C.pl2("log out computer immediately.");
			
			return true;
		}
		
		if(is(KEY_WINDOWS_RESTART)) {
			PanaceaBox.execute(VALUE_WINDOWS_RESTART);
			C.pl2("restart computer immediately.");
			
			return true;
		}
		
		return false;
	}
}
