package com.sirap.db.adjustor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.util.StrUtil;

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
		String temp = DBKonstants.SHOW_TABLES;
		
		return temp;
	}
	
	public String showTables(String schema) {
		StringBuffer sb = StrUtil.sb();
		sb.append("select a.table_schema, a.table_name, b.table_cols from information_schema.tables a join (");
		sb.append("select table_name, count(1) TABLE_COLS from information_schema.columns where table_schema {0} group by table_name");
		sb.append(") b on a.table_name = b.table_name");
		String gist = null;
		if(schema != null) {
			gist = StrUtil.occupy("= '{0}'", schema);
		} else {
			List<String> ins = Lists.newArrayList();
			for(String item : DBKonstants.MYSQL_SCHEMAS) {
				ins.add("'" + item + "'");
			}
			gist = StrUtil.occupy("not in ({0})", StrUtil.connectWithCommaSpace(ins));
		}
		
		return StrUtil.occupy(sb.toString(), gist);
	}
	
	public String showDatabases() {
		return DBKonstants.SHOW_DATABASES;
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
