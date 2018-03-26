package com.sirap.basic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.google.common.collect.Lists;
import com.sirap.basic.component.MexOption;
import com.sirap.basic.exception.DuplicationException;
import com.sirap.basic.exception.MexException;

public class OptionUtil {
	
	public static int readIntegerPRI(String options, String key, int def) {
		try {
			Integer value = readInteger(options, key);
			if(value != null) {
				return value;
			}
		} catch (DuplicationException ex) {
			throw ex;
		}
		
		return def;
	}
	
	public static Integer readInteger(String options, String key) {
		OptionUtil.checkOptionKey(key);
		String strValue = readString(options, key);
		
		return MathUtil.toInteger(strValue);
	}
	
	public static String readString(String options, String key, String def) {
		try {
			String value = readString(options, key);
			if(value != null) {
				return value;
			}
		} catch (DuplicationException ex) {
			throw ex;
		}
		
		return def;
	}

	public static String readString(String options, String key) {
		return readString(options, key, true);
	}
	
	public static String readString(String options, String key, boolean handleSpace) {
		if(EmptyUtil.isNullOrEmpty(options)) {
			return null;
		}
		
		OptionUtil.checkOptionKey(key);

		Matcher ma = StrUtil.createMatcher("(,|^)\\s*" + key + "\\s*=\\s*([^,]+)", options);
		
		String value = null;
		int count = 0;
		
		while(ma.find()) {
			count++;
			OptionUtil.checkDuplicativeKeys(key, count);
			value = ma.group(2);
			if(handleSpace) {
				value = value.replace("\\\\s", " ");
				value = value.replace("\\\\c", ",");
			}
		}
		
		return value;
	}
	
	public static boolean readBooleanPRI(String options, String key, boolean def) {
		try {
			Boolean flag = readBoolean(options, key);
			if(flag != null) {
				return flag;
			}
		} catch (DuplicationException ex) {
			throw ex;
		}
		
		return def;
	}
	
	public static Boolean readBoolean(String options, String key) {
		if(EmptyUtil.isNullOrEmpty(options)) {
			return null;
		}
		checkOptionKey(key);

		Matcher ma = StrUtil.createMatcher("(,|^)\\s*(\\+|-)" + key + "\\s*(,|$)", options);
		
		Boolean flag = null;
		int count = 0;
		
		while(ma.find()) {
			count++;
			checkDuplicativeKeys(key, count);
			flag = StrUtil.equals(ma.group(2), "+");
		}
		
		return flag;
	}
	
	public static String mergeOptions(String highPriority, String lowPriority) {
		List<MexOption> listA = parseOptions(highPriority);
		List<MexOption> listB = parseOptions(lowPriority);
		
		List<MexOption> listAll = Lists.newArrayList(listA);
		for(MexOption itemB : listB) {
			if(listA.indexOf(itemB) >= 0) {
				continue;
			}
			
			listAll.add(itemB);
		}
		
		return StrUtil.connectWithComma(listAll);
	}
	
	public static List<MexOption> parseOptions(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return Lists.newArrayList();
		}
		List<MexOption> options = new ArrayList<>();
		
		List<String> params = StrUtil.split(source);
		for(String param : params) {
			MexOption mo = new MexOption();
			if(mo.parse(param)) {
				options.add(mo);
			}
		}
		
		return options;
	}
	
	/***
	 * alphanumeric and underscore, aka \w
	 * good: 12k,hu232,ko232,232,1999,k_3812
	 * bad: &kf23,ji f23
	 */
	public static void checkOptionKey(String key) {
		if(!key.matches("\\w+")) {
			String msg = "Invalid key [{0}], chars should either be alphanumeric or undersocre.";
			throw new MexException(msg, key);
		}
	}
	
	public static void checkDuplicativeKeys(String key, int count) {
		if(count > 1) {
			throw new DuplicationException("Found duplicative keys: {0}", key);
		}
	}
	
	public static void suckOptions(StringBuffer source, StringBuffer options) {
		List<String> regexes = Lists.newArrayList();
		//[$x=12 ]
		//[ $x=14 ]
		//[ $x=15]
		//[$x=16]
		regexes.add("^\\$(\\S+)\\s");
		regexes.add("\\s\\$(\\S+)\\s");
		regexes.add("\\s\\$(\\S+)$");
		regexes.add("^\\$(\\S+)$");
		
		String tempSource = source.toString().trim();

		for(String regex : regexes) {
			Matcher ma = StrUtil.createMatcher(regex, tempSource);
			if(ma.find()) {
				tempSource = tempSource.replace(ma.group(0), " ").trim();
				source.setLength(0);
				source.append(tempSource);
				
				String tempOptions = ma.group(1);
				String merge = OptionUtil.mergeOptions(options.toString(), tempOptions);
				options.setLength(0);
				options.append(merge);
				suckOptions(source, options);
				break;
			}
		}
	}
	
	public static List<String> suckOptions(String raw) {
		StringBuffer source = StrUtil.sb(raw);
		StringBuffer options = StrUtil.sb();
		
		suckOptions(source, options);
		
		return Lists.newArrayList(source.toString(), options.toString());
	}
}
