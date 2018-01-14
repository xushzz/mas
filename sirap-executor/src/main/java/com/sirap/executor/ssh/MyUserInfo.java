package com.sirap.executor.ssh;

import com.jcraft.jsch.UserInfo;
import com.sirap.basic.tool.C;

/** 
 * This class provide interface to feedback information to the user. 
 */  
public class MyUserInfo implements UserInfo {  
  
    @Override  
    public String getPassphrase() {  
    	info("MyUserInfo.getPassphrase()");
    	
        return null;  
    }  
  
    @Override  
    public String getPassword() {  
    	info("MyUserInfo.getPassword()");
    	
        return null;  
    }  
  
    @Override  
    public boolean promptPassphrase(final String arg0) {  
    	info("MyUserInfo.promptPassphrase()");  
    	info(arg0);
    	
        return false;  
    }  
  
    @Override  
    public boolean promptPassword(final String arg0) {  
    	info("MyUserInfo.promptPassword()");  
    	info(arg0);  
    	
        return false;  
    }
  
    @Override  
    public boolean promptYesNo(final String arg0) {  
        return arg0.contains("The authenticity of host");
    }
  
    @Override  
    public void showMessage(final String arg0) {  
    	info("MyUserInfo.showMessage()");  
    } 
    
    public void info(String value) {
    	C.pl("ssh: " + value);
    }
}