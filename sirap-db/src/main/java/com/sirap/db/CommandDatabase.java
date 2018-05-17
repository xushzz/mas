package com.sirap.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.CollUtil;
import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.framework.Stash;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.target.TargetExcel;
import com.sirap.db.parser.ConfigItemParserMySQL;

public class CommandDatabase extends CommandBase {

	private static final String KEY_EXECUTE_SQL = "!";
	private static final String KEY_TABLE = "t";
	private static final String KEY_DATABASE = "db";
	private static final String KEY_SCHEMA = "sma";
	private static final String KEY_TABLES = "tbs";
	private static final String KEY_DATABASES = "dbs";
	private static final String KEY_VARIABLES = "vas";
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
		solo = StrUtil.parseParam(KEY_EXECUTE_SQL + "(.{4,})", sean.getCommand());
		if(solo != null) {
			File file = parseFile(solo);
			boolean reduce = OptionUtil.readBooleanPRI(options, "r", true);
			List<String> sqls = null;
			if(file != null) {
				String filepath = file.getAbsolutePath();
				if(FileOpener.isTextFile(filepath)) {
					checkTooBigToHandle(file, g().getUserValueOf(SQL_MAX_SIZE_KEY, SQL_MAX_SIZE_DEFAULT));
					sqls = DBUtil.readSqlFile(filepath, charset(), reduce);
				} else {
					XXXUtil.alert("Not a text file: {0}", solo);
				}
			} else {
				sqls = DBUtil.readSqls(solo, reduce);
			}
			
			if(EmptyUtil.isNullOrEmpty(sqls)) {
				XXXUtil.alert("Sql file virtually empty: {0}", solo);
			} else {
				boolean batch = OptionUtil.readBooleanPRI(options, "b", false);
				if(batch) {
					int[] result = manager().batch(sqls, printSql());
					export(printResult(result, sqls));						
				} else {
					dealWith(sqls);
				}
			}
			
			return true;
		}

		if(is(KEY_VARIABLES)) {
			String sql = DBKonstants.SHOW_VARIABLES;
			QueryWatcher ming = query(sql);
			export(watcherExport(ming));
			
			return true;
		}

		if(isIn(KEY_DATABASES)) {
			String sql = DBKonstants.SHOW_DATABASES_X;
			QueryWatcher ming = query(sql);
			export(watcherExport(ming));
			
			return true;
		}

		if(isIn(KEY_SCHEMA + KEY_2DOTS)) {
			String sql = DBKonstants.SHOW_DATABASES;
			QueryWatcher ming = query(sql);
			export(watcherExport(ming));
			
			return true;
		}
		
		if(isIn(KEY_SCHEMA)) {
			String sql = DBKonstants.SHOW_CURRENT_DATABASE;
			QueryWatcher ming = query(sql);
			export(watcherExport(ming));
			
			return true;
		}
		
		if(is(KEY_TABLES)) {
			boolean showAll = OptionUtil.readBooleanPRI(options, "a", false);
			String sql = showAll ? DBKonstants.SHOW_ALL_SCHEMA_TABLES : DBKonstants.SHOW_CURRENT_SCHEMA_TABLES;
			QueryWatcher ming = query(sql);
			export(watcherExport(ming));
			
			return true;
		}
		
		if(is(KEY_TABLES + KEY_2DOTS)) {
			String sql = DBKonstants.SHOW_USER_SCHEMA_TABLES;
			QueryWatcher ming = query(sql);
			export(watcherExport(ming));
			
			return true;
		}
		
		solo = parseParam(KEY_TABLE + "\\s(.+)");
		if(solo != null) {
			String sql = DBKonstants.SHOW_CURRENT_SCHEMA_TABLES;
			QueryWatcher ming = query(sql);
			export2(watcherExport(ming), solo);
			
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
		
		solo = parseParam(KEY_DATABASE + "\\.(.+)");
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
		
		solo = parseParam(KEY_DATABASE + "=(.+)");
		if(solo != null) {
			String dbName = solo;
			DBConfigItem db = DBHelper.getDatabaseByName(dbName);
			if(db != null) {
				DBHelper.setActiveDB(db);
				C.pl2("currently active: " + dbName + "");
				export(db.toPrint());
			} else {
				export("No configuration for database [" + dbName + "].");
			}
			
			return true;
		}
		
		if(StrUtil.isRegexMatched(KEY_MYSQL + " (.+)", command)) {
			DBConfigItem db = (new ConfigItemParserMySQL()).parse(command);
			if(db != null) {
				DBHelper.setActiveDB(db);
				String dbName = db.getItemName();
				Stash.g().place(dbName, db);
				C.pl2("currently active: " + dbName + "");
				export(db.toPrint());
			}
			
			return true;
		}
		
		solo = parseParam(KEY_SCHEMA + "=(.*)");
		if(solo != null) {
			if(solo.isEmpty()) {
				DBHelper.setActiveDBSchema("");
				C.pl2("No schema/database selected.");
			} else {
				String sql = DBKonstants.SHOW_DATABASES;
				String actualSchema = null;
				
				QueryWatcher ming = query(sql);
				List<String> items = ming.exportLiteralStrings();
				String smaCriteria = "^" + solo + "$";
				List tempA = CollUtil.filterMix(items, smaCriteria, false);
				if(tempA.size() == 1) {
					actualSchema = solo;
				} else {
					smaCriteria = solo;
					tempA = CollUtil.filterMix(items, smaCriteria, false);
					if(tempA.size() == 1) {
						actualSchema = tempA.get(0).toString();
					} else if(tempA.size() > 1) {
						C.pl("Not found schema [" + solo + "], similar schemas:");
						C.list(tempA);
						C.pl();
					}
				}
				
				if(actualSchema != null) {
					DBHelper.setActiveDBSchema(actualSchema);
					C.pl2("currently active schema: " + actualSchema + "");
				} else if(tempA.isEmpty()) {
					C.pl("Not found schema [" + solo + "], available schemas:");
					CollUtil.sortIgnoreCase(items);
					C.list(items);
					C.pl();
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private List<String> printResult(int[] result, List<String> sqls) {
		List<String> lines = Lists.newArrayList("Results:");
		String tempalte = "#{0} rows affected {1} by {2}";
		for(int i = 0; i < result.length; i++) {
			lines.add(StrUtil.occupy(tempalte, i + 1, result[i], StrUtil.firstK(sqls.get(i), 100)));
		}
		
		return lines;
	}
	
	private List<String> watcherExport(QueryWatcher ming) {
		boolean rotate = OptionUtil.readBooleanPRI(options, "r", false);
		boolean pretty = OptionUtil.readBooleanPRI(options, "p", true);
		String connector = OptionUtil.readString(options, "c", " , ");
		
		List<String> items = ming.exportLiteralStrings(rotate, pretty, connector);
		return items;
	}
	
	private void dealWith(List<String> sqls) {
		boolean fix = OptionUtil.readBooleanPRI(options, "f", true);
		String temp = DBHelper.SQL_RESERVED_WORDS.replaceAll(";", "|");
		for(String sql : sqls) {
			if(fix) {
				String[] params = StrUtil.parseParams("(" + temp + ")\\s+(.+)", sql);
				if(params != null) {
					sql = DBHelper.rephrase(params);
				}
			}
			dealWith(sql);
		}
	}
	
	private void dealWith(String sql) {
		String[] queryPrefixes = DBHelper.KEYS_QUERY.split(";");
		boolean rotate = OptionUtil.readBooleanPRI(options, "r", false);
		boolean pretty = OptionUtil.readBooleanPRI(options, "p", true);
		String connector = OptionUtil.readString(options, "c", " , ");
		if(StrUtil.startsWith(sql, queryPrefixes)) {
			QueryWatcher ming = query(sql);
			if(target instanceof TargetExcel) {
				export(ming.exportListItems(rotate));
			} else {
				export(ming.exportLiteralStrings(rotate, pretty, connector));
			}
		} else {
			int affectedRows = manager().update(sql, printSql());
			String plural = affectedRows > 1 ? "s" : "";
			String tempalte = "affected row{0}: {1}.";
			String temp = StrUtil.occupy(tempalte, plural, affectedRows);
			export(temp);		
		}
	}
	
	private QueryWatcher query(String sql) {
		Boolean pc2 = OptionUtil.readBoolean(options, "c");
		boolean printColumnName = pc2 != null ? pc2 : g().isYes("sql.columnName.print");
		QueryWatcher ming = manager().query(sql, true, printSql());
		
		ming.setPrintColumnName(printColumnName);
		
		return ming;
	}

	private DBManager manager() {
		DBConfigItem item = DBHelper.getActiveDB();
		return DBManager.g(item.getUrl(), item.getUsername(), item.getPassword());
	}
	
	private boolean printSql() {
		return g().isYes("sql.print");
	}
}
