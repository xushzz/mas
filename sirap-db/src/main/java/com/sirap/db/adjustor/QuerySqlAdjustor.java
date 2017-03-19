package com.sirap.db.adjustor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;
import com.sirap.db.DBKonstants;


public abstract class QuerySqlAdjustor {
	
	public String adjust(String sql) {
		return adjust(sql, null);
	}

	public String adjust(String sql, String schema) {
		String temp;
		
		if(StrUtil.startsWith(sql, DBKonstants.SHOW_TABLES)) {
			temp = showTables(schema);
			return temp;
		}
		
		if(StrUtil.startsWith(sql, DBKonstants.SHOW_DATABASES)) {
			temp = showDatabases();
			return temp;
		}
		
		String regex = DBKonstants.SHOW_CREATE + "\\s+(|[^\\s]+)\\s+([^\\s]+)";
		String[] params = StrUtil.parseParams(regex, sql);
		if(params != null) {
			temp = showCreation(sql, params[0], params[1]);
			return temp;
		}
		
		regex = DBKonstants.SHOW_CREATE + "\\s+([^\\s]+)";
		String singleParam = StrUtil.parseParam(regex, sql);
		if(singleParam != null) {
			String keyword = "table";
			temp = sql.replace(DBKonstants.SHOW_CREATE, DBKonstants.SHOW_CREATE + " " + keyword);
			temp = showCreation(temp, keyword, singleParam);
			

			return temp;
		}
		
		regex = "(\\s+" + DBKonstants.SHOW_LIMIT + "\\s+([\\d]{1,9})$)";
		Matcher auntie = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(sql);
		String tempLimit = null;
		String kkk = null;
		while(auntie.find()) {
			tempLimit = auntie.group(1);
			kkk = auntie.group(2);
		}
		
		if(tempLimit != null) {
			temp = showLimit(sql, tempLimit, kkk);
			return temp;
		}
		
		regex = "(\\s+" + DBKonstants.SHOW_TOP + "\\s+([\\d]{1,9})$)";
		auntie = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(sql);
		tempLimit = null;
		kkk = null;
		while(auntie.find()) {
			tempLimit = auntie.group(1);
			kkk = auntie.group(2);
		}
		
		if(tempLimit != null) {
			temp = showTop(sql, tempLimit, kkk);
			return temp;
		}
		
		return sql;
	}
	
	public String showTables() {
		String temp = "show tables";
		
		return temp;
	}
	
	public String showTables(String schema) {
		String temp = null;
		
		if(schema != null) {
			temp = "select table_name, table_rows from information_schema.tables where TABLE_SCHEMA = '" + schema + "'";
		} else {
			temp = "show tables";
		}
		
		return temp;
	}

	public String showDatabases() {
		String temp = "show databases";
		return temp;
	}
	
	public String showCreation(String sql, String type, String name) {
		return sql;
	}
	
	public String showLimit(String sql, String temp, String kkk) {
		return sql;
	}
	
	public String showTop(String sql, String temp, String kkk) {
		return sql;
	}
}
