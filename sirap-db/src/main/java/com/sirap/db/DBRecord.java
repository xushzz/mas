package com.sirap.db;

import com.sirap.basic.domain.MexItem;
import com.sirap.basic.util.StrUtil;

public class DBRecord extends MexItem {
	
	private static final long serialVersionUID = 1L;

	private String dbName;
	private String url;
	private String schema;
	private String username;
	private String password;

	public DBRecord() {
		
	}
	public DBRecord(String dbName) {
		this.dbName = dbName;
	}

	public DBRecord(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public String getValidSchema() {
		if(schema != null && !schema.contains("{")) {
			return schema;
		}
		
		return null;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public boolean isValid() {
		boolean flag = StrUtil.startsWith(url, "jdbc:");
		if(!flag) {
			return false;
		}
		
		String dbType = StrUtil.parseDbTypeByUrl(url);		
		schema = DBFactory.getSchemaNameParser(dbType).parseSchema(url);
		
		return true;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
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
		sb.append(dbName + ".url=" + url).append("\n");
		sb.append(dbName + ".who=" + username).append(",").append(password);
		
		return sb.toString();
	}
}
