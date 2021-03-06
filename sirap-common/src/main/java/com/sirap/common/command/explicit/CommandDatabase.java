package com.sirap.common.command.explicit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sirap.basic.component.DBKonstants;
import com.sirap.basic.db.DBManager;
import com.sirap.basic.db.QueryWatcher;
import com.sirap.basic.db.resultset.ResultSetMingAnalyzer;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.Colls;
import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.OptionUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.command.CommandBase;
import com.sirap.common.component.FileOpener;
import com.sirap.common.component.db.ConfigItemParserMySQL;
import com.sirap.common.component.db.DBConfigItem;
import com.sirap.common.component.db.DBHelper;
import com.sirap.common.component.db.MysqlHelpCategory;
import com.sirap.common.component.db.MysqlHelpTopic;
import com.sirap.common.component.db.MysqlHelper;
import com.sirap.common.framework.Stash;
import com.sirap.common.framework.command.InputAnalyzer;
import com.sirap.common.framework.command.SqlInputAnalyzer;
import com.sirap.common.framework.command.target.TargetConsole;
import com.sirap.common.framework.command.target.TargetExcel;

public class CommandDatabase extends CommandBase {

	private static final String KEY_EXECUTE_SQL = "!";
	private static final String KEY_TABLE = "t";
	private static final String KEY_DATABASE = "db";
	private static final String KEY_SCHEMA = "sma";
	private static final String KEY_TABLES = "tbs";
	private static final String KEY_DATABASES = "dbs";
	private static final String KEY_VARIABLES = "vas";
	private static final String KEY_MYSQL = "mysql";
	private static final String KEY_MYSQL_HELP = "mys";

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
					sqls = DBUtil.readSqlFile(filepath, charset(), reduce, true);
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
		
		params = parseParams("(\\*\\s*|cols\\s)(\\S+)(.*?)");
		if(params != null) {
			String type = params[0];
			String tableInfo = params[1];
			String remain = params[2];

			if(DBUtil.takeAsColumnOrTableName(tableInfo)) {
				String sql = DBKonstants.SHOW_COLUMNS + " " + tableInfo;
				List<String> items = manager().query(resultSetAsList(), sql, true, printSql()).exportLiteralStrings();
				if(EmptyUtil.isNullOrEmpty(items)) {
					XXXUtil.info("Found no columns from: [{0}]", tableInfo);
					C.pl();
				} else {
					boolean showColumns = StrUtil.equals("cols", type);
					boolean sort = OptionUtil.readBooleanPRI(options, "s", false);
					boolean single = items.size() == 1;
					if(showColumns) {
						if(single) {
							String[] arr = items.get(0).split("\\s{2,}");
							List<String> rotate = Lists.newArrayList();
							rotate.add(arr[0].trim());
							List<String> cols = StrUtil.split(arr[1].trim());
							if(sort) {
								Colls.sortIgnoreCase(cols);
							}
							rotate.addAll(cols);
							export(rotate);
						} else {
							export(items);
						}
					} else {
						List<String> lines = Lists.newArrayList();
						for(String item : items) {
							String[] arr = item.split("\\s{2,}");
							String schemaAndTable = arr[0].trim();
							List<String> cols = StrUtil.split(arr[1].trim());
							if(sort) {
								Colls.sortIgnoreCase(cols);
							}
							String newSql = StrUtil.occupy("select {0} from {1} {2}", StrUtil.connectWithCommaSpace(cols), schemaAndTable, remain);
							lines.add(newSql);
						}
						export(lines);
					}
				}
			} else {
				XXXUtil.info("Not a valid table name: [{0}]", tableInfo);
				C.pl();
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
		
		String temp = DBUtil.SQL_RESERVED_WORDS.replaceAll(";", "|");
		sean = new SqlInputAnalyzer(input);
		params = StrUtil.parseParams("(" + temp + ")\\s+(.+)", sean.getCommand());
		if(params != null) {
			this.command = sean.getCommand();
			this.target = sean.getTarget();
			String sql = DBUtil.rephrase(params);
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
			if(DBUtil.takeAsColumnOrTableName(dbName)) {
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
			String dbName = null;
			String schema = null;
			List<String> items = StrUtil.split(solo, ".");
			if(items.size() == 2) {
				dbName = items.get(0);
				schema = items.get(1);
			} else {
				dbName = solo;
			}
			DBConfigItem db = DBHelper.getDatabaseByName(dbName);
			if(db != null) {
				DBHelper.setActiveDB(db);
				C.pl2("currently active: " + dbName + "");
				export(db.toPrint());
				if(schema != null) {
					setSchema(schema);
				}
			} else {
				export("No configuration for database [" + dbName + "].");
			}
			
			return true;
		}
		
		if(is("dba")) {
			long start = System.currentTimeMillis();
			String sql = "select version()";
			DBConfigItem item = DBHelper.getActiveDB();
			DBManager karius = DBManager.g(item.getUrl(), item.getUsername(), item.getPassword());
			QueryWatcher ming = karius.query(resultSetAsList(), sql);
			List<String> items = ming.exportLiteralStrings();
			if(items.size() > 0) {
				long cost = System.currentTimeMillis() - start;
				String line = StrUtil.occupy("Connected [{0} millis] to {1} by {2} [version: {3}]", cost, item.getUrl(), item.getUsername(), items.get(0));
				export(line);
			}
			
			return true;
		}
		
		if(StrUtil.isRegexMatched(KEY_MYSQL + " (.+)", command)) {
			DBConfigItem db = (new ConfigItemParserMySQL()).parse(command);
			if(db != null) {
				String url = db.getUrl();
				String username = db.getUsername();
				String password = db.getPassword();
				DBManager huang = DBManager.g(url, username, password);
				try {
					huang.isAvailable();
					DBHelper.setActiveDB(db);
					String dbName = db.getItemName();
					Stash.g().place(dbName, db);
					C.pl2("currently active: " + dbName + "");
					export(db.toPrint());
				} catch (Exception ex) {
					export("Can't create a connection by: " + command);
					if(isDebug()) {
						ex.printStackTrace();
					}
				}
			}
			
			return true;
		}
		
		solo = parseParam(KEY_SCHEMA + "=(.*)");
		if(solo != null) {
			setSchema(solo);
			
			return true;
		}
		
		solo = parseParam(KEY_MYSQL_HELP + "(|\\s+.+?)");
		if(solo != null) {
			String topicInfo = StrUtil.parseParam("(.+?)\\.", solo);
			if(topicInfo != null) {
				MysqlHelpTopic topic;
				if(StrUtil.isRegexMatched("\\d{1,3}", topicInfo)) {
					int id = Integer.parseInt(topicInfo);
					topic = MysqlHelper.getHelpTopic(manager(), id);
				} else {
					topic = MysqlHelper.getHelpTopic(manager(), topicInfo);
				}
				if(topic == null) {
					C.pl2("Not found MySQL help topic: " + topicInfo);
				} else {
					export(topic.list());
				}
			} else {
				List<MysqlHelpCategory> cats = MysqlHelper.getHelpCategories(manager());
				boolean showTopic = OptionUtil.readBooleanPRI(options, "t", true);
				if(showTopic) {
					List<MysqlHelpTopic> topics =MysqlHelper.getHelpTopics(manager());
					MysqlHelper.addTopics(cats, topics);
				}
				
				MysqlHelpCategory root = MysqlHelper.categoryTreeOf(cats);

				int kInfo = 0;
				Boolean kk = OptionUtil.readBoolean(options, "k");
				if(kk != null) {
					kInfo = kk ? 100 : 0;
				} else {
					kInfo = OptionUtil.readIntegerPRI(options, "k", 0);
				}
				
				List<String> lines = root.list(kInfo, showTopic);

				if(solo.isEmpty()) {
					export(lines);
				} else {
					export2(lines, solo);
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private void setSchema(String tempSchema) {
		XXXUtil.nullCheck(tempSchema, "tempSchema");
		
		if(tempSchema.isEmpty()) {
			DBHelper.setActiveDBSchema("");
			C.pl2("No schema/database selected.");
			return;
		}
		
		String sql = DBKonstants.SHOW_DATABASES;
		String actualSchema = null;
		
		QueryWatcher ming = query(sql);
		ming.setPrintColumnName(false);
		List<String> items = ming.exportLiteralStrings();
		String smaCriteria = "^" + tempSchema + "$";
		List<MexItem> tempA = Colls.filter(toMexItems(items), smaCriteria);
		if(tempA.size() == 1) {
			actualSchema = tempSchema;
		} else {
			smaCriteria = tempSchema;
			tempA = Colls.filterMix(items, smaCriteria, false);
			if(tempA.size() == 1) {
				actualSchema = tempA.get(0).toString();
			} else if(tempA.size() > 1) {
				C.pl("Not found schema [" + tempSchema + "], similar schemas:");
				C.list(tempA);
				C.pl();
			}
		}

		if(actualSchema != null) {
			DBHelper.setActiveDBSchema(actualSchema);
			C.pl2("currently active schema: " + actualSchema + "");
		} else if(tempA.isEmpty()) {
			C.pl("Not found schema [" + tempSchema + "], available schemas:");
			Colls.sortIgnoreCase(items);
			C.list(items);
			C.pl();
		}
	}
	
	private List<String> printResult(int[] result, List<String> sqls) {
		List<String> lines = Lists.newArrayList("Results:");
		String tempalte = "#{0} rows affected {1} by {2}";
		for(int i = 0; i < result.length; i++) {
			lines.add(StrUtil.occupy(tempalte, i + 1, result[i], StrUtil.firstK(sqls.get(i), 100)));
		}
		
		return lines;
	}
	
	private List watcherExport(QueryWatcher ming) {
		boolean rotate = OptionUtil.readBooleanPRI(options, "r", false);
//		if(TargetConsole.class.isInstance(target)) {
//			boolean pretty = OptionUtil.readBooleanPRI(options, "p", true);
//			String connector = OptionUtil.readString(options, "c", " , ");
//			return ming.exportLiteralStrings(rotate, pretty, connector);
//		} else {
//			return ming.exportListItems(rotate);
//		}
		if(isToExcel() || isToPdf()) {
			return ming.exportListItems(rotate);
		} else {
			boolean pretty = OptionUtil.readBooleanPRI(options, "p", true);
			String connector = OptionUtil.readString(options, "c", " , ");
			return ming.exportLiteralStrings(rotate, pretty, connector);
		}
	}
	
	private void dealWith(List<String> sqls) {
		boolean fix = OptionUtil.readBooleanPRI(options, "f", true);
		String temp = DBUtil.SQL_RESERVED_WORDS.replaceAll(";", "|");
		for(String sql : sqls) {
			if(fix) {
				String[] params = StrUtil.parseParams("(" + temp + ")\\s+(.+)", sql);
				if(params != null) {
					sql = DBUtil.rephrase(params);
				}
			}
			dealWith(sql);
		}
	}
	
	private void dealWith(String sql) {
		String[] queryPrefixes = DBUtil.KEYS_QUERY.split(";");
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
		QueryWatcher ming = manager().query(resultSetAsList(), sql, true, printSql());
		
		ming.setPrintColumnName(printColumnName);
		
		return ming;
	}

	private DBManager manager() {
		DBConfigItem item = DBHelper.getActiveDB();
		return DBManager.g(item.getUrl(), item.getUsername(), item.getPassword());
	}
	
	private boolean printSql() {
		Boolean toPrint = OptionUtil.readBoolean(options, "sql");
		boolean flag = toPrint != null ? toPrint : g().isYes("sql.print");
		
		return flag;
	}
	
	public ResultSetMingAnalyzer resultSetAsList() {
		return new ResultSetMingAnalyzer();
	}
}
