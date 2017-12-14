package com.sirap.db.adjustor;

import java.util.regex.Matcher;

import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

public class QueryAdjustorSqlServer extends QuerySqlAdjustor {

	@Override
	public String showTables(String schema) {
		String temp = showTables();
		return temp;
	}
	
	@Override
	public String showTables() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT a.name 'TABLE_NAME', b.rows 'TABLE_ROWS' FROM sysobjects AS a");
		sb.append(" INNER JOIN sysindexes AS b ON a.id = b.id");
		sb.append(" WHERE (a.type = 'u') AND (b.indid IN (0, 1)) ORDER BY a.name,b.rows DESC");
		return sb.toString();
	}

	@Override
	public String showDatabases() {
		String temp = "SELECT Name 'SCHEMA_NAME' FROM Master..SysDatabases ORDER BY Name";
		return temp;
	}

	/***
	 * select dbms_metadata.get_ddl('TABLE','TABLE_NAME') from dual;
	 * select dbms_metadata.get_ddl('INDEX','INDEX_NAME') from dual;
	 */
	@Override
	public String showCreation(String sql, String type, String name) {
		String tempQuery = "select to_char(substr(dbms_metadata.get_ddl( '{0}', '{1}'), 1, 10000)) from dual";
		String querySql = StrUtil.occupy(tempQuery, type.toUpperCase(), name);
		
		return querySql;
	}

	@Override
	public String showLimit(String sql, String limitKKK, String kkk) {
		
		String regex = "^select"; 
		Matcher ma = StrUtil.createMatcher(regex, sql);
		String temp = sql.replace(limitKKK, "");
		if(ma.find()) {
			String value = ma.group();
			temp = temp.replace(value, value + " top " + kkk);
		} else {
			XXXUtil.alert("There is not a select sentence: " + sql);
		}
		
		return temp;
	}

	@Override
	public String showTop(String sql, String temp, String kkk) {
		return showLimit(sql, temp, kkk);
	}
}
