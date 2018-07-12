package com.sirap.common.framework;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.tool.C;
import com.sirap.basic.util.StrUtil;

public class Stash {
	public static final String KEY_USER_INPUT_TARGET = "userInputTarget";
	public static final String KEY_START_IN_MILLIS = "startInMillis";
	public static final int MAX_QUERY_RESULT = 999;
	public static final String KEY_GETSTASH = "getstash";
	
	private Stash() {
		
	}
	
	private static Stash instanace;
	
	private static class Holder {
		private static Stash instance = new Stash();
	}
	
	public static Stash g() {
		return Holder.instance;
	}
	
	private List<QueryItem> queryList = Lists.newArrayList();

	public void clearQuery() {
		queryList.clear();
	}
	
	public List<String> getQueryNames() {
		List items = Lists.newArrayList();
		String temp = "#last{0} [{1} records] for {2}";
		int count = 1;
		for(QueryItem item : queryList) {
			items.add(StrUtil.occupy(temp, count++, item.getResult().size(), item.getCommand()));
		}
		
		return items;
	}
	
	public List getLastQuery() {
		return getLastKQuery(1);
	}
	
	public List getLastKQuery(int kFromOne) {
		int size = queryList.size();
		if(kFromOne > size || kFromOne < 1) {
			C.pl2("The demanded query #{0} exceeds query size of {1}.", kFromOne, size);
			return getQueryNames();
		}
		
		QueryItem item = queryList.get(kFromOne - 1);
		List all = Lists.newArrayList("$ " + item.getCommand());
		all.addAll(item.getResult());
		
		return all;
	}
	
	public void setLastQuery(String command, List result) {
		setLastQuery(new QueryItem(command, result));
	}

	public void setLastQuery(QueryItem query) {
		if(queryList.size() >= MAX_QUERY_RESULT) {
			//remove the oldest
			queryList.remove(queryList.size() - 1);
		}
		queryList.add(0, query);
	}

	private Map<Object, Object> map = new LinkedHashMap<>();
	
	public void place(Object key, Object value) {
		map.put(key, value);
	}
	
	public Object readAndRemove(Object key) {
		Object value = map.get(key);
		map.remove(key);
		
		return value;
	}
	
	public Object read(Object key) {
		Object value = map.get(key);
		
		return value;
	}
	
	public List<Map.Entry<Object, Object>> print() {
		Set<Map.Entry<Object, Object>> balls = map.entrySet();
		
		return Lists.newArrayList(balls);
	}
}
