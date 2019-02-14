package com.sirap.basic.component;

import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XCodeUtil;

public class Konstants {
	public static final int TIME_STEP = 60;
	public static final int MILLI_PER_SECOND = 1000;
	public static final int MILLI_PER_MINUTE = TIME_STEP * MILLI_PER_SECOND;
    public static final int MILLI_PER_HOUR   = TIME_STEP * MILLI_PER_MINUTE;
    public static final int MILLI_PER_DAY    = 24 * MILLI_PER_HOUR;
    
	public static final int SECONDS_PER_MINUTE = TIME_STEP * 1;
    public static final int SECONDS_PER_HOUR   = TIME_STEP * SECONDS_PER_MINUTE;
    public static final int SECONDS_PER_DAY    = 24 * SECONDS_PER_HOUR;

    public static final String CHINESE_YEAR_NIAN = XCodeUtil.urlDecodeUTF8("%E5%B9%B4");
    public static final String CHINESE_MONTH_YUE = XCodeUtil.urlDecodeUTF8("%E6%9C%88");
    public static final String CHINESE_DAY_TIAN = XCodeUtil.urlDecodeUTF8("%E5%A4%A9");
    public static final String CHINESE_HOUR_XIAOSHI = XCodeUtil.urlDecodeUTF8("%E5%B0%8F%E6%97%B6");
    public static final String CHINESE_MINUTE_FENZHONG = XCodeUtil.urlDecodeUTF8("%E5%88%86%E9%92%9F");
    public static final String CHINESE_MINUTE_FEN = XCodeUtil.urlDecodeUTF8("%E5%88%86");
    public static final String CHINESE_SECOND_MIAO = XCodeUtil.urlDecodeUTF8("%E7%A7%92");
    
    public static final int FILE_SIZE_STEP = 1024;
	public static final String FILE_SIZE_UNIT = "BKMGTPE";
	public static final String REGEX_NUMBER = "-?((0|[1-9]\\d*)\\.\\d+|(0|[1-9]\\d*))(|[eE](\\+?|\\-)\\d+)";
	public static final String REGEX_FLOAT = "(\\d+|\\d+\\.\\d*|\\d*\\.\\d+)";
	public static final String REGEX_SIGN_FLOAT = "(-?\\d+|-?\\d+\\.\\d*|-?\\d*\\.\\d+)";
	public static final String REGEX_JAVA_IDENTIFIER = "[a-zA-Z_$][\\da-zA-Z_$]*";
	public static final String REGEX_IP;
	static {
		String digit = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		REGEX_IP = StrUtil.repeat(digit + "\\.", 3) + digit;	
	}

	public static final String KEY_CONTENT_DISPOSITION = "Content-Disposition";
	public static final String KEY_CONTENT_TYPE_APPLICATION = "application";
	public static final String KEY_CONTENT_LENGTH = "Content-Length";
	public static final String KEY_CONTENT_TYPE = "Content-Type";
	public static final String KEY_AGE = "Age";
	public static final String KEY_LOCATION = "Location";
	public static final String FOLDER_EXPORT = "exp";
	public static final String FOLDER_DATA = "data";
	public static final String FOLDER_IMG = "img";
	public static final String FOLDER_SOGOU = "sogou";
	public static final String FOLDER_YOUDAO = "youdao";
	public static final String FOLDER_MISC = "misc";
	public static final String FOLDER_SCREENSHOT = "shot";
	public static final String FOLDER_HISTORY = "loghere";
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
	public static final String DOT_JAR = ".jar";
	public static final String JAVA_DOT_CLASS_DOT_PATH = "java.class.path";
	public static final String JAVA_DOT_LIBRARY_DOT_PATH = "java.library.path";

	//á 225 aacute, small a, acute accent
	public static final String FAKED_NULL = "NULL";
	public static final String FAKED_SPACE = "" + (char)225;
	public static final String FAKED_EMPTY = "[EMPTY]";
	public static final String SHITED_FACE = "^=^";
	public static final String HYPHEN = "-";
	public static final String NEWLINE = "\n";
	public static final String NEWLINE_SHORT = "NL";
	
	public static final char BEEP = 7;
	
	public static final String[] DISTRIBUTION_KEYS_TBD = {"-", "/", "TBD"};

	public static final String CODE_GBK = "GBK";
	public static final String CODE_GB2312 = "GB2312";
    public static final String CODE_ISO88591 = "ISO-8859-1";
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
	
	public static final String COMMENT_2HYPENS = "--";
	public static final String COMMENT_POUND = "#";
	public static final String COMMENT_2SLASHES = "//";
	public static final String COMMENT_REGEX_HTML = "<!--.*?-->";
	public static final String COMMENT_REGEX_JAVA = "/\\*.*?\\*/";
	public static final List<String> COMMENTS_REGEX = Lists.newArrayList(COMMENT_REGEX_HTML, COMMENT_REGEX_JAVA);
	public static final List<String> COMMENTS_START_WITH = Lists.newArrayList(COMMENT_2HYPENS, COMMENT_2SLASHES, COMMENT_POUND);

	public static final List<Class<?>> PRIMITIVE_ARRAY_CLASSES = Lists.newArrayList();
	static {
		PRIMITIVE_ARRAY_CLASSES.add(byte[].class);
		PRIMITIVE_ARRAY_CLASSES.add(short[].class);
		PRIMITIVE_ARRAY_CLASSES.add(int[].class);
		PRIMITIVE_ARRAY_CLASSES.add(long[].class);
		PRIMITIVE_ARRAY_CLASSES.add(float[].class);
		PRIMITIVE_ARRAY_CLASSES.add(double[].class);
		PRIMITIVE_ARRAY_CLASSES.add(char[].class);
		PRIMITIVE_ARRAY_CLASSES.add(boolean[].class);
	}

	public static final List<String> PRIMITIVE_ARRAY_SIMPLENAMES = Lists.newArrayList();
	static {
		PRIMITIVE_ARRAY_CLASSES.stream().forEach(item -> PRIMITIVE_ARRAY_SIMPLENAMES.add(item.getName() + " " + item.getSimpleName()));
	}
}
