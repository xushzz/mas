package com.sirap.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.Stash;
import com.sirap.db.parser.SchemaNameParser;

public class DBHelper {

	public static final String KEY_DB_CONFIG_FLY = "flydb";
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
	
	private static DBConfigItem activeDB;
	
	public static DBConfigItem getActiveDB() throws MexException {
		if(activeDB == null) {
			String key = "db.active";
			String dbName = SimpleKonfig.g().getUserValueOf(key);
			if(dbName == null) {
				XXXUtil.alert("No setting for active database [{0}].", key);
			} else {
				DBConfigItem item = DBHelper.getDatabaseByName(dbName);
				if(item == null) {
					throw new MexException("No configuration for active database [" + dbName + "].");
				}
				
				activeDB = item;
			}
		}
		
		return activeDB;
	}
	
	public static void setActiveDB(DBConfigItem db) throws MexException {
		activeDB = db;
	}
	
	public static void setActiveDBSchema(String schema) throws MexException {
		DBConfigItem item = getActiveDB();
		String url = item.getUrl();
		String dbType = DBUtil.dbTypeOfUrl(url);
		SchemaNameParser zhihui = DBFactory.getSchemaNameParser(dbType);
		String fixedUrl = zhihui.fixUrlByChangingSchema(item.getUrl(), schema);
		item.setUrl(fixedUrl);
	}
	
	public static List<DBConfigItem> getAllDBRecords() {
		return new ArrayList<DBConfigItem>(getAllDBConfigItems().values());
	}
	
	public static Map<String, DBConfigItem> getAllDBConfigItems() {
		Map<String, DBConfigItem> map = getDBRecordsMap(SimpleKonfig.g().getUserProps().listOf());

		if(activeDB != null) {
			map.put(activeDB.getItemName(), activeDB);
		}

		DBConfigItem instash = (DBConfigItem)Stash.g().read(KEY_DB_CONFIG_FLY);
		if(instash != null) {
			map.put(instash.getItemName(), instash);
		}
		
		return map;
	}
	
	public static DBConfigItem getDatabaseByName(String dbName) {
		Map<String, DBConfigItem> map = getAllDBConfigItems();
		DBConfigItem record = map.get(dbName);
		
		return record;
	}

	private static Map<String, DBConfigItem> getDBRecordsMap(List<TypedKeyValueItem> configs) {
		String keywords = "url|who";
		List<String> list = StrUtil.split(keywords, '|');
		String regex = "(.*?)\\.(" + keywords + ")";
		
		Map<String, DBConfigItem> store = new HashMap<>();
		for(TypedKeyValueItem config : configs) {
			String key = config.getKey();
			String[] entry = StrUtil.parseParams(regex, key);
			if(entry == null) {
				continue;
			}
			
			String dbName = entry[0];
			DBConfigItem record = store.get(dbName);
			if(record == null) {
				record = new DBConfigItem(dbName);
				store.put(dbName, record);
			}
			
			String value = config.getValueX();
			String attribute = entry[1];
			int index = list.indexOf(attribute);
			if(index == 0) {
				record.setUrl(value);
			} else if(index == 1) {
				String[] arr = value.split(",");
				if(arr.length > 0) {
					record.setUsername(arr[0].trim());
				}			
				if(arr.length > 1) {
					record.setPassword(arr[1].trim());
				}
			}
		}
		
		Map<String, DBConfigItem> map2 = new HashMap<>();
		Iterator<String> it = store.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			DBConfigItem record = store.get(key);
			if(record.isValid()) {
				map2.put(key, record);
			}
		}
		
		return map2;
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
}
