package com.sirap.common.framework;

import com.sirap.basic.tool.C;
import com.sirap.security.MrTrump;

public class VerbalJanitor extends Janitor {
    
    public VerbalJanitor(SimpleKonfig konfig) {
		super(konfig);
		setPasswordCheckNeeded(true);
	}

	@Override
    protected int getMaxAttempts() {
    	return 99;
    }
    
    @Override
    protected boolean verify(String input) {
    	if(input == null || input.length() < 1) {
    		C.pl("Invalid password.");
    		return false;
    	}

    	String passcode = SimpleKonfig.g().getSecurityPasscode();
    	String temp = SimpleKonfig.g().getPasswordEncrypted();
    	if(temp == null) {
    		C.pl("Invalid encrypted password provided.");
			return false;
    	}
    	
    	String password = MrTrump.decodeBySIRAP(temp, passcode);
    	if(password == null) {
			C.pl("Illegal encrypted password [" + temp + "]");
			return false;
		}
    	
    	boolean flag = input.equalsIgnoreCase(password);
    	
    	return flag;
    }
}
