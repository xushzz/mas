package com.sirap.geek.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sirap.basic.exception.MexException;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;
import com.sirap.geek.json.JsonBox;
import com.sirap.geek.json.JsonPrinter;
import com.sirap.geek.json.JsonReader;

public class JsonUtil {
	public static String getFirstStringValueByKey(String source, String key) {
		if(source == null)
			return null;
		String template = "\"{0}\"\\s*:\\s*\"([^\"]*)\"";
		String regex = StrUtil.occupy(template, key);
		Matcher m = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);

		if(m.find()) {
			String value = m.group(1);
			return value;
		}
		
		return null;
	}
	
	public static String getFirstNumberValueByKey(String source, String key) {
		if(source == null)
			return null;
		String template = "\"{0}\"\\s*:\\s*(\\d+)";
		String regex = StrUtil.occupy(template, key);
		Matcher m = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE).matcher(source);

		if(m.find()) {
			String value = m.group(1);
			return value;
		}
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static List toList(String json) {
		Object sea = parseObject(json);
		
		if(!(sea instanceof List)) {
			XXXUtil.alert("can't convert the json to List, could be " + sea.getClass().getName());
		}
		
		return (List)sea;
	}

	public static boolean isLegalJson(String json) {
		try {
			JsonBox beiring = new JsonBox(json);
			
			return beiring.isLegalJson();
		} catch (MexException ex) {
			return false;
		}
	}

	public static Object parseObject(String json) {
		JsonBox beiring = new JsonBox(json);
		Object sea = beiring.getKing();
		
		return sea;
	}

	@SuppressWarnings("rawtypes")
	public static Map toMap(String json) {
		Object sea = parseObject(json);
		
		if(!(sea instanceof Map)) {
			XXXUtil.alert("can't convert the json to Map, could be " + sea.getClass().getName());
		}
		
		return (Map)sea;
	}
	
	public static String getPrettyText(String json) {
		if(EmptyUtil.isNullOrEmpty(json)) {
			return null;
		}
		
		JsonBox beiring = new JsonBox(json);
		JsonPrinter master = new JsonPrinter(beiring.getKing());
		
		return master.getPrettyText();
	}
	
	public static String getRawText(String json) {
		if(EmptyUtil.isNullOrEmpty(json)) {
			return null;
		}
		
		JsonBox beiring = new JsonBox(json);
		JsonPrinter master = new JsonPrinter(beiring.getKing());
		
		return master.getRawText();
	}
	
	public static String getPrettyText(Object json) {
		JsonPrinter master = new JsonPrinter(json);
		
		return master.getPrettyText();
	}
	
	public static String getRawText(Object json) {
		JsonPrinter master = new JsonPrinter(json);
		
		return master.getRawText();
	} 
	
	public static Object readObjectByPath(Object json, String path) {
		JsonReader master = new JsonReader(json);
		
		return master.readObject(path);
	}
}
