package com.sirap.basic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
    
    public Connection createConnection() {
    	try {
			conn = DriverManager.getConnection(url, username, password);
			return conn;
		} catch (SQLException ex) {
			throw new MexException(ex); 
		}
    }
    
    public ResultSet executeQuery(String sql) throws Exception {
    	stmt = createConnection().createStatement();
    	result = stmt.executeQuery(sql);
    	
    	return result;
    }
    
    public int executeUpdate(String sql) throws Exception {
    	stmt = createConnection().createStatement();
    	int affectedRows = stmt.executeUpdate(sql);
    	
    	return affectedRows;
    }
    
    public int[] executeBatch(List<String> sqls) throws Exception {
    	createConnection();
    	conn.setAutoCommit(false);
    	stmt = conn.createStatement();
    	for(String sql : sqls) {
    		stmt.addBatch(sql);
    	}

    	int[] result = stmt.executeBatch();
    	conn.commit();

    	return result;
    }
  
    public void closeUp() {  
        try {
        	if(result != null) {
                result.close();
        	}
        	if(stmt != null) {
                stmt.close();
        	}
        	if(conn != null) {
                conn.close();
        	}
        } catch (SQLException e) {  
            e.printStackTrace();  
        }  
    }
    
    public void closeConnection() {  
    	try {
    		if(conn != null) {
                conn.close();
        	}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
    }

	public String getUrl() {
		return url;
	}
}  