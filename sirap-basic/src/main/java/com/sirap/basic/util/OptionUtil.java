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
		
		if(ma.find()) {
			count++;
			OptionUtil.checkDuplicativeKeys(key, count);
			value = ma.group(2);
			if(handleSpace) {
				String regex = "#([sc])(\\d{0,3})";
				Matcher mat = StrUtil.createMatcher(regex, value);
				StringBuffer sb = StrUtil.sb();
				while(mat.find()) {
					String flag = mat.group(1);
					String repeat = mat.group(2);
					String what = null;
					if(StrUtil.equals("s", flag)) {
						what = " ";
					} else if(StrUtil.equals("c", flag)) {
						what = ",";
					}
					int times = repeat.isEmpty() ? 1 : Integer.parseInt(repeat);
					String stuff = StrUtil.repeat(what, times);
					mat.appendReplacement(sb, stuff);
				}
				mat.appendTail(sb);
				value = sb.toString();
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
		
		return StrUtil.connectWithCommaSpace(listAll);
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

	/****
	 * 
		[$x=12 ]
		[$x=12>]
		[$x=12 KK]
		[$x=12>KK]
		[AA $x=14 KK]
		[AA $x=14>KK]
		[AA $x=15]
		[$x=16]
	 * @param source
	 * @param optionsBox
	 */
	public static void suckOptions(StringBuffer source, StringBuffer optionsBox) {
		List<String> regexes = Lists.newArrayList();
		regexes.add("(^\\$|\\s\\$)([^\\s>]+)[\\s>]");
		regexes.add("(^\\$|\\s\\$)([^\\s>]+)$");
		String temp = source.toString().trim();
		
		for(String regex : regexes) {
			Matcher ma = StrUtil.createMatcher(regex, temp);
			while(ma.find()) {
				String options = ma.group(2);
				temp = temp.replace("$" + options, "").trim();
//				D.pl(regex, options, temp);
				source.setLength(0);
				source.append(temp);
				String merge = OptionUtil.mergeOptions(optionsBox.toString(), options);
				optionsBox.setLength(0);
				optionsBox.append(merge);
				
				suckOptions(source, optionsBox);
				
				break;
			}
		}
	}
	
	public static List<String> suckOptions(String raw) {
		StringBuffer source = StrUtil.sb(raw);
		StringBuffer options = StrUtil.sb();
		suckOptions(source, options);
//		D.pl(100, source, options);
		return Lists.newArrayList(source.toString(), options.toString());
	}
}
