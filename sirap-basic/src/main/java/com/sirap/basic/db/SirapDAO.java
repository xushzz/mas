package com.sirap.basic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.StrUtil;

public class SirapDAO {

    private String url;
    private String driver;
    private String username;    
    private String password;
    private String schema;
  
    private Connection conn;
    private Statement stmt;
    private ResultSet result;  
  
    public SirapDAO(String url, String username, String password) {  
        try {
        	this.url = url;
        	this.driver = StrUtil.getDbDriverByUrl(url);
        	this.username = username;
        	this.password = password;
        	
            Class.forName(driver);
        } catch (Exception ex) {  
            throw new MexException(ex); 
        }  
    }
    
    protected Connection createConnection() {
    	try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException ex) {
			throw new MexException(ex); 
		}
    	
    	return null;
    }
    
    protected ResultSet executeQuery(String sql) throws Exception {
    	createConnection();
    	stmt = conn.createStatement();
    	result = stmt.executeQuery(sql);
    	
    	return result;
    }
    
    protected int executeUpdate(String sql) throws Exception {
    	createConnection();
    	stmt = conn.createStatement();
    	int affectedRows = stmt.executeUpdate(sql);
    	
    	return affectedRows;
    }
  
    protected void closeStuff() {  
        try {
        	if(result != null) {
                result.close();
        	}
            stmt.close();
            conn.close();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}

	public String getUrl() {
		return url;
	}
}  