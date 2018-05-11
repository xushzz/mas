package com.sirap.basic.util;

import java.util.HashMap;
import java.util.Map;

public class DBUtil {
    
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
	
	public static String dbTypeOfUrl(String url) {
		if(StrUtil.contains(url, "Microsoft Access Driver")) {
			return DB_TYPE_ACCESS;
		} else if(StrUtil.contains(url, "microsoft:sqlserver")) {
			return DB_TYPE_SQLSERVER;
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
		String className = MAP_DB_TYPE_CLASS.get(dbType);

		String msg = StrUtil.occupy(":Unsupported database type [{0}], available: \n{1}", dbType, MAP_DB_TYPE_CLASS.keySet());
		XXXUtil.nullCheck(className, msg);
		
		return className;
	}
	

}
