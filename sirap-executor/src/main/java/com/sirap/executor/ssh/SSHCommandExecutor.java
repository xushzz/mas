package com.sirap.executor.ssh;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.framework.SimpleKonfig;  
  
public class SSHCommandExecutor {
	
	private String ip;  
    private String username;    
    private String password;  
    
	private static SSHCommandExecutor instance;

	public static SSHCommandExecutor g() {
		String values = SimpleKonfig.g().getUserValueOf("ssh");
		if(EmptyUtil.isNullOrEmpty(values)) {
    		String msg = "no ssh connection found by key: ssh";
    		throw new MexException(msg);
    	}
		
    	List<String> items = StrUtil.split(values, ',');
    	if(items.size() < 3) {
    		String msg = "invalid ssh connection: " + values;
    		throw new MexException(msg);
    	}

	    instance = new SSHCommandExecutor(items.get(0), items.get(1), items.get(2));
		return instance;
	}
	
	public static SSHCommandExecutor g2(String ip, String username, String password) {
		instance = new SSHCommandExecutor(ip, username, password);
		
		return instance;
	}

	public SSHCommandExecutor(String ip, String username, String password) {
		this.ip = ip;
		this.username = username;
		this.password = password;
	}
	
    private static final int DEFAULT_SSH_PORT = 22;  
  
    private Vector<String> stdout = new Vector<>();
    
    public List<String> execute(final String command) throws MexException {
    	remoteExecute(command);
    	Vector<String> stdout = getStandardOutput();
    	
    	List<String> items = new ArrayList<>();
        for (String item : stdout) { 
        	items.add(item);
        }
        
        return items;
    }
    
    public String getMilliSecondsFrom1970() {
    	String command = "date +%s";
    	remoteExecute(command);
    	Vector<String> items = getStandardOutput();
    	if(stdout.size() != 1) {
    		throw new MexException("bad request: " + command);
    	}
    	
    	String result = items.get(0) + "000";
        return result; 
    }
  
    private int remoteExecute(final String command) throws MexException {  
        int returnCode = 0;  
        JSch jsch = new JSch();  
        MyUserInfo userInfo = new MyUserInfo();  
  
        try {  
            Session session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);  
            session.setPassword(password);  
            session.setUserInfo(userInfo);  
            session.connect();
  
            Channel channel = session.openChannel("exec");  
            ((ChannelExec) channel).setCommand(command);  
  
            channel.setInputStream(null);
            InputStream is = channel.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(is));  
  
            channel.connect();  
  
            String line;
            stdout.clear();
            while ((line = input.readLine()) != null) {  
                stdout.add(line);  
            }  
            input.close();
  
            if (channel.isClosed()) {  
                returnCode = channel.getExitStatus();  
            }  
  
            channel.disconnect();  
            session.disconnect();  
        } catch (Exception ex) {
        	String msg = ex.getMessage() + ", info: " + this;
            throw new MexException(msg);  
        }  
        
        return returnCode;  
    }  
    
    public String getIp() {
		return ip;
	}

	private Vector<String> getStandardOutput() {  
        return stdout;
    }

	@Override
	public String toString() {
		return "SSHCommandExecutor [ip=" + ip + ", username=" + username + ", password=" + password + "]";
	}
}