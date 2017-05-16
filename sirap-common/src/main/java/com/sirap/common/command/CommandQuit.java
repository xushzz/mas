package com.sirap.common.command;

public class CommandQuit extends CommandBase {
	
	public CommandQuit(String command) {
		this.command = command;
	}
	
	@Override
	public boolean handle() {
		if(isIn(KEY_EXIT)) {
    		return true;
		}
		
		return false;
	}
}
