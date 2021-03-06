package com.sirap.basic.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.tool.D;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

import lombok.Data;

@SuppressWarnings("rawtypes")
//@NoArgsConstructor
@Data
public class Mist {

	private Object core;
	private boolean isHumanReadable = true;
	
	public Object getCore() {
		return core;
	}

	public Mist asIs() {
		this.isHumanReadable = false;
		return this;
	}

	public boolean isArray() {
		return List.class.isInstance(core);
	}
	
	public boolean isMap() {
		return Map.class.isInstance(core);
	}
	
	public Mist(Object core) {
		this.core = core;
		XXXUtil.shouldBeTrue(isArray() || isMap());
	}
	
	public String rootOf(Object gist) {
		if(gist == null) {
			return null;
		}
		
		if(!Map.class.isInstance(gist)) {
			XXXUtil.alert("Should be dealing with {0} but {1}: \n{2}", Map.class.getName(), gist.getClass().getName(), D.jsp(gist));
		}
		
		Map mars = (Map)gist;
		Set keys = mars.keySet();
		
		if(keys.size() != 1) {
			XXXUtil.alert("Map should contain only one entry but {0}", keys);
		}
		
		return Lists.newArrayList(keys).get(0).toString();
	}
	
	/****
	 * 
	 * @param mapOrList
	 * @param expression inventory.book.title
	 * @return	Snow Crash
	 */
	public Object valueOf(String expression) {
		XXXUtil.nullCheckOnly(expression);
		
		List<String> keys = Lists.newArrayList();
		if(expression.isEmpty()) {
			keys.add(expression);
		} else {
			keys = StrUtil.split(expression, '.');
		}
		
		return valueOf(keys);
	}
	
	public Object valueOf(List<String> keys) {
		XXXUtil.nullCheckOnly(keys);
		
		Object gist = core;
		
		for(String key : keys) {
			gist = readFromStart(gist, key);
		}
		
		return gist;
	}
	
	public Object findBy(String expression) {
		XXXUtil.nullOrEmptyCheck(expression);
		
		List<String> keys = StrUtil.split(expression, '.');
		
		return findBy(keys);
	}
	
	public String findStringBy(String expression) {
		XXXUtil.nullOrEmptyCheck(expression);
		
		List<String> keys = StrUtil.split(expression, '.');
		
		Object obj = findBy(keys);
		if(obj == null) {
			return null;
		}
		
		return obj.toString();
	}
	
	public Object findBy(List<String> keys) {
		XXXUtil.nullOrEmptyCheck(keys);
		
		Object gist = core;
		List matchedItems = null;
		for(String key : keys) {
			matchedItems = Lists.newArrayList();
			findAnyMatched(matchedItems, gist, key);
			gist = matchedItems;
		}
		
		if(EmptyUtil.isNullOrEmpty(matchedItems)) {
			return isHumanReadable ? Konstants.FAKED_EMPTY : null;
		}
		
		if(matchedItems.size() == 1) {
			Object obj = matchedItems.get(0);
			if(obj.toString().isEmpty()) {
				return isHumanReadable ? Konstants.FAKED_EMPTY : "";
//				return Konstants.FAKED_EMPTY;
			} else {
				return obj;
			}
		} else {
			return matchedItems;
		}
	}
	
	private Object readFromStart(Object mars, String originKey) {
		String key = originKey;
		if(key.isEmpty()) {
//			D.pla(key, holder);
			key = rootOf(mars);
		}
//		D.pl(originKey + " => " + key);
		
		if(mars instanceof Map) {
			return ((Map)mars).get(key);
		}
		
		if(mars instanceof List) {
			List box = null;
			List items = (List)mars;
			for(Object item : items) {
				Object gist = readFromStart(item, key);
				if(gist == null) {
					continue;
				}
				
				if(box == null) {
					box = Lists.newArrayList();
				}
				
				if(gist instanceof List) {
					box.addAll((List)gist);
				} else {
					box.add(gist);
				}
			}
			
			if(box != null && box.size() == 1) {
				return box.get(0);
			}
			
			return box;
		}
		
		return mars;
	}
	
	private void findAnyMatched(List holder, Object ball, String key) {
		if(EmptyUtil.isNullOrEmpty(key)) {
			XXXUtil.alert("Not a valid key: {0}", key);
		}
		
//		D.pl(originKey + " => " + key);
		
		if(ball instanceof Map) {
			Map mars = (Map)ball;
			Iterator<String> it = mars.keySet().iterator();
			while(it.hasNext()) {
				String akey = it.next();
				Object moon = mars.get(akey);
				if(StrUtil.equals(akey, key)) {
					holder.add(moon);
				}
				
				findAnyMatched(holder, moon, key);
			}
		}
		
		if(ball instanceof List) {
			List items = (List)ball;
			for(Object item : items) {
				findAnyMatched(holder, item, key);
			}
		}
		
		return;
	}
}
