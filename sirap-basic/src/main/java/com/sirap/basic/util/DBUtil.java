package com.sirap.basic.util;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.component.Konstants;

public class DBUtil {
    
	public static String dbTypeOfUrl(String url) {
		if(StrUtil.contains(url, "Microsoft Access Driver")) {
			return DBKonstants.DB_TYPE_ACCESS;
		} else if(StrUtil.contains(url, "microsoft:sqlserver")) {
			return DBKonstants.DB_TYPE_SQLSERVER;
		}
		
		String regex = "jdbc:([^:-]+).+";
		String param = StrUtil.parseParam(regex, url);
		
		return param;
	}
	
	public static String dbDriverOfUrl(String url) {
		String dbType = dbTypeOfUrl(url);
		return dbDriverOfType(dbType);
	}
	
	public static String dbDriverOfType(String dbType) {
		String className = DBKonstants.MAP_DB_TYPE_CLASS.get(dbType);

		String msg = StrUtil.occupy(":Unsupported database type [{0}], available: \n{1}", dbType, DBKonstants.MAP_DB_TYPE_CLASS.keySet());
		XXXUtil.nullCheck(className, msg);
		
		return className;
	}
	
	public static List<String> readSqls(String mixedSqls, boolean reduce) {
		List<String> lines = StrUtil.split(mixedSqls, ";");
		return readSqls(lines, reduce);
	}
	
	public static List<String> readSqlFile(String filepath, String charset, boolean reduce) {
		List<String> lines = IOUtil.readLines(filepath, charset);
		lines = CollUtil.filterSome(lines, Konstants.COMMENTS_START_WITH);
		String oneline = HtmlUtil.removeBlockComment(StrUtil.connectWithSpace(lines));
		return readSqls(StrUtil.split(oneline, ";"), reduce);
	}
	
	public static List<String> readSqls(List<String> lines, boolean reduce) {
		List<String> sqls = Lists.newArrayList();
		for(String line : lines) {
			if(EmptyUtil.isNullOrEmptyOrBlankOrLiterallyNull(line)) {
				continue;
			}
			if(reduce) {
				line = StrUtil.reduceMultipleSpacesToOne(line).trim();
			}
			sqls.add(line);
		}
		
		return sqls;
	}
	
	public static String mysqlShowDatabasesX() {
		StringBuffer sb = StrUtil.sb();
		sb.append("select case when SCHEMA_NAME in ('information_schema', 'performance_schema', 'sys', 'mysql') then concat('*', schema_name)");
		sb.append(" else SCHEMA_NAME end as 'SCHEMA_NAME', ");
		sb.append("DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME ");
		sb.append("from information_schema.SCHEMATA order by schema_name");
		
		return sb.toString();
	}
}
