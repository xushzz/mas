package com.sirap.common.framework;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.SecurityUtil;

public class ArmedJanitor extends Janitor {
    
    public ArmedJanitor(SimpleKonfig konfig) {
		super(konfig);
		setPasswordCheckNeeded(true);
	}

	@Override
    protected int getMaxAttempts() {
    	return 3;
    }
    
    @Override
    protected boolean verify(String input) {
    	if(input == null || input.length() < 1) {
    		C.pl("Invalid password.");
    		return false;
    	}
    	
    	String encryptedPwd = SimpleKonfig.g().getPasswordEncrypted();
    	if(encryptedPwd == null || encryptedPwd.length() < 16) {
    		C.pl("No valid encrypted password provided.");
			return false;
    	}
    	
    	String md5Pwd = SecurityUtil.md5(input);
    	boolean flag = md5Pwd.equalsIgnoreCase(encryptedPwd);
    	
    	return flag;
    }
}
