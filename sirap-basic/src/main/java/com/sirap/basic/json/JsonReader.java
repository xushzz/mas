package com.sirap.basic.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MathUtil;
import com.sirap.basic.util.StrUtil;

public class JsonReader {
	
	private Object king;
	
	public JsonReader(Object king) {
		this.king = king;
	}

	@SuppressWarnings("rawtypes")
	public Object readObject(String path) {
		List allItems = new ArrayList();
		readObject(king, path, allItems);
		if(EmptyUtil.isNullOrEmpty(allItems)) {
			return null;
		} else if(allItems.size() == 1) {
			return allItems.get(0);
		}
		
		return allItems;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void readObject(Object json, String path, List allItems) {
		if(!(json instanceof Map || json instanceof List)) {
			//XXXUtil.alert("not a possible json Object: " + json);
			return;
		}
		
		if(EmptyUtil.isNullOrEmpty(path)) {
			//XXXUtil.alert("not a possible json path: " + path);
			return;
		}
		
		String regex = "(@|/|[^@/]+)";
		List<String> items = new ArrayList<>();
		Matcher m = Pattern.compile(regex).matcher(path);
		while(m.find()) {
			items.add(m.group());
		}
//		D.pl(path);
//		D.pl(items);
		Object currentObj = json;
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			if(isAt(item)) {
				i++;
				String temp = items.get(i);
				
				if(currentObj instanceof List) {
					List list = (List)currentObj;
					if(isAll(temp)) {
						String newPath = StrUtil.connect(items, i + 1);
						for(Object subObj : list) {
							readObject(subObj, newPath, allItems);
						}
						
						return;
					} else {
						Integer atIndex = MathUtil.toInteger(temp);
						if(atIndex == null) {
							//XXXUtil.info("index [" + temp + "] is illegal for path: " + path);
							return;
						}
						
						atIndex--;
						if(atIndex >= 0 && atIndex < list.size()) {
							currentObj = list.get(atIndex);
						} else {
							//XXXUtil.info("index [" + atIndex + "] is out of range for list size [" + list.size() + "].");
							return;
						}
					}
				} else {
					//XXXUtil.info("looking for [" + item + temp + "], but object is not a List => " + currentObj);
					return;
				}
			} else if(isSlash(item)) {
				i++;
				String key = items.get(i);
				currentObj = getFromMap(currentObj, key);
			} else {
				String key = items.get(i);
				currentObj = getFromMap(currentObj, key);
			}
		}
		
		if(currentObj != null) {
			allItems.add(currentObj);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Object getFromMap(Object currentObj, String key) {
		if(currentObj instanceof Map) {
			Map map = (Map)currentObj;
			Object targetObj = map.get(key);
			if(targetObj == null) {
				//XXXUtil.info("Key [" + key + "] is not found in key set " + map.keySet() + ".");
			} else {
				return targetObj;
			}
		} else {
			//XXXUtil.info("current object is not a Map => " + currentObj);
		}
		
		return null;
	}
	
	private boolean isSlash(String source) {
		String slash = "/";
		boolean flag = StrUtil.equals(source, slash);
		
		return flag;
	}
	
	private boolean isAt(String source) {
		String at = "@";
		boolean flag = StrUtil.equals(source, at);
		
		return flag;
	}
	
	private boolean isAll(String source) {
		String at = "*";
		boolean flag = StrUtil.equals(source, at);
		
		return flag;
	}
	
}
