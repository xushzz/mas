package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sirap.basic.component.MexedOption;

public class OptionUtil {

	public static String readString(List<MexedOption> options, String targetKey) {
		Object temp = readOption(options, targetKey, true);
		if(temp == null) {
			return null;
		} else {
			return temp.toString();
		}
	}

	public static Boolean readBoolean(String options, String targetKey) {
		List<MexedOption> items = parseOptions(options);
		Boolean flag = readBoolean(items, targetKey);
		
		return flag;
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
	
	public static boolean readBoolean(List<MexedOption> options, String targetKey) {
		Object temp = readOption(options, targetKey, true);
		if(temp instanceof Boolean) {
			return (Boolean)temp;
		} else {
			return false;
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

	public static boolean readInteger(String options, String targetKey, boolean def) {
		List<MexedOption> items = parseOptions(options);
		boolean flag = readBoolean(items, targetKey, def);
		
		return flag;
	}
	
	public static int readInteger(List<MexedOption> options, String targetKey, int def) {
		Object temp = readOption(options, targetKey, true);
		if(temp instanceof Integer) {
			return (Integer)temp;
		} else {
			return def;
		}
	}
}
