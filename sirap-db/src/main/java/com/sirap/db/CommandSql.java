package com.sirap.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sirap.basic.domain.MexedObject;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollectionUtil;
import com.sirap.basic.util.IOUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.TargetExcel;
import com.sirap.db.parser.ConfigItemParser;
import com.sirap.db.parser.ConfigItemParserMySQL;

public class CommandSql extends CommandBase {

	private static final String KEY_SQL = "!";
	private static final String KEY_TABLE = "t";
	private static final String KEY_DATABASE = "db";
	private static final String KEY_SCHEMA = "sma";
	private static final String KEY_SHOW_TABLES = "tbs";
	private static final String KEY_SHOW_DATABSES = "dbs";
	private static final String KEY_MYSQL = "mysql";
	
	@Override
	public boolean handle() {
		InputAnalyzer sean = new DBInputAnalyzer(input);
		this.command = sean.getCommand();
		this.target = sean.getTarget();
		
		singleParam = parseParam(KEY_SQL + "(.{4,})");
		if(singleParam != null) {
			File file = parseFile(singleParam);
			if(file != null) {
				if(FileOpener.isTextFile(file.getAbsolutePath()) && !tooBigToHanlde(file, "10M")) {
					String filePath = file.getAbsolutePath();
					String temp = IOUtil.readFileWithLineSeparator(filePath, " ", "--");
					String sql = StrUtil.reduceMultipleSpacesToOne(temp).trim();
					dealWith(sql);
				}
			} else {
				dealWith(singleParam);
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
		
		singleParam = parseParam(KEY_TABLE + "\\s(.+)");
		if(singleParam != null) {
			String sql = DBKonstants.SHOW_TABLES;
			QueryWatcher ming = query(sql);

			List<String> items = ming.exportLiteralStrings();
			List<MexedObject> result = CollectionUtil.search(items, singleParam);
			export(result);
			
			return true;
		}
		
		String temp = DBHelper.SQL_RESERVED_WORDS.replaceAll(";", "|");
		String regex = "(" + temp + ")\\s+(.+)";
		sean = new DBInputAnalyzer(input);
		this.command = sean.getCommand();
		this.target = sean.getTarget();
		params = parseParams(regex);
		
		if(params != null) {
			String sql = DBHelper.rephrase(params);
			dealWith(sql);
			
			return true;
		}
		
		if(is(KEY_DATABASE)) {
			DBConfigItem db = DBHelper.getActiveDB();
			export(db.toPrint());
			
			return true;
		}
		
		singleParam = parseParam(KEY_DATABASE + "\\.(.+)");
		if(singleParam != null) {
			String dbName = singleParam.toLowerCase();
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
		
		singleParam = parseParam(KEY_DATABASE + "=(.+)");
		if(singleParam != null) {
			String dbName = singleParam;
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
				g().getStash().put(dbName, db);
				g().getUserProps().put("db.active", dbName);
				g().getUserProps().put("db.schema", null);
				C.pl2("currently active: " + dbName + "");
				export(db.toPrint());
			}
			
			return true;
		}
		
		singleParam = parseParam(KEY_SCHEMA + "=(.+)");
		if(singleParam != null) {
			String sql = DBKonstants.SHOW_DATABASES;
			String actualSchema = null;
			
			QueryWatcher ming = query(sql);
			List<String> items = ming.exportLiteralStrings();
			for(String item : items) {
				if(StrUtil.equals(singleParam, item)) {
					actualSchema = item;
				}
			}
			
			if(actualSchema != null) {
				g().getUserProps().put("db.schema", actualSchema);
				C.pl2("currently active schema: " + actualSchema + "");
			} else {
				C.pl("Not found schema [" + singleParam + "], available schemas:");
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
