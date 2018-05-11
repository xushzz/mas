package com.sirap.basic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.DBUtil;

public class SirapDAO {

    private String url;
    private String username;    
    private String password;
  
    private Connection conn;
    private Statement stmt;
    private ResultSet result;  
  
    public SirapDAO(String url, String username, String password) {  
        try {
        	this.url = url;
        	this.username = username;
        	this.password = password;
        	
        	String driver = DBUtil.dbDriverOfUrl(url);
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

	public String getUrl() {
		return url;
	}
}  