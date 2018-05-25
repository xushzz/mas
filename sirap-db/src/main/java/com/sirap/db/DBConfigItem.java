package com.sirap.db;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;

public class DBConfigItem extends MexItem {
	
	private static final long serialVersionUID = 1L;

	private String itemName;
	private String url;
	private String username;
	private String password;

	public DBConfigItem() {
		
	}
	public DBConfigItem(String itemName) {
		this.itemName = itemName;
	}

	public DBConfigItem(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public boolean isValid() {
		boolean flag = StrUtil.startsWith(url, "jdbc:");
				
		return flag;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String getUrl() {
		return url;
	}

	public String getValidUrl() {
		String regex = "\\{\\s*\\d*\\s*\\}";
		String temp = url.replaceFirst(regex, "");
		
		return temp;
	}

	public void setUrl(String url) {
		this.url = url;
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
	
	@Override
	public String toString() {
		return "DBRecord [url=" + url + ", username=" + username + ", password=" + password + "]";
	}
	
	public String toPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(itemName + ".url=" + url).append("\n");
		String temp = password;
		if(EmptyUtil.isNullOrEmpty(password)) {
			temp = "[empty]";
		} else {
			temp = StrUtil.maskSome(password);
		}
		sb.append(itemName + ".who=" + username).append(",").append(temp);
		
		return sb.toString();
	}
}
