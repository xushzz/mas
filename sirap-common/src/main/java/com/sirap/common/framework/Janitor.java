package com.sirap.common.framework;

import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.command.CommandHelp;
import com.sirap.common.command.CommandTask;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.manager.CommandHistoryManager;

public class Janitor extends Checker {
	
	protected Konfig konfig;
	private List<Class<?>> commandList = new ArrayList<Class<?>>();
	
	private boolean expirationCheckNeeded;
	private boolean passwordCheckNeeded;

	public Janitor(Konfig konfig) {
		this.konfig = konfig;
		instance = this;
	}

	public void setExpirationCheckNeeded(boolean expirationCheckNeeded) {
		if(expirationCheckNeeded) {
			initLoginTime();
		}
		
		this.expirationCheckNeeded = expirationCheckNeeded;
	}

	public void setPasswordCheckNeeded(boolean passwordCheckNeeded) {
		this.passwordCheckNeeded = passwordCheckNeeded;
	}
	
	private static Janitor instance;

	public static Janitor g() {
		return instance;
	}
	
	private void initCommandList() {
		List<String> commandNodes = SimpleKonfig.g().getCommandClassNames();
		
		commandList.clear();
		for(String className : commandNodes) {
			try {
				Class<?> clazz = Class.forName(className);
				commandList.add(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

    public void process(String source) {
    	process(source, true);
    }
    
    public void process(String source, boolean toStashTarget) {
    	long start = System.currentTimeMillis();
    	konfig.getStash().put("startInMillis", start);
    	if(EmptyUtil.isNullOrEmptyOrBlank(source)) {
    		return;
    	}
    	
    	String input = source.trim();
    	
    	CommandBase cmd = new CommandTask();
    	cmd.setInstructions(input);
    	if(cmd.process()) {
    		if(cmd.isToCollect()) {
    			CommandHistoryManager.g().collect(input);
    		}
    		return;
    	}
    	
    	InputAnalyzer fara = new InputAnalyzer(input);
    	String command = fara.getCommand();
    	Target target = fara.getTarget();
    	if(toStashTarget) {
    		konfig.getStash().put(CommandBase.STASH_USER_INPUT_TARGET, target);
    	}
    	
    	if(EmptyUtil.isNullOrEmpty(command)) {
    		return;
    	}
    	
    	cmd = new CommandHelp();
    	cmd.setInstructions(input, command, target);
    	if(cmd.process()) {
    		if(cmd.isToCollect()) {
    			CommandHistoryManager.g().collect(input);
    		}
    		return;
    	}
    	
    	initCommandList();
    	
		if(EmptyUtil.isNullOrEmpty(commandList)) {
			C.pl2("Uncanny, no command nodes configured.");
			return;
		}
    	
    	for(Class<?> classType: commandList) {
    		cmd = ObjectUtil.createInstanceViaConstructor(classType, CommandBase.class);
    		cmd.setInstructions(input, command, target);
    		
    		if(cmd.process()) {
    			if(cmd.isToCollect()) {
        			CommandHistoryManager.g().collect(input);
        		}
    			return;
    		}
    	}
    	
    	CommandHistoryManager.g().collect(input);
    }

    protected boolean checkPassword() {
    	if(!passwordCheckNeeded) {
    		return true;
    	}
    	
    	boolean flag = askAndCheckPassword();
    	
    	return flag;
    }
    
    @Override
    protected boolean verify(String input) {
    	return false;
    }
    
    protected boolean checkExpiration() {
		if(!expirationCheckNeeded) {
			return true;
		}
		
		boolean flag = calculateExpiration();
		
		return flag;
    }
}
