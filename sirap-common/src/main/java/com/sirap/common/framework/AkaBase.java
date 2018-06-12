package com.sirap.common.framework;

import java.util.List;

import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.MexedTimer;
import com.sirap.basic.exception.QuitException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.MexUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.ThreadUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.command.CommandQuit;
import com.sirap.common.manager.AlarmManager;
import com.sirap.common.manager.CommandHistoryManager;
import com.sirap.common.manager.LoginHistoryManager;
import com.sirap.common.manager.RemoteCommandManager;
import com.sirap.common.manager.VFileManager;

public abstract class AkaBase extends MexedTimer {
	
	public static final String USERNAME = "KY#1917";
	private static final int MAX_LEN_COMMAND = 9999;
	private boolean isSessionTimeout;
    
	public final void init(String[] args) {
		initKonfig(args);
    	CommandHistoryManager.g().start();
	}
    
	protected abstract void initKonfig(String[] args);
	
	protected void loadAppData() {
		ThreadUtil.executeInNewThread(LoginHistoryManager.class, "g", true);
		ThreadUtil.executeInNewThread(RemoteCommandManager.class, "g", true);
		ThreadUtil.executeInNewThread(VFileManager.class, "g", true);
	}
    protected abstract Janitor getJanitor();
    protected abstract String prompt();
    protected abstract String system();
	protected abstract Konfig getKonfig();
    	
    public void run() {
    	welcome();
    	
    	Janitor smith = getJanitor();
    	try {
        	if(!smith.checkPassword()) {
        		byebye();
        		return;
        	}
    	} catch (QuitException ex) {
    		C.pl(ex.getMessage());
    		byebye();
    		return;
    	}

    	loadAppData();

    	String initCommands = SimpleKonfig.g().getUserValueOf("command.init");
    	if(!EmptyUtil.isNullOrEmptyOrBlank(initCommands)) {
    		String[] commands = initCommands.split(";");
    		for(int i = 0; i < commands.length; i++) {
    			String initCommand = commands[i];
    			if(!EmptyUtil.isNullOrEmptyOrBlank(initCommand)) {
            		C.pl(prompt() + initCommand);
            		smith.process(initCommand);
    			}
    		}
    	}

    	startTimer();

    	try {
    		while(true) {
    			String input = MexUtil.askForInput(prompt());
    			if(input == null) {
    				C.pl2("Something wrong, did you just press Ctrl and C/V/Q/E or some other shit?");
    				continue;
    			}
    			
    			if(input.length() > MAX_LEN_COMMAND) {
    				String template = "The input is too lengthy[{0}], make it no more than {1}.";
    				C.pl2(StrUtil.occupy(template, input.length(), MAX_LEN_COMMAND));
    				continue;
    			}
    			
    			if(!smith.checkExpiration()) {
    				break;
    			}
    			
    			CommandBase cmd = new CommandQuit(input);
	        	if(cmd.handle()) {
	        		if(cmd.isToCollect()) {
	        			CommandHistoryManager.g().collect(input);
	        		}
	        		cancelTimerTask();
	        		break;
	        	}

    			if(isSessionTimeout) {
    				if(!smith.checkPassword()) {
    		    		break;
    		    	}
    				updateTimerTask();
					isSessionTimeout = false;
					welcomeBack();
    				continue;
    			}
	        	
	        	updateTimerTask();
	        	smith.process(input);
        	}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}

    	cancelTimer();
    	AlarmManager.g().cancelAlarms(false, null);
    	LoginHistoryManager.g().exitGracefully();
    	RemoteCommandManager.g().cancelTimer();
    	System.exit(0);
    	
    	byebye();
    }
    
    protected void welcome() {
    	List<String> lines = IOUtil.readLines("/welcome.txt", Konstants.CODE_UTF8);
    	lines.remove(0);
    	C.list(lines, false);
		String source = "=== hi {0}, welcome to {1}, {2} ===";
		String result = StrUtil.occupy(source, USERNAME, system(), getDatetimeString());
		C.pl2(result);
	}
    
	protected String getDatetimeString() {
		return DateUtil.displayDate(DateUtil.WEEK_DATE_TIME, SimpleKonfig.g().getLocale());
	}
    
    protected void welcomeBack() {
		String source = "=== yo {0}, welcome back, {1} ===";
		String result = StrUtil.occupy(source, USERNAME, getDatetimeString());
		C.pl2(result);
	}
    
    protected void byebye() {
		String source = "=== {0}, bye, {1} ===";
		String result = StrUtil.occupy(source, USERNAME, getDatetimeString());
		C.pl2(result);
	}
    
	@Override
	protected void timerAction()  {
		isSessionTimeout = true;
		timeout();
    }
	
	protected void timeout() {
		C.pl();
    	C.pl(StrUtil.occupy("=== Session TIMEOUT since {0} ===", getDatetimeString()));
    	C.pl("Press ENTER to continue, [" + CommandBase.KEY_EXIT + "] to quit");
	}
}
