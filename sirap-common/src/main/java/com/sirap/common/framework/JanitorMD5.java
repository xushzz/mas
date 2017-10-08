package com.sirap.common.framework;

import com.sirap.basic.tool.C;
import com.sirap.basic.util.SecurityUtil;
import com.sirap.basic.util.StrUtil;

public class JanitorMD5 extends Janitor {
    
    public JanitorMD5(SimpleKonfig konfig) {
		super(konfig);
		setWhatToCheckAgainst(konfig.getPasswordEncrypted("md5"));
	}

    @Override
    protected boolean verify(String input) {
    	if(input == null || input.length() < 1) {
    		C.pl("Invalid password.");
    		return false;
    	}
    	
    	String encryptedPwd = getWhatToCheckAgainst();
    	if(encryptedPwd == null || encryptedPwd.length() < 16) {
    		C.pl("No valid encrypted password provided.");
			return false;
    	}
    	
    	boolean flag = StrUtil.equals(SecurityUtil.md5(input), encryptedPwd);
    	
    	return flag;
    }
}
