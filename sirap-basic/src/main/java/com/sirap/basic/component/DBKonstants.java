package com.sirap.basic.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.util.StrUtil;

public class DBKonstants {
	public static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	public static final String DRIVER_ORACLE = "oracle.jdbc.OracleDriver";
	public static final String DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver";
	public static final String DRIVER_SQLSERVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String DRIVER_POSTGRE = "org.postgresql.Driver";
	public static final String DRIVER_SYBASE = "com.sybase.JDBC.SybDriver";
	public static final String DRIVER_INFORMIX = "com.informix.JDBC.ifxDriver";
	public static final String DRIVER_ACCESS = "sun.jdbc.odbc.JdbcOdbcDriver";
	public static final String DRIVER_DERBY = "org.apache.derby.jdbc.ClientDriver";

	public static final String DB_TYPE_MYSQL = "mysql";
	public static final String DB_TYPE_ORACLE = "oracle";
	public static final String DB_TYPE_DB2 = "db2";
	public static final String DB_TYPE_SQLSERVER = "sqlserver";
	public static final String DB_TYPE_POSTGRE = "postgresql";
	public static final String DB_TYPE_SYBASE = "sybase";
	public static final String DB_TYPE_INFORMIX = "informix";
	public static final String DB_TYPE_ACCESS = "access";
	public static final String DB_TYPE_DERBY = "derby";
	
	public static final String SHOW_TABLES = "show tables";
	public static final String SHOW_DATABASES = "show databases";
	public static final String SHOW_CREATE = "show create";
	public static final String SHOW_LIMIT = "limit";
	public static final String SHOW_TOP = "top";

	public static String SQL_SHOW_TABLES = "show tables";
	public static String SQL_SHOW_DATABSES = "show databases";
	
	public static final Map<String, String> MAP_DB_TYPE_CLASS = new HashMap<String, String>();
	static {
		MAP_DB_TYPE_CLASS.put(DB_TYPE_MYSQL, DRIVER_MYSQL);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_ORACLE, DRIVER_ORACLE);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_DB2, DRIVER_DB2);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_SQLSERVER, DRIVER_SQLSERVER);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_POSTGRE, DRIVER_POSTGRE);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_SYBASE, DRIVER_SYBASE);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_INFORMIX, DRIVER_INFORMIX);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_ACCESS, DRIVER_ACCESS);
		MAP_DB_TYPE_CLASS.put(DB_TYPE_DERBY, DRIVER_ACCESS);
	}
	
	public static final List<String> MYSQL_SCHEMAS = StrUtil.split("information_schema,performance_schema,sys,mysql");
}
