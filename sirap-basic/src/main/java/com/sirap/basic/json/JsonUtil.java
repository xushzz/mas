package com.sirap.basic.json;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static String quote(Object obj) {
		String temp = obj + "";
		temp = temp.replace("\"", "\\\"");
		return StrUtil.occupy("\"{0}\"", temp);
	}
	
	public static Object parseObject(String jsonString) {
//		JsonBox beiring = new JsonBox(jsonString);
//		Object sea = beiring.getKing();
//		return sea;
		
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
		
		return toPrettyJson(parseObject(jsonString));
	}
	
	public static String getRawText(String jsonString) {
		if(EmptyUtil.isNullOrEmpty(jsonString)) {
			return null;
		}
		
		return toJson(parseObject(jsonString));
	}
	
	public static Object readObjectByPath(Object json, String path) {
		JsonReader master = new JsonReader(json);
		
		return master.readObject(path);
	}
	
	public static String createRegexKey(String key) {
		String temp = "\"{0}\"\\s*:\\s*\"([^\"]+)\"";
		return StrUtil.occupy(temp, key);
	}
}
