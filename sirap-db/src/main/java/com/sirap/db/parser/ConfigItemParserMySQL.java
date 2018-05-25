package com.sirap.db.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.sirap.basic.util.StrUtil;
import com.sirap.db.DBConfigItem;
import com.sirap.db.DBHelper;

public class ConfigItemParserMySQL extends ConfigItemParser {

	@Override
	public DBConfigItem parse(String source) {
		List<String> list = StrUtil.splitByRegex(source, "\\s+-");
		
		DBConfigItem config = new DBConfigItem();
		config.setUsername("root");
		String urlTemplate = "jdbc:mysql://${host}${port}${schema}";
		
		Map<String, Object> params = new HashMap<>();
		params.put("host", "localhost");
		params.put("port", "");
		params.put("schema", "/mysql");
		
		for(int i = 1; i < list.size(); i++) {
			String item = list.get(i).trim();
			String[] arr = StrUtil.parseParams("([a-z])(.+)", item, Pattern.CASE_INSENSITIVE);
			if(arr == null) {
				continue;
			}
			
			String key = arr[0];
			String value = arr[1].trim();

			if(StrUtil.equalsCaseSensitive(key, "p")) {
				config.setPassword(value);
			} else if(StrUtil.equalsCaseSensitive(key, "u")) {
				config.setUsername(value);
			} else if(StrUtil.equalsCaseSensitive(key, "h")) {
				params.put("host", value);
			} else if(StrUtil.equalsCaseSensitive(key, "P")) {
				params.put("port", ":" + value);
			} else if(StrUtil.equalsCaseSensitive(key, "D")) {
				params.put("schema", "/" + value);
			}
		}
		
		String url = StrUtil.occupyKeyValues(urlTemplate, params);
		config.setUrl(url);
		config.setItemName(DBHelper.KEY_DB_CONFIG_FLY);
		
		return config;
	}
	
}
