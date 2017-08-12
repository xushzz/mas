package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.component.MexedOption;

public class OptionUtil {

	public static int readInteger(String options, String targetKey, int def) {
		List<MexedOption> items = parseOptions(options);
		return readInteger(items, targetKey, def);
	}
	
	public static int readInteger(List<MexedOption> options, String targetKey, int def) {
		Object temp = readOption(options, targetKey, true);
		if(temp instanceof Integer) {
			return (Integer)temp;
		} else {
			return def;
		}
	}
	
	public static String readString(List<MexedOption> options, String targetKey) {
		return readString(options, targetKey, null);
	}
	
	public static String readString(List<MexedOption> options, String targetKey, String def) {
		Object temp = readOption(options, targetKey, true);
		if(temp instanceof String) {
			return (String)temp;
		} else {
			return def;
		}
	}

	public static String readString(String options, String targetKey, String def) {
		List<MexedOption> items = parseOptions(options);
		return readString(items, targetKey, def);
	}

	public static boolean readBoolean(String options, String targetKey, boolean def) {
		List<MexedOption> items = parseOptions(options);
		boolean flag = readBoolean(items, targetKey, def);
		
		return flag;
	}
	
	public static boolean readBoolean(List<MexedOption> options, String targetKey, boolean def) {
		Object temp = readOption(options, targetKey, true);
		if(temp instanceof Boolean) {
			return (Boolean)temp;
		} else {
			return def;
		}
	}
	
	public static Object readOption(List<MexedOption> options, String targetKey) {
		return readOption(options, targetKey, true);
	}
	
	public static Object readOption(List<MexedOption> options, String targetKey, boolean ignoreCase) {
		for(MexedOption mo : options) {
			String key = mo.getName();
			Object value = mo.getValue();
			if(ignoreCase) {
				if(StrUtil.equals(targetKey, key)) {
					return value;
				}
			} else {
				if(StrUtil.equalsCaseSensitive(targetKey, key)) {
					return value;
				}
			}
			
		}
		
		return null;
	}
	
	public static List<MexedOption> parseOptions(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return Collections.EMPTY_LIST;
		}
		List<MexedOption> options = new ArrayList<>();
		
		List<String> params = StrUtil.split(source);
		for(String param : params) {
			MexedOption mo = new MexedOption();
			if(mo.parse(param)) {
				options.add(mo);
			}
		}
		
		return options;
	}
}
