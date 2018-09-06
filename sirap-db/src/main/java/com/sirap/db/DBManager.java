package com.sirap.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.sirap.basic.db.SirapDAO;
import com.sirap.basic.domain.KeyValuesItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.db.adjustor.QuerySqlAdjustor;
import com.sirap.db.resultset.ResultSetAnalyzer;
import com.sirap.db.resultset.ResultSetKeyValuesAnalyer;
import com.sirap.db.resultset.ResultSetObjectMatrixAnalyer;
import com.sirap.db.resultset.ResultSetStringMatrixAnalyer;

public class DBManager extends SirapDAO {
	
	private static DBManager instance;
	
	public static DBManager g(String url, String username, String password) {
		instance = new DBManager(url, username, password);
		
		return instance;
	}

	public DBManager(String url, String username, String password) {
		super(url, username, password);
	}
	
	public boolean isAvailable() {
		Connection conn = createConnection();
		try {
			conn.close();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return false;
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
			closeUp();
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
        	closeUp();
        }
	}
	
	public <T extends Object> T query(ResultSetAnalyzer<T> rest, String sql) {
		return query(rest, sql, false, false);
	}
	
	public <T extends Object> T query(ResultSetAnalyzer<T> rest, String sql,  boolean toAdjust, boolean printSql) {
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
			return rest.analyze(result);
		} catch (Exception ex) {  
//			ex.printStackTrace();
            throw new MexException(ex);
        } finally {
	        closeUp();
        }
	}
	
	public List<List<Object>> queryAsObjectMatrix(String sql, boolean getColumn) {
//		D.ts("queryAsObjectMatrix...");
		return queryAsObjectMatrix(sql, getColumn, false, false);
	}
	
	public List<List<Object>> queryAsObjectMatrix(String sql, boolean getColumn, boolean toAdjust, boolean printSql) {
		ResultSetAnalyzer<List<List<Object>>> rest = new ResultSetObjectMatrixAnalyer(getColumn);
		return query(rest, sql, toAdjust, printSql);
	}
	
	public List<List<String>> queryAsStringMatrix(String sql, boolean getColumn) {
//		D.ts("queryAsStringMatrix...");
		return queryAsStringMatrix(sql, getColumn, false, false);
	}
	
	public List<List<String>> queryAsStringMatrix(String sql, boolean getColumn, boolean toAdjust, boolean printSql) {
		ResultSetAnalyzer<List<List<String>>> rest = new ResultSetStringMatrixAnalyer(getColumn);
		return query(rest, sql, toAdjust, printSql);
	}
	
	public List<KeyValuesItem> queryAsKeyValues(String sql) {
//		D.ts("queryAsStringMatrix...");
		return queryAsKeyValues(sql, false, false);
	}
	
	public List<KeyValuesItem> queryAsKeyValues(String sql, boolean toAdjust, boolean printSql) {
		ResultSetAnalyzer<List<KeyValuesItem>> rest = new ResultSetKeyValuesAnalyer();
		return query(rest, sql, toAdjust, printSql);
	}
}
