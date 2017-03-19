package com.sirap.db.adjustor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;

public class QueryAdjustorOracle extends QuerySqlAdjustor {

	@Override
	public String showTables(String schema) {
		String temp = showTables();
		return temp;
	}
	
	@Override
	public String showTables() {
		String temp = "select table_name, num_rows from user_tables";
		return temp;
	}

	@Override
	public String showDatabases() {
		String temp = "select username from dba_users";
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
		
		String regex = "\\s+where\\s+"; 
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(sql);
		String temp = sql.replace(limitKKK, "");
		if(m.find()) {
			String value = m.group();
			temp = temp.replace(value, " where rownum <= " + kkk + " and ");
		} else {
			temp += " where rownum <= " + kkk;
		}
		
		return temp;
	}

	@Override
	public String showTop(String sql, String temp, String kkk) {
		return showLimit(sql, temp, kkk);
	}
	
	
	
}
