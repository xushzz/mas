package com.sirap.common.component.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sirap.basic.db.DBFactory;
import com.sirap.basic.db.parser.SchemaNameParser;
import com.sirap.basic.domain.TypedKeyValueItem;
import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.DBUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.common.framework.SimpleKonfig;
import com.sirap.common.framework.Stash;

public class DBHelper {

	public static final String KEY_DB_CONFIG_FLY = "flydb";
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

}
