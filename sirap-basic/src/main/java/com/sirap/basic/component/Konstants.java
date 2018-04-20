package com.sirap.basic.component;

import java.util.HashMap;
import java.util.Map;

public class Konstants {
	public static final int TIME_STEP = 60;
	public static final int MILLI_PER_SECOND = 1000;
	public static final int MILLI_PER_MINUTE = TIME_STEP * MILLI_PER_SECOND;
    public static final int MILLI_PER_HOUR   = TIME_STEP * MILLI_PER_MINUTE;
    public static final int MILLI_PER_DAY    = 24 * MILLI_PER_HOUR;
    
    public static final int FILE_SIZE_STEP = 1024;
	public static final String FILE_SIZE_UNIT = "BKMGTPE";
	public static final String REGEX_FLOAT = "(\\d+|\\d+\\.\\d*|\\d*\\.\\d+)";
	public static final String REGEX_SIGN_FLOAT = "(-?\\d+|-?\\d+\\.\\d*|-?\\d*\\.\\d+)";
	public static final String REGEX_JAVA_IDENTIFIER = "[a-zA-Z_$][\\da-zA-Z_$]*";

	public static final String FOLDER_EXPORT = "exp";
	public static final String FOLDER_IMG = "img";
	public static final String FOLDER_SOGOU = "sogou";
	public static final String FOLDER_YOUDAO = "youdao";
	public static final String FOLDER_MISC = "misc";
	public static final String FOLDER_SCREENSHOT = "shot";
	public static final String FOLDER_HISTORY = "log";
	public static final String FOLDER_REMOTE = "remote";
	public static final String DOT_BAT = ".bat";
	public static final String DOT_TXT = ".txt";
	public static final String DOT_PDF = ".pdf";
	public static final String DOT_HTM = ".htm";
	public static final String DOT_EXCEL = ".xls";
	public static final String DOT_CSV = ".csv";
	public static final String DOT_EXCEL_X = ".xlsx";
	public static final String DOT_MEX = ".mex";
	public static final String DOT_SIRAP = ".sirap";
	public static final String DOT_JPG = ".jpg";
	public static final String DOT_CLASS = ".class";
	public static final String DOT_JAVA = ".java";

	public static final String SHITED_FACE = "^=^";
	public static final String HYPHEN = "-";
	public static final String NEWLINE = "\n";
	
	public static final char BEEP = 7;
	
	public static final String[] DISTRIBUTION_KEYS_TBD = {"-", "/", "TBD"};

	public static final String CODE_GBK = "GBK";
	public static final String CODE_GB2312 = "GB2312";
    public static final String CODE_UTF8 = "UTF-8";
    public static final String CODE_UNICODE = "Unicode";

	public static final String FLAG_YES = "Y";
	public static final String FLAG_NO = "N";
	public static final String IMG_JPG = "jpg";
	public static final String IMG_BMP = "bmp";
    public static final String IMG_FORMATS = "png,jpg,jpeg,bmp,gif";
    public static final String KEY_ALL = "all";
    public static final String KEY_DOT_AS_CRITERIA = ".";
    
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

	public static final String OS_MAC = "Mac";
	public static final String OS_WINDOWS = "Windows";
	public static final String OS_LINUX = "Linux";
	public static final String OS_UNIX = "Unix";

	public static final String FILE_SEPARATOR_WINDOWS = "\\";
	public static final String FILE_SEPARATOR_UNIX = "/";	
}
