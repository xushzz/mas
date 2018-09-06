package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.db.DBManager;
import com.sirap.basic.db.QueryWatcher;
import com.sirap.basic.db.resultset.ResultSetMingAnalyzer;
import com.sirap.basic.exception.MexException;

public class DBUtil {

	public static final String[] KEYS_DML_ARRAY;
	public static final List<String> KEYS_DML_LIST;

	public static final String KEYS_QUERY = "select;show;desc;describe;from;count;cnt;explain";
	public static final String SQL_RESERVED_WORDS;
	static {
		StringBuilder sb = new StringBuilder();
		sb.append(KEYS_QUERY).append(";");
		sb.append("insert;delete;update").append(";");
		sb.append("into").append(";");
		sb.append("create;drop;alter;call;truncate");
		SQL_RESERVED_WORDS = sb.toString();		
	}

	/**
	 * from A => select * from A
	 * into B => insert into B
	 * desc C => desc C
	 * 
	 */
	private static final Map<String, String> FULL_WORDS;
	static {
		FULL_WORDS = new HashMap<>();
		FULL_WORDS.put("from", "select * from");
		FULL_WORDS.put("into", "insert into");
		FULL_WORDS.put("count", "select count(*) as Count from");
		FULL_WORDS.put("cnt", "select count(*) as Count from");
		
		KEYS_DML_LIST = new ArrayList<>(FULL_WORDS.keySet());
		
		KEYS_DML_ARRAY = new String[KEYS_DML_LIST.size()];
		KEYS_DML_LIST.toArray(KEYS_DML_ARRAY);
	}
    
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
	
	public static List<String> readSqlFile(String filepath, String charset, boolean reduce, boolean fromSecondLine) {
		List<String> lines = IOUtil.readLines(filepath, charset);
		if(fromSecondLine && lines.size() > 1) {
			lines.remove(0);
		}
		lines = Colls.filterSome(lines, Konstants.COMMENTS_START_WITH);
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
	
	public static String rephrase(String[] params) throws MexException {
		String keyword = params[0];
		String others = params[1];
		
		String[] words = others.split(" ");
		String table = words[0];
		
		keyword = keyword.toLowerCase();
		String leadWord = keyword;
		if(FULL_WORDS.containsKey(keyword)) {
			if(takeAsColumnOrTableName(table)) {
				
				leadWord = FULL_WORDS.get(keyword);
			} else {
				throw new MexException("[" + table + "] is not a table name, is it?");
			}
		}
		
		String value = leadWord + " " + others;
		
		return value;
	}
	
	public static boolean takeAsColumnOrTableName(String source) {
		String regex = "^[$\\.a-z0-9_]+$";
		boolean flag = StrUtil.isRegexMatched(regex, source);
		if(!flag) {
			return false;
		}
		
		flag = StrUtil.isDigitsOnly(source);
		if(flag) {
			return false;
		}
		
		Matcher m = Pattern.compile("[A-Za-z]{1,64}").matcher(source);
		if(!m.find()) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<List> queryRawList(String url, String username, String password, String sql) {
		DBManager liu = DBManager.g(url, username, password);
		boolean fix = true;
		String tempSql = sql;
		if(fix) {
			String keyWords = SQL_RESERVED_WORDS.replaceAll(";", "|");
			String[] params = StrUtil.parseParams("(" + keyWords + ")\\s+(.+)", tempSql);
			if(params != null) {
				tempSql = rephrase(params);
			}
		}
		QueryWatcher yan = liu.query(new ResultSetMingAnalyzer(), tempSql, true, false);
		
		return yan.getRecords();
	}
}
