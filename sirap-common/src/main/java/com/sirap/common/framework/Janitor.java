package com.sirap.common.framework;

import java.util.List;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.ThreadUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.command.CommandHelp;
import com.sirap.common.command.CommandTask;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.Target;
import com.sirap.common.manager.CommandHistoryManager;

public class Janitor extends Checker {
	
	protected Konfig konfig;
	
	private boolean expirationCheckNeeded;

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

	private static Janitor instance;

	public static Janitor g() {
		if(instance == null) {
			instance = new Janitor(SimpleKonfig.g());
		}
		return instance;
	}
	
	public int depth;
	
	public boolean process(String origin) {
		return process(origin, null);
	}
	
    public boolean process(String origin, String highOptions) {
    	boolean fromOutside = SimpleKonfig.g().isFromWeb();
    	String niceinput = JanitorHelper.findCoinsFromSatos(origin);
    	InputAnalyzer fara = new InputAnalyzer(niceinput);
    	fara.setOptions(OptionUtil.mergeOptions(highOptions, fara.getOptions()));
    	String command = fara.getCommand();
    	String options = fara.getOptions();
    	
    	String alias = JanitorHelper.findAliasFromUserProperties(command);
    	if(alias != null) {
//    		D.pla(command, options, niceinput);
    		InputAnalyzer temp = new InputAnalyzer(alias);
    		
    		String command2 = temp.getCommand();
    		String options2 = OptionUtil.mergeOptions(options, temp.getOptions());
    		String niceinput2 = command2;
    		if(!EmptyUtil.isNullOrEmpty(options2)) {
    			niceinput2 += "$" + options2;
    		}
    		niceinput = niceinput2;
    		command = command2;
    		options = options2;
//    		D.pla(command2, options2, niceinput2);
    	}
    	
//    	if("".isEmpty()) return false;

    	long start = System.currentTimeMillis();
    	Stash.g().place(Stash.KEY_START_IN_MILLIS, start);
    	if(EmptyUtil.isNullOrEmptyOrBlank(command)) {
    		return false;
    	}
    	
    	if(!fromOutside) {
    		CommandBase cmd = new CommandTask();
        	cmd.setInstructions(command);
        	if(cmd.process()) {
        		if(cmd.isToCollect()) {
        			CommandHistoryManager.g().collect(command);
        		}
        		return true;
        	}
    	}

    	if(StrUtil.equals(command, "suck")) {
    		SimpleKonfig.g().setSuckOptionsEnabled(true);
    		C.pl2("Enable to suck options.");
    	} else if(StrUtil.equals(command, "nosuck")) {
    		SimpleKonfig.g().setSuckOptionsEnabled(false);
    		C.pl2("Disable to suck options.");
    	} 

    	final String niceinput2 = niceinput;
    	final String command2 = command;
    	final String options2 = options;
    	boolean newThread = OptionUtil.readBooleanPRI(options, "new", false);
    	if(newThread && !fromOutside) {
    		ThreadUtil.executeInNewThread(new Runnable() {
				@Override
				public void run() {
					executionUnit(niceinput2, command2, options2, fara.getTarget());
				}
			});
    		return true;
    	} else {
    		return executionUnit(niceinput, command, options, fara.getTarget());
    	}
    }
    
    private boolean executionUnit(String input, String command, String options, Target target) {
    	if(EmptyUtil.isNullOrEmpty(command)) {
    		return false;
    	}
    	
    	{
        	CommandBase cmd = new CommandHelp();
        	cmd.setInstructions(input, command, options, target);
        	if(cmd.process()) {
        		if(cmd.isToCollect()) {
        			CommandHistoryManager.g().collect(input);
        		}
        		return true;
        	}
    	}
    	
    	List<CommandBase> commandList = SimpleKonfig.g().getCommandInstances();
    	for(CommandBase handler: commandList) {
    		handler.setInstructions(input, command, options, target);
    		
    		if(handler.process()) {
    			if(handler.isToCollect()) {
        			CommandHistoryManager.g().collect(input);
        		}
    			return true;
    		}
    	}
    	
    	CommandHistoryManager.g().collect(input);
    	return false;
    }

    protected boolean checkPassword() {
    	if(EmptyUtil.isNullOrEmpty(getWhatToCheckAgainst())) {
    		return true;
    	}
    	
    	boolean flag = askAndCheckPassword();
    	
    	return flag;
    }
    
    @Override
    protected boolean verify(String input) {
    	throw new MexException("This method must be overridden");
    }
    
    protected boolean checkExpiration() {
		if(!expirationCheckNeeded) {
			return true;
		}
		
		boolean flag = calculateExpiration();
		
		return flag;
    }
}
