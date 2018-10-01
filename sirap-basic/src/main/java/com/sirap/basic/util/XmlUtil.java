package com.sirap.basic.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.component.XmlToMapConverter;
import com.sirap.basic.tool.D;

import lombok.Data;

@Data
@SuppressWarnings({"unchecked", "rawtypes"})
public class XmlUtil {
	
	public static Map<String, Object> xmlOfFile(String xmlFile) {
		XmlToMapConverter jack = new XmlToMapConverter();
		return jack.toMapFromXmlFile(xmlFile);
	}
	
	public static Map<String, Object> xmlOfText(String xmlText) {
		XmlToMapConverter jack = new XmlToMapConverter();
		return jack.toMapFromXmlText(xmlText);
	}
	
	public static List wrapMapIfNeeded(Object moon) {
		List items = Lists.newArrayList();
		if(List.class.isInstance(moon)) {
			items.addAll((List)moon);
		} else if(Map.class.isInstance(moon)) {
			items.add(moon);
		} else {
			XXXUtil.alert("Unsupported type: {0}", moon.getClass().getName());
		}
		
		return items;
	}
	
	public static Object readValueFromText(String xmlText, String expression) {
		return valueOf(xmlOfText(xmlText), expression);
	}
	
	public static Object readValueFromFile(String xmlFile, String expression) {
		return valueOf(xmlOfFile(xmlFile), expression);
	}
	
	public static String rootOf(Object mapOrList) {
		if(mapOrList == null) {
			return null;
		}
		
		if(!Map.class.isInstance(mapOrList)) {
			XXXUtil.alert("Should be dealing with {0} but {1}: \n{2}", Map.class.getName(), mapOrList.getClass().getName(), D.jsp(mapOrList));
		}
		
		Map mars = (Map)mapOrList;
		Set keys = mars.keySet();
		
		if(keys.size() != 1) {
			XXXUtil.alert("Map hould contain only one entry but {0}", keys);
		}
		
		return Lists.newArrayList(keys).get(0).toString();
	}
	
	/****
	 * 
	 * @param mapOrList
	 * @param expression inventory.book.title
	 * @return	Snow Crash
	 */
	public static Object valueOf(Object mapOrList, String expression) {
		XXXUtil.nullOrEmptyCheck(expression);
		
		List<String> keys = StrUtil.split(expression, '.');

		return valueOf(mapOrList, keys);
	}
	
	public static Object valueOf(Object mapOrList, List<String> keys) {
		XXXUtil.nullOrEmptyCheck(keys);
		
		Object gist = mapOrList;
		
		for(String key : keys) {
			gist = readFromStart(gist, key);
		}
		
		return gist;
	}
	
	public static Object findBy(Object mapOrList, String expression) {
		XXXUtil.nullOrEmptyCheck(expression);
		
		List<String> keys = StrUtil.split(expression, '.');
		
		return findBy(mapOrList, keys);
	}
	
	public static Object findBy(Object mapOrList, List<String> keys) {
		XXXUtil.nullOrEmptyCheck(keys);
		
		Object gist = mapOrList;
		List matchedItems = null;
		for(String key : keys) {
			matchedItems = Lists.newArrayList();
			findAnyMatched(matchedItems, gist, key);
			gist = matchedItems;
		}
		
		if(matchedItems == null || matchedItems.isEmpty()) {
			return Konstants.FAKED_NULL;
		}
		
		if(matchedItems.size() == 1) {
			Object obj = matchedItems.get(0);
			if(obj.toString().isEmpty()) {
				return Konstants.FAKED_EMPTY;
			} else {
				return obj;
			}
		} else {
			return matchedItems;
		}
	}
	
	private static Object readFromStart(Object holder, String originKey) {
		String key = originKey;
		if(key.isEmpty()) {
//			D.pla(key, holder);
			key = rootOf(holder);
		}
//		D.pl(originKey + " => " + key);
		
		if(holder instanceof Map) {
			return ((Map)holder).get(key);
		}
		
		if(holder instanceof List) {
			List box = null;
			List items = (List)holder;
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
		
		return holder;
	}
	
	private static void findAnyMatched(List holder, Object ball, String key) {
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
	
	public static String removeHeaderCommentCDATA(String source) {
		String header = "<\\?xml.*?\\?>";
		String comment = "<!--.*?-->";
		String cdata = "<!\\[CDATA\\[.+?\\]\\]";
		String doctype = "<!DOCTYPE.*?>";
		
		String temp = source;
		temp = temp.replaceAll(header, "").trim();
		temp = temp.replaceAll(comment, "").trim();
		temp = temp.replaceAll(cdata, "").trim();
		temp = temp.replaceAll(doctype, "").trim();
		
		return temp;
	}
}
