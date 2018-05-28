package com.sirap.db.adjustor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.util.StrUtil;

public abstract class QuerySqlAdjustor {
	
	public String adjust(String sql) {
		String temp;

		if(StrUtil.equals(sql, DBKonstants.SHOW_CURRENT_SCHEMA_TABLES)) {
			temp = showCurrentSchemaTables();
			return temp;
		}
		
		if(StrUtil.equals(sql, DBKonstants.SHOW_USER_SCHEMA_TABLES)) {
			temp = showUserSchemaTables();
			return temp;
		}
		
		if(StrUtil.equals(sql, DBKonstants.SHOW_ALL_SCHEMA_TABLES)) {
			temp = showAllSchemaTables();
			return temp;
		}
		
		if(StrUtil.equals(sql, DBKonstants.SHOW_VARIABLES)) {
			temp = showVariables();
			return temp;
		}
		
		if(StrUtil.equals(sql, DBKonstants.SHOW_DATABASES)) {
			temp = showDatabases();
			return temp;
		}
		
		if(StrUtil.equals(sql, DBKonstants.SHOW_DATABASES_X)) {
			temp = showDatabasesX();
			return temp;
		}
		
		if(StrUtil.equals(sql, DBKonstants.SHOW_CURRENT_DATABASE)) {
			temp = showCurrentDatabase();
			return temp;
		}
		
		String regex = DBKonstants.TO_CREATE + "\\s+(|[^\\s]+)\\s+([^\\s]+)";
		String[] params = StrUtil.parseParams(regex, sql);
		if(params != null) {
			temp = showCreation(sql, params[0], params[1]);
			return temp;
		}
		
		regex = DBKonstants.SHOW_COLUMNS + "\\s+(.+?)";
		String param = StrUtil.parseParam(regex, sql);
		if(param != null) {
			temp = showColumns(param);
			return temp;
		}
		
		regex = DBKonstants.TO_CREATE + "\\s+([^\\s]+)";
		String singleParam = StrUtil.parseParam(regex, sql);
		if(singleParam != null) {
			String keyword = "table";
			temp = sql.replace(DBKonstants.TO_CREATE, DBKonstants.TO_CREATE + " " + keyword);
			temp = showCreation(temp, keyword, singleParam);
			

			return temp;
		}
		
		regex = "(\\s+" + DBKonstants.TO_LIMIT + "\\s+([\\d]{1,9})$)";
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
		
		regex = "(\\s+" + DBKonstants.TO_TOP + "\\s+([\\d]{1,9})$)";
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
	
	public String showCurrentSchemaTables() {
		StringBuffer sb = StrUtil.sb();
 		sb.append("select c.schema_name, a.table_name, a.table_rows, b.table_cols, a.table_type from (select database() SCHEMA_NAME) c, information_schema.tables a, ");
 		sb.append("(select table_name, count(1) TABLE_COLS from information_schema.columns group by table_name) b ");
 		sb.append("where a.table_schema = c.schema_name and a.table_name = b.table_name");
 		
 		return sb.toString();
	}
	
	public String showVariables() {
		return "show variables";
	}
	
	public String showAllSchemaTables() {
		StringBuffer sb = StrUtil.sb();
		sb.append("select concat(a.table_schema, '.', a.table_name) table_name, a.table_rows, b.table_cols, a.table_type from information_schema.tables a join (");
		sb.append("select table_name, count(1) TABLE_COLS from information_schema.columns group by table_name");
		sb.append(") b on a.table_name = b.table_name");
		
		return sb.toString();
	}
	
	public String showUserSchemaTables() {
		StringBuffer sb = StrUtil.sb();
		sb.append("select concat(a.table_schema, '.', a.table_name) table_name, a.table_rows, b.table_cols, a.table_type from information_schema.tables a join (");
		sb.append("select table_name, count(1) TABLE_COLS from information_schema.columns where table_schema {0} group by table_name");
		sb.append(") b on a.table_name = b.table_name");
		String gist = null;
		List<String> ins = Lists.newArrayList();
		for(String item : DBKonstants.MYSQL_GIVEN_SCHEMAS) {
			ins.add("'" + item + "'");
		}
		gist = StrUtil.occupy("not in ({0})", StrUtil.connectWithCommaSpace(ins));
		
		return StrUtil.occupy(sb.toString(), gist);
	}
	
	public String showDatabases() {
		return "show databases";
	}
	
	public String showDatabasesX() {
		return mysqlShowDatabasesX();
	}
	
	public String showCurrentDatabase() {
		return "select database() SCHEMA_NAME";
	}
	
	public String showCreation(String sql, String type, String name) {
		return sql;
	}
	
	public String showColumns(String tableInfo) {
		List<String> items = StrUtil.split(tableInfo, ".");
		String sql;
		StringBuffer sb = StrUtil.sb();
		String space = StrUtil.repeatSpace(4);
		sb.append("select concat(table_schema, '.', table_name, '{0}', group_concat(column_name separator ', ')) from information_schema.columns where table_name = '{1}'");
		if(items.size() == 2) {
			sb.append(" and table_schema = '{2}' group by table_schema");
			sql = StrUtil.occupy(sb.toString(), space, items.get(1), items.get(0));
		} else {
			sb.append(" group by table_schema");
			sql = StrUtil.occupy(sb.toString(), space, tableInfo);
		}
		
		return sql;
	}
	
	public String showLimit(String sql, String temp, String kkk) {
		return sql;
	}
	
	public String showTop(String sql, String temp, String kkk) {
		return sql;
	}
	
	public static String mysqlShowDatabasesX() {
		StringBuffer sb = StrUtil.sb();
		sb.append("select case when SCHEMA_NAME in ('information_schema', 'performance_schema', 'sys', 'mysql') then concat('* ', schema_name)");
		sb.append(" else SCHEMA_NAME end as 'SCHEMA_NAME', ");
		sb.append("DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME ");
		sb.append("from information_schema.SCHEMATA order by schema_name");
		
		return sb.toString();
	}
}
