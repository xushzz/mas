package com.sirap.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.db.SirapDAO;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.db.adjustor.QuerySqlAdjustor;

public class DBManager extends SirapDAO {
	
	private static DBManager instance;

	public static DBManager g() {
		DBConfigItem db = DBHelper.getActiveDB();

		String url = db.getValidUrl();
	    String username = db.getUsername();    
	    String password = db.getPassword();

	    instance = new DBManager(url, username, password);
	    
		return instance;
	}
	
	public static DBManager g2(String url, String username, String password) {
		instance = new DBManager(url, username, password);
		
		return instance;
	}

	public DBManager(String url, String username, String password) {
		super(url, username, password);
	}
	
	public int update(String sql) {
		try {
			int result = executeUpdate(sql);
			closeStuff();
			
			return result;
			
		} catch (Exception ex) {
            throw new MexException(ex);
        } 
	}
	
	public QueryWatcher query(String sql) {
		return query(sql, false, false);
	}
	
	public QueryWatcher query(String sql,  boolean toAdjust, boolean printSql) {
		QueryWatcher ming = new QueryWatcher();
		
		try {
			
			String tempSql = sql;
			if(toAdjust) {
				String dbType = StrUtil.parseDbTypeByUrl(getUrl());
				QuerySqlAdjustor zhihui = DBFactory.getQuerySqlAdjustor(dbType);
				if(zhihui != null) {
					String schema = DBFactory.getSchemaNameParser(dbType).parseSchema(getUrl());
					tempSql = zhihui.adjust(tempSql, schema);
				} else {
					String msg = "Not yet supported database type: " + dbType;
					XXXUtil.alert(msg);
				}
			}
			
			if(printSql) {
				C.pl("fetching... " + tempSql);
			}
			
			ResultSet result = executeQuery(tempSql);
	    	ResultSetMetaData meta = result.getMetaData();
	    	int count = meta.getColumnCount();
	    	
	    	if(true) {
	    		List<String> items = new ArrayList<>();
		    	for(int i = 1; i <= count; i++) {
		    		items.add(meta.getColumnName(i));
		    	}
		    	ming.setColumnNames(items);
	    	}
	    	
	    	List<List<Object>> records = new ArrayList<>(); 
	        while (result.next()) {
	        	List<Object> items = new ArrayList<>();
	        	for(int i = 1; i <= count; i++) {
	        		items.add(result.getObject(i));
	        	}
	        	records.add(items);
	        }
	        ming.setRecords(records);
	        
	        closeStuff();
		} catch (Exception ex) {  
            throw new MexException(ex);
        } 
		
        return ming;
	}
}
