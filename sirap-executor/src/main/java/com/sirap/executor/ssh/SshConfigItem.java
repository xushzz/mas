package com.sirap.executor.ssh;

import com.sirap.basic.tool.D;
import com.sirap.basic.util.OptionUtil;

public class SshConfigItem {
	
	private String host;
	private String username;
	private String password;
	private int port = 22;

	public SshConfigItem() {
		
	}

	public SshConfigItem(String host, String username, String password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}
	
	public static SshConfigItem of(String options) {
		String host = OptionUtil.readString(options, "h");
		String username = OptionUtil.readString(options, "u");
		String password = OptionUtil.readString(options, "p");
		String portstr = OptionUtil.readString(options, "po");
		if(host != null && username != null) {
			SshConfigItem config = SshConfigItem.of(host, username, password);
			if(portstr != null) {
				config.setPort(Integer.parseInt(portstr));
			}
			return config;
		} else {
			return null;
		}
	}
	
	public static SshConfigItem of(String host, String username, String password) {
		return new SshConfigItem(host, username, password);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String toString() {
		return D.js(this, this.getClass());
	}
}
