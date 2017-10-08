package com.sirap.common.framework;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.TrumpUtil;
import com.sirap.basic.util.XXXUtil;
	
public class JanitorSIRAP extends Janitor {
    
    public JanitorSIRAP(SimpleKonfig konfig) {
		super(konfig);
		setWhatToCheckAgainst(konfig.getPasswordEncrypted("sirap"));
	}
    
    @Override
    protected boolean verify(String input) {
    	if(input == null || input.length() < 1) {
    		C.pl("Invalid password.");
    		return false;
    	}

    	String passcode = SimpleKonfig.g().getSecurityPasscode();
    	XXXUtil.nullOrEmptyCheck(passcode, "security passcode");
    	
    	String temp = getWhatToCheckAgainst();
    	if(temp == null) {
    		C.pl("Invalid encrypted password provided.");
			return false;
    	}
    	
    	String password = TrumpUtil.decodeBySIRAP(temp, passcode);
    	if(password == null) {
			C.pl("Illegal encrypted password [" + temp + "]");
			return false;
		}
    	
    	boolean flag = input.equalsIgnoreCase(password);
    	
    	return flag;
    }
}
