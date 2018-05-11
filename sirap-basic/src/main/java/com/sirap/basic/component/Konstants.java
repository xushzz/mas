package com.sirap.basic.component;

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

	public static final String OS_MAC = "Mac";
	public static final String OS_WINDOWS = "Windows";
	public static final String OS_LINUX = "Linux";
	public static final String OS_UNIX = "Unix";

	public static final String FILE_SEPARATOR_WINDOWS = "\\";
	public static final String FILE_SEPARATOR_UNIX = "/";	
}
