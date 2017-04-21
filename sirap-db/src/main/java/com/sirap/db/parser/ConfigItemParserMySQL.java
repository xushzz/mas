package com.sirap.db.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sirap.basic.util.StrUtil;
import com.sirap.db.DBConfigItem;

public class ConfigItemParserMySQL extends ConfigItemParser {

	@Override
	public DBConfigItem parse(String source) {
		List<String> list = StrUtil.splitByRegex(source, "\\s+-");
		
		DBConfigItem config = new DBConfigItem();
		String urlTemplate = "jdbc:mysql://${host}${port}${schema}";
		
		Map<String, Object> params = new HashMap<>();
		params.put("host", "localhost");
		params.put("port", "");
		params.put("schema", "");
		
		for(int i = 1; i < list.size(); i++) {
			String item = list.get(i).trim();
			String[] arr = StrUtil.parseParams("([a-z])(.+)", item);
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
		
		String url = StrUtil.occupyMaps(urlTemplate, params);
		config.setUrl(url);
		config.setItemName("flydb");
		
		return config;
	}
	
}
