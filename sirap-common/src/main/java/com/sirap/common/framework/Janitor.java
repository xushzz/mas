package com.sirap.common.framework;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.ObjectUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.SatoUtil;
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
		return instance;
	}
	
	private List<Class<?>> initCommandList() {
		List<String> commandNodes = SimpleKonfig.g().getCommandClassNames();

		List<Class<?>> commandList = new ArrayList<Class<?>>();
		for(String className : commandNodes) {
			try {
				Class<?> clazz = Class.forName(className);
				commandList.add(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return commandList;
	}

    public void process(String origin) {
    	String source = origin;
		try {
			List<TypedKeyValueItem> items = Lists.newArrayList();
			items.addAll(SatoUtil.systemPropertiesAndEnvironmentVaribables());
			items.addAll(konfig.getUserProps().listOf());
			String after = SatoUtil.occupyCoins(source, items);
			if(!StrUtil.equals(source, after)) {
				C.pl(source + " = " + after);
				source = after;
			}
			
		} catch (MexException me) {
			C.pl(me.getMessage());
		}

    	long start = System.currentTimeMillis();
    	Stash.g().place(Stash.KEY_START_IN_MILLIS, start);
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
    	String options = fara.getOptions();
    	Target target = fara.getTarget();
    	
    	boolean newThread = OptionUtil.readBooleanPRI(options, "new", false);
    	if(newThread) {
    		ThreadUtil.executeInNewThread(new Runnable() {
				@Override
				public void run() {
					executionUnit(input, command, options, target);
				}
			});
    	} else {
    		executionUnit(input, command, options, target);
    	}
    }
    
    private void executionUnit(String input, String command, String options, Target target) {
    	if(EmptyUtil.isNullOrEmpty(command)) {
    		return;
    	}
    	
    	CommandBase cmd = new CommandHelp();
    	cmd.setInstructions(input, command, options, target);
    	if(cmd.process()) {
    		if(cmd.isToCollect()) {
    			CommandHistoryManager.g().collect(input);
    		}
    		return;
    	}
    	
    	List<Class<?>> commandList = initCommandList();
    	
		if(EmptyUtil.isNullOrEmpty(commandList)) {
			C.pl2("Uncanny, no command nodes configured.");
			return;
		}
    	
    	for(Class<?> classType: commandList) {
    		cmd = ObjectUtil.createInstanceViaConstructor(classType, CommandBase.class);
    		cmd.setInstructions(input, command, options, target);
    		
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
