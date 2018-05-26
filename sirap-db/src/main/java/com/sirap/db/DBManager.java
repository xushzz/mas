package com.sirap.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.db.SirapDAO;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.db.adjustor.QuerySqlAdjustor;

public class DBManager extends SirapDAO {
	
	private static DBManager instance;
	
	public static DBManager g(String url, String username, String password) {
		instance = new DBManager(url, username, password);
		
		return instance;
	}

	public DBManager(String url, String username, String password) {
		super(url, username, password);
	}
	
	public int update(String sql, boolean printSql) {
		try {
			if(printSql) {
				C.pl("updating... " + sql + " " + instance.getUrl());
			}
			int result = executeUpdate(sql);
			
			return result;
			
		} catch (Exception ex) {
//			ex.printStackTrace();
            throw new MexException(ex);
        } finally {
			closeTrio();
        }
	}
	
	public int[] batch(List<String> sqls, boolean printSql) {
		try {
			if(printSql) {
				int some = 9;
				C.pl(StrUtil.occupy("batching... {0} sqls on {1}:", sqls.size(), instance.getUrl()));
				C.listSome(sqls, some, true);
			}
			int[] result = executeBatch(sqls);

			return result;
		} catch (Exception ex) {
//			ex.printStackTrace();
            throw new MexException(ex);
        } finally {
        	closeTrio();
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
				String dbType = DBUtil.dbTypeOfUrl(getUrl());
				QuerySqlAdjustor zhihui = DBFactory.getQuerySqlAdjustor(dbType);
				if(zhihui != null) {
					tempSql = zhihui.adjust(tempSql);
				} else {
					String msg = "Not yet supported database type: " + dbType;
					XXXUtil.alert(msg);
				}
			}
			
			if(printSql) {
				C.pl("querying... " + tempSql + " " + instance.getUrl());
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
		} catch (Exception ex) {  
//			ex.printStackTrace();
            throw new MexException(ex);
        } finally {
	        closeTrio();
        }
		
        return ming;
	}
}
