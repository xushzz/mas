package com.sirap.common.command.explicit;

import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.PanaceaBox;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.framework.command.target.TargetConsole;

public class CommandDos extends CommandBase {
	
	private static final String KEY_COMMAND_DOS = "c\\.";
	private static final String KEY_WINDOWS_LOCK = "l";
	private static final String KEY_WINDOWS_LOGOUT = "out";
	private static final String KEY_WINDOWS_TURNOFF = "off";
	private static final String KEY_WINDOWS_RESTART = "rest";
	private static final String KEY_WINDOWS_SLEEP = "sle{3,}";
	
	private static final String VALUE_WINDOWS_LOCK = "rundll32 user32.dll,LockWorkStation";
	private static final String VALUE_WINDOWS_LOGOUT = "shutdown -l -t 1";
	private static final String VALUE_WINDOWS_TURNOFF = "shutdown -s -t 1";
	private static final String VALUE_WINDOWS_RESTART = "shutdown -r -t 1";
	private static final String VALUE_WINDOWS_SLEEP = "shutdown -h";
	public static final List<String> KEY_BAN_WORDS = StrUtil.split("shutdown,rundll32,md,rd,del,copy,move,ren,replace,tskill");

	@Override
	public boolean handle() {
		
		if(!PanaceaBox.isWindows()) {
			return false;
		}
		
		if(!g().isFromWeb()) {
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
			
			if(StrUtil.isRegexMatched(KEY_WINDOWS_SLEEP, command)) {
				PanaceaBox.execute(VALUE_WINDOWS_SLEEP);
				C.pl2("computer sleeps immediately.");
				
				return true;
			}
		}
	
		solo = parseParam(KEY_COMMAND_DOS + "(.{3,}?)");
		if(solo != null) {
			executeInternalCmd(solo);
			return true;
		}
		
		String dosPrefix = g().getUserValueOf("dos.prefix");
		if(!EmptyUtil.isNullOrEmpty(dosPrefix)) {
			List<String> items = StrUtil.split(dosPrefix);
			for(String item : items) {
				if(StrUtil.equalsCaseSensitive(command, item) || command.startsWith(item + " ")) {
					String temp = command;
					String path = OptionUtil.readString(options, "p");
					if(!EmptyUtil.isNullOrEmpty(path)) {
						temp = translatePath(temp, path);
						C.pl(temp);
					}
					executeInternalCmd(temp);
					return true;
				}
			}
		}

		return false;
	}
	
	protected void executeInternalCmd(String conciseCommand) {
		if(g().isFromWeb() && StrUtil.contains(conciseCommand, KEY_BAN_WORDS)) {
			export("Forbidden to execute: " + conciseCommand);
			return;
		}
		
		Boolean printAlong = OptionUtil.readBoolean(options, "ing");
		String zoo = OptionUtil.readString(options, "z");
		if(printAlong == null) {
			printAlong = zoo == null;
		}
		String newCommand = "cmd /c " + conciseCommand;
		if(PanaceaBox.isMac()) {
			newCommand = conciseCommand;
		}
		List<String> result = PanaceaBox.executeAndRead(newCommand, printAlong);
		if(EmptyUtil.isNullOrEmpty(result)) {
			export("Command [" + conciseCommand + "] executed.");
		} else {
			if(g().isFromWeb()) {
				export(result);
			} else {
				if(target instanceof TargetConsole) {
					if(printAlong) {
						if(result.size() > 5) {
							C.total(result.size());
						}
						
						C.pl();
					} else {
						export(result);
					}
				} else {
					export(result);
				}
			}
		}
	}
}
