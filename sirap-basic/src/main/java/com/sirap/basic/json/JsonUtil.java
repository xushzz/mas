package com.sirap.basic.json;

import java.util.List;

import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.MistUtil;
import com.sirap.basic.util.StrUtil;

public class JsonUtil {
	
	/***
	 * As its name suggests, could be any kind of object
	 * Convert it to json string, SOLID response
	 * @param key
	 * @param anyKindOfObject
	 * @return
	 */
	public static String toJson(String key, Object anyKindOfObject) {
		String template = "{\"{0}\":{1}}";
		String actual = toJson(anyKindOfObject);
		
		return StrUtil.occupy(template, key, actual);
	}
	
	public static String toJson(Object anyKindOfObject) {
		return JsonConvertManager.g().toJson(anyKindOfObject);
	}
	
	public static String toPrettyJson(Object anyKindOfObject) {
		return toPrettyJson(anyKindOfObject, 0);
	}
	
	public static String toPrettyJson(Object anyKindOfObject, int depth) {
		return JsonConvertManager.g(true).toJson(anyKindOfObject, depth);
	}
	
	public static String quote(Object obj) {
		String temp = obj + "";
		temp = temp.replace("\"", "\\\"");
		return StrUtil.occupy("\"{0}\"", temp);
	}
	
	public static Object parse(String jsonString) {
		return MistUtil.ofJsonText(jsonString).getCore();
	}
	
	public static List<String> getPrettyTextInLines(String jsonString) {
		String prettyAce = getPrettyText(jsonString);
		return StrUtil.split(prettyAce, "\n", false);
	}
	
	public static String getPrettyText(String jsonString) {
		if(EmptyUtil.isNullOrEmpty(jsonString)) {
			return jsonString;
		}
		
		return toPrettyJson(parse(jsonString));
	}
	
	public static List<String> objectToPrettyJsonInLines(Object anyKindOfObject) {
		String prettyAce = toPrettyJson(anyKindOfObject);
		
		return StrUtil.split(prettyAce, "\n", false);
	}
	
	public static String objectToPrettyJson(Object anyKindOfObject) {
		return toPrettyJson(anyKindOfObject);
	}
	
	public static String getRawText(String jsonString) {
		if(EmptyUtil.isNullOrEmpty(jsonString)) {
			return null;
		}
		
		return toJson(parse(jsonString));
	}
	
	public static String createRegexKey(String key) {
		String temp = "\"{0}\"\\s*:\\s*\"([^\"]+)\"";
		return StrUtil.occupy(temp, key);
	}
}
