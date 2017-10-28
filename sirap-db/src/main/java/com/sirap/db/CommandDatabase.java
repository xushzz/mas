package com.sirap.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.Stash;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.TargetExcel;
import com.sirap.db.parser.ConfigItemParser;
import com.sirap.db.parser.ConfigItemParserMySQL;

public class CommandDatabase extends CommandBase {

	private static final String KEY_SQL = "!";
	private static final String KEY_TABLE = "t";
	private static final String KEY_DATABASE = "db";
	private static final String KEY_SCHEMA = "sma";
	private static final String KEY_SHOW_TABLES = "tbs";
	private static final String KEY_SHOW_DATABSES = "dbs";
	private static final String KEY_MYSQL = "mysql";

	public static final String SQL_MAX_SIZE_DEFAULT = "10M";
	public static final String SQL_MAX_SIZE_KEY = "sql.max";
	
	{
		helpMeanings.put("sql.max", SQL_MAX_SIZE_DEFAULT);
		helpMeanings.put("sql.max.key", SQL_MAX_SIZE_KEY);
	}
	
	@Override
	public boolean handle() {
		InputAnalyzer sean = new SqlInputAnalyzer(input);
		solo = StrUtil.parseParam(KEY_SQL + "(.{4,})", sean.getCommand());
		if(solo != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			File file = parseFile(solo);
			if(file != null) {
				if(FileOpener.isTextFile(file.getAbsolutePath())) {
					checkTooBigToHandle(file, g().getUserValueOf(SQL_MAX_SIZE_KEY, SQL_MAX_SIZE_DEFAULT));
					String filePath = file.getAbsolutePath();
					String charset = g().getCharsetInUse();
					String temp = IOUtil.readFileWithLineSeparator(filePath, " ", charset, "--");
					String sql = StrUtil.reduceMultipleSpacesToOne(temp).trim();
					dealWith(sql);
				}
			} else {
				dealWith(solo);
			}
			
			return true;
		}
		
		if(is(KEY_SHOW_DATABSES)) {
			String sql = DBKonstants.SHOW_DATABASES;
			QueryWatcher ming = query(sql);
			export(ming.exportLiteralStrings());
			
			return true;
		}
		
		if(is(KEY_SHOW_TABLES)) {
			String sql = DBKonstants.SHOW_TABLES;
			QueryWatcher ming = query(sql);
			export(ming.exportLiteralStrings());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_TABLE + "\\s(.+)");
		if(solo != null) {
			String sql = DBKonstants.SHOW_TABLES;
			QueryWatcher ming = query(sql);

			List<String> items = ming.exportLiteralStrings();
			List<MexObject> result = CollectionUtil.search(items, solo);
			export(result);
			
			return true;
		}
		
		String temp = DBHelper.SQL_RESERVED_WORDS.replaceAll(";", "|");
		sean = new SqlInputAnalyzer(input);
		params = StrUtil.parseParams("(" + temp + ")\\s+(.+)", sean.getCommand());
		if(params != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			String sql = DBHelper.rephrase(params);
			dealWith(sql);
			
			return true;
		}
		
		if(is(KEY_DATABASE)) {
			DBConfigItem db = DBHelper.getActiveDB();
			export(db.toPrint());
			
			return true;
		}
		
		solo = parseSoloParam(KEY_DATABASE + "\\.(.+)");
		if(solo != null) {
			String dbName = solo.toLowerCase();
			if(DBHelper.takeAsColumnOrTableName(dbName)) {
				DBConfigItem db = DBHelper.getDatabaseByName(dbName);
				if(db != null) {
					export(db.toPrint());
				} else {
					export("No configuration for database [" + dbName + "].");
				}
				
				return true;
			}
		}
		
		if(is(KEY_DATABASE + KEY_2DOTS)) {
			List<String> result = new ArrayList<>();
			List<DBConfigItem> list = DBHelper.getAllDBRecords();
			for(int i = 0; i < list.size(); i++) {
				DBConfigItem item = list.get(i);
				if(i != 0) {
					result.add("");
				}
				result.add(item.toPrint());
			}
			
			export(result);
			return true;
		}
		
		solo = parseSoloParam(KEY_DATABASE + "=(.+)");
		if(solo != null) {
			String dbName = solo;
			DBConfigItem db = DBHelper.getDatabaseByName(dbName);
			if(db != null) {
				g().getUserProps().put("db.active", dbName);
				C.pl2("currently active: " + dbName + "");
				g().getUserProps().put("db.schema", null);
				export(db.toPrint());
			} else {
				export("No configuration for database [" + dbName + "].");
			}
			
			return true;
		}
		
		if(StrUtil.isRegexMatched(KEY_MYSQL + " (.+)", command)) {
			ConfigItemParser hai = new ConfigItemParserMySQL();
			DBConfigItem db = hai.parse(command);
			String dbName = db.getItemName();
			if(db != null) {
				Stash.g().place(dbName, db);
				g().getUserProps().put("db.active", dbName);
				g().getUserProps().put("db.schema", null);
				C.pl2("currently active: " + dbName + "");
				export(db.toPrint());
			}
			
			return true;
		}
		
		solo = parseSoloParam(KEY_SCHEMA + "=(.+)");
		if(solo != null) {
			String sql = DBKonstants.SHOW_DATABASES;
			String actualSchema = null;
			
			QueryWatcher ming = query(sql);
			List<String> items = ming.exportLiteralStrings();
			for(String item : items) {
				if(StrUtil.equals(solo, item)) {
					actualSchema = item;
				}
			}
			
			if(actualSchema != null) {
				g().getUserProps().put("db.schema", actualSchema);
				C.pl2("currently active schema: " + actualSchema + "");
			} else {
				C.pl("Not found schema [" + solo + "], available schemas:");
				C.list(items);
				C.pl();
			}
			
			return true;
		}
		
		return false;
	}
	
	private void dealWith(String sql) {
		String[] queryPrefixes = DBHelper.KEYS_QUERY.split(";");
		
		if(StrUtil.startsWith(sql, queryPrefixes)) {
			QueryWatcher ming = query(sql);
			if(target instanceof TargetExcel) {
				export(ming.exportListItems());
			} else {
				export(ming.exportLiteralStrings());
			}
		} else {
			if(g().isYes("sql.print")) {
				C.pl("doing... " + sql);
			}
			int affectedRows = DBManager.g().update(sql);
			String plural = affectedRows > 1 ? "s" : "";
			String tempalte = "affected row{0}: {1}.";
			String temp = StrUtil.occupy(tempalte, plural, affectedRows);
			export(temp);		
		}
	}
	
	private QueryWatcher query(String sql) {
		boolean printSql = g().isYes("sql.print");
		boolean printColumnName = g().isYes("sql.columnName.print");
		QueryWatcher ming = DBManager.g().query(sql, true, printSql);
		
		ming.setPrintColumnName(printColumnName);
		
		return ming;
	}
}
