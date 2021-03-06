package com.sirap.basic.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.sirap.basic.component.Konstants;
import com.sirap.basic.exception.MexException;

public class StrUtil {

	public static final String DIGITS = "0123456789";
	public static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
	public static final String LETTERS_UPPERCASED = LETTERS.toUpperCase();
	public static final String ALPHANUMERIC = DIGITS + LETTERS;

	public static boolean isDigitsOnly(String str) {
		if(str == null) return false;
		
		Matcher m = Pattern.compile("\\d+").matcher(str);
		
		return m.matches();
	}
	
	public static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}
	
	public static boolean containsNoneOfAplhanumeric(String source) {
		if(source == null) return true;
		
		Matcher m = Pattern.compile("[A-Za-z\\d]+").matcher(source);
		
		return !m.find();
	}
	
	public static boolean containsDigit(String source) {
		if(source == null) return false;
		
		Matcher m = Pattern.compile("\\d+").matcher(source);
		
		return m.find();
	}
	
	public static boolean containsNoneOfDigit(String source) {
		if(source == null) return true;
		
		Matcher m = Pattern.compile("\\d+").matcher(source);
		
		return !m.find();
	}
	
	public static Integer extractFirstInteger(String str) {
		if(str == null) return null;
		
		Matcher m = Pattern.compile("\\d+").matcher(str);
		if(m.find()) {
			return Integer.parseInt(m.group());
		}
		
		return null;
	}
	
	public static String takeDigitsOnly(String str) {
		XXXUtil.nullCheck(str, "jack");
		
		String regex = "\\D";
		String value = str.replaceAll(regex, "");
		
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Integer> extractIntegers(String str) {
		if(str == null) return Collections.EMPTY_LIST;
		
		List<Integer> list = new ArrayList<Integer>();
		
		Matcher m = Pattern.compile("\\d+").matcher(str);
		while(m.find()) {
			list.add(Integer.parseInt(m.group()));
		}
		
		return list;
	}

	@SuppressWarnings("rawtypes")
	public static String connect(List list, int fromIndex) {
		StringBuffer sb = new StringBuffer();
		for(int i = fromIndex; i < list.size(); i++) {
			sb.append(list.get(i));
		}
		
		return sb.toString();
	}
	
	public static String conn(Object... items) {
		return connect(Arrays.asList(items), "");
	}

	public static String connWithSpace(Object... items) {
		return connect(Arrays.asList(items), " ");
	}

	public static String connWithCommaSpace(Object... items) {
		return connect(Arrays.asList(items), ", ");
	}
	
	@SuppressWarnings("rawtypes")
	public static String connect(List items) {
		return connect(items, "");
	}

	@SuppressWarnings("rawtypes")
	public static String connectWithCommaSpace(List items) {
		return connect(items, ", ");
	}

	@SuppressWarnings("rawtypes")
	public static String connectWithSpace(List items) {
		return connect(items, " ");
	}

	@SuppressWarnings("rawtypes")
	public static String connectWithLineSeparator(List items) {
		return connect(items, Konstants.NEWLINE);
	}
	
	@SuppressWarnings("rawtypes")
	public static String connect(List items, String connector) {
		XXXUtil.nullCheckOnly(items);
		
		StringBuffer sb = new StringBuffer();

		boolean theFirstOne = true;
		for(Object item : items) {
			if(!theFirstOne) {
				sb.append(connector);
			}
			sb.append(item);
			theFirstOne = false;
		}

		return sb.toString();
	}
	
	public static String regexOfKeys(Map<String, ?> map) {
		List<String> keys = Lists.newArrayList(map.keySet());
		return connect(keys, "|");
	}
	
	/***
	 * default with regular expression ",|;"
	 * @param source
	 * @return
	 */
	public static List<String> splitByRegex(String source) {
		return splitByRegex(source, ",|;");
	}
	
	/***
	 * The Java and Scala split methods operate in two steps like this:
	 * 1) First, split the string by delimiter. The natural consequence is that if the string does not contain the delimiter, array with just the string is returned.
	 * 2) Second, remove all the rightmost empty strings. This is the reason ",,,".split(",") returns empty array.
	 * 
	 * Replaced by StrUtil.split(String source, char delimiter, boolean trimEachItem) returning List
	 * @param source
	 * @param regex
	 * @return
	 */
	public static List<String> splitByRegex(String source, String regex) {
		return splitByRegex(source, regex, true);
	}

	@SuppressWarnings("unchecked")
	public static List<String> splitByRegex(String source, String regex, boolean trimEachItem) {
		if(EmptyUtil.isNullOrEmptyOrBlank(source)) {
			return Collections.EMPTY_LIST;
		}
		
		XXXUtil.nullCheck(regex, "regex");
		String[] strs = source.trim().split(regex);
		List<String> strList = Lists.newArrayList();
		for(String str : strs) {
			if(trimEachItem) {
				strList.add(str.trim());
			} else {
				strList.add(str);
			}
		}
		
		return strList;
	}
	
	public static List<String> split(String source) {
		return split(source, ",");
	}
	
	public static List<String> split(String source, char delimiter) {
		return split(source, delimiter + "", true);
	}
	
	/***
	 * : colon
	 * ; semicolon
	 * , comma
	 * . stop
	 * @param source
	 * @param delimiter
	 * @param trimEachItem
	 * @return
	 */
	public static List<String> split(String source, String delimiter) {
		return split(source, delimiter, true);
	}

	public static List<String> split(String source, String delimiter, boolean trimEachItem) {
		XXXUtil.nullCheck(source, "source");
		XXXUtil.nullCheck(delimiter, "delimiter");
		
		if(source.isEmpty()) {
			return Lists.newArrayList();
		}
		
		if(delimiter.isEmpty()) {
			return splitByRegex(source, "");
		}
		
		int lenOfDelimiter = delimiter.length();
		
		int len = source.length();
		int index = source.indexOf(delimiter);
		List<String> items = new ArrayList<>();
		int lastIndex = 0;
		while(index >= 0 && index < len) {
			String sub = source.substring(lastIndex, index);
			if(trimEachItem) {
				items.add(sub.trim());
			} else {
				items.add(sub);
			}
			lastIndex = index + lenOfDelimiter;
			index = source.indexOf(delimiter, lastIndex);
		}
		
		String sub = source.substring(lastIndex);
		if(trimEachItem) {
			items.add(sub.trim());
		} else {
			items.add(sub);
		}
		
		return items;
	}
	
	public static boolean containsIgnoreCase(String source, List<String> keyWords) {
		if(EmptyUtil.isNullOrEmpty(source) || EmptyUtil.isNullOrEmpty(keyWords)) {
			return false;
		}
		
		for(String keyWord : keyWords) {
			if(source.toLowerCase().contains(keyWord.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean containsIgnoreCase(String[] keyWords, String criteria) {
		if(EmptyUtil.isNullOrEmpty(criteria) || EmptyUtil.isNullOrEmpty(keyWords)) {
			return false;
		}
		
		for(int i = 0; i < keyWords.length; i++) {
			if(keyWords[i].toLowerCase().contains(criteria.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isIn(String target, List<String> items) {
		for(String item : items) {
			if(equals(item, target)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean contains(String[] keyWords, String criteria) {
		if(EmptyUtil.isNullOrEmpty(criteria) || EmptyUtil.isNullOrEmpty(keyWords)) {
			return false;
		}
		
		for(int i = 0; i < keyWords.length; i++) {
			if(keyWords[i].contains(criteria)) {
				return true;
			}
		}
		
		return false;
	}

	public static boolean contains(String source, List<String> keyWords) {
		if(EmptyUtil.isNullOrEmpty(source) || EmptyUtil.isNullOrEmpty(keyWords)) {
			return false;
		}
		
		for(String keyWord : keyWords) {
			if(source.contains(keyWord)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean contains(String source, String target) {
		return contains(source, target, 1, false);
	}
	
	public static boolean contains(String source, String target, boolean caseSensitive) {
		return contains(source, target, 1, caseSensitive);
	}

	public static boolean contains(String source, String target, int minimunLength) {
		return contains(source, target, minimunLength, false);
	}
	
	public static boolean contains(String source, String target, int minimunLength, boolean caseSensitive) {
		if(source == null || target == null || target.length() < minimunLength) {
			return false;
		}
		
		if(caseSensitive) {
			return source.contains(target);
		} else {
			return source.toLowerCase().contains(target.toLowerCase());
		}
	}
	
	public static boolean containsAnyChar(String source, String keyChars) {
		if(EmptyUtil.isNullOrEmpty(source) || EmptyUtil.isNullOrEmpty(keyChars)) {
			return false;
		}
		
		for(int i = 0; i < keyChars.length(); i++) {
			char ch = keyChars.charAt(i);
			if(source.indexOf(ch + "") >= 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean startsWith(String source, String target) {
		return startsWith(source, target, 1);
	}
	
	public static boolean startsWith(String source, String target, int minimunLength) {
		if(source == null || target == null || target.length() < minimunLength) {
			return false;
		}

		return source.toLowerCase().startsWith(target.toLowerCase());
	}
	
	public static boolean endsWith(String source, String target) {
		return endsWith(source, target, 1);
	}
	
	public static boolean endsWith(String source, String target, int minimunLength) {
		if(source == null || target == null || target.length() < minimunLength) {
			return false;
		}

		return source.toLowerCase().endsWith(target.toLowerCase());
	}
	
	public static boolean is0(String source) {
		return StrUtil.equals(source, "0");
	}
	
	public static boolean equals(String source, String target) {
		if(source == null || target == null) {
			return false;
		}

		return source.equalsIgnoreCase(target);
	}
	
	public static boolean equalsCaseSensitive(String source, String target) {
		if(source == null || target == null) {
			return false;
		}

		return source.equals(target);
	}
	
	public static boolean existsIgnoreCase(String[] targetArr, String key) {
		return exists(targetArr, key, true);
	}
			
	public static boolean exists(String[] targetArr, String key, boolean ignoreCase) {
		if(targetArr == null || key == null) {
			return false;
		}
		
		for(int i = 0; i < targetArr.length; i++) {
			if(key.equals(targetArr[i])) {
				return true;
			} else if(ignoreCase && key.equalsIgnoreCase(targetArr[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean startsWith(String source, String[] keyArr) {
		return startsWith(source, keyArr, true);
	}
	
	public static boolean startsWith(String source, List<String> keys) {
		String[] keyArr = new String[keys.size()];
		keyArr = keys.toArray(keyArr);
		
		return startsWith(source, keyArr, true);
	}
	
	public static boolean startsWith(String source, String[] keyArr, boolean ignoreCase) {
		if(source == null || keyArr == null) {
			return false;
		}
		
		for(int i = 0; i < keyArr.length; i++) {
			boolean flag = false;
			String key = keyArr[i];
			if(ignoreCase) {
				flag = source.toLowerCase().startsWith(key.toLowerCase());
			} else {
				flag = source.startsWith(key);
			}
			
			if(flag) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String truncate(String source, int targetLength) {
		if(source == null) {
			return null;
		}
		
		if(source.length() < targetLength) {
			return source;
		} else {
			return source.substring(0, targetLength);
		}
	}
	
	public static String padRight(String source, int targetLength) {
		return pad(source, false, targetLength, " ", true);
	}
	
	public static String padRight(String source, int targetLength, String whatToFill) {
		return pad(source, false, targetLength, whatToFill, true);
	}

	public static String padRightAscii(String source, int targetLength) {
		return pad(source, false, targetLength, " ", false);
	}
	
	public static String padRightAscii(String source, int targetLength, String whatToFill) {
		return pad(source, false, targetLength, whatToFill, false);
	}
	
	public static int countOfAscii(String source) {
		int len = 0;
		for(int k = 0; k < source.length(); k++) {
			char car = source.charAt(k);
			len += countOfAscii(car);
		}
		
		return len;
	}
	
	public static int countOfAscii(char car) {
		byte[] bs;
		try {
			bs = (car + "").getBytes(Konstants.CODE_GBK);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MexException(ex);
		}
		return bs.length;
	}
	
	public static String firstK(String source, int kchars) {
		if(source.length() < kchars) {
			return source;
		}
		
		return source.substring(0, kchars) + "...";
	}
	
	public static String padLeft(String source, int targetLen) {
		return pad(source, true, targetLen, " ", true);
	}
	
	public static String padLeft(String source, int targetLen, String whatToFill) {
		return pad(source, true, targetLen, whatToFill, true);
	}

	public static String padLeftAscii(String source, int targetLen) {
		return pad(source, true, targetLen, " ", false);
	}
	
	public static String padLeftAscii(String source, int targetLen, String whatToFill) {
		return pad(source, true, targetLen, whatToFill, false);
	}
	
	public static String pad(String source, boolean padLeft, int targetLength, String whatToFill, boolean lengthAsCharNumbers) {
		XXXUtil.nullCheck(source, "source");
		XXXUtil.nullCheck(whatToFill, "whatToFill");
		
		int currentLength = lengthAsCharNumbers ? source.length() : countOfAscii(source);
		int diff = targetLength - currentLength;
		if(diff <= 0) {
			return source;
		}
		
		StringBuffer some = sb();
		int step = 1;
		if(!lengthAsCharNumbers) {
			step = countOfAscii(whatToFill);
			int remain = diff % step;
			if(remain != 0) {
				int low = targetLength - remain, high = low + step;
				XXXUtil.alert("Can't sharply pad {0} with {1} to get length {2}, {3} or {4} will be good.", source, whatToFill, targetLength, low, high);
			}
		}
		for(int i = 0; i < diff; i = i + step) {
			some.append(whatToFill);
		}
		
		if(padLeft) {
			return some.append(source).toString();
		} else {
			return source + some.toString();
		}
	}
	
	public static String spaces(int times) {
		return repeat(" ", times);
	}

	public static String repeat(char c, int times) {
		return repeat(c + "", times);
	}
	
	public static String repeat(String source, int times) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < times; i++) {
			sb.append(source);
		}
		
		return sb.toString();
	}
	
	public static String signValue(double value) {
		String sign = value >= 0 ? "+" : "";
		return sign + value;
	}
	
	public static String repeatNicely(char c, int number) {
		return repeatNicely(c, number, number);
	}
	
	public static String repeatNicely(char c, int number, int max) {
		String temp = StrUtil.repeat(c, (number <= max ? number : max));
		if(number > 2) {
			temp += " (" + number + ")";
		}
		
		return temp;
	}

	public static String parseParam(String regex, String source) {
		XXXUtil.nullCheck(regex, "regex");
		XXXUtil.nullCheck(source, "source");
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(source);
		if(m.matches()) {
			return m.group(1).trim();
		}
		
		return null;
	}

	public static String[] parseParams(String regex, String source) {
		return parseParams(regex, source, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}
	
	public static String[] parseParams(String regex, String source, int flags) {
		if(regex == null || source == null) {
			return null;
		}
		
		Matcher m = Pattern.compile(regex, flags | Pattern.DOTALL).matcher(source);
		if(m.matches()) {
			int count = m.groupCount();
			String[] strArr = new String[count];
			for(int i=0; i < count; i++) {
				String temp = m.group(i+1);
				if(temp != null) {
					strArr[i] = temp.trim();
				}
			}
			
			return strArr;
		}
		
		return null;
	}
	
	public static boolean isRegexMatched(String regex, String source) {
		if(source == null) {
			return false;
		}
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);

		if(m.matches()) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isRegexFound(String regex, String source) {
		Matcher m = createMatcher(regex, source);
		if(m.find()) {
			return true;
		}
		
		return false;
	}
	
	public static List<String> findAllMatchedItems(String regex, String source) {
		return findAllMatchedItems(regex, source, 0);
	}
	
	public static List<String> findAllMatchedItems(String regex, String source, int groupIndex) {
		if(regex == null || source == null) {
			return null;
		}

		List<String> list = new ArrayList<>();
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String item = m.group(groupIndex);
			list.add(item);
		}
		
		return list;
	}

	public static List<List<String>> findAllMatchedListedItems(String regex, String source, boolean caseSensitive) {
		if(regex == null || source == null) {
			return null;
		}

		List<List<String>> allItems = new ArrayList<>();
		
		Matcher m = createMatcher(regex, source, caseSensitive);
		while(m.find()) {
			int count = m.groupCount();
			
			List<String> items = new ArrayList<String>();
			
			if(count == 0) {
				items.add(m.group());
			} else {
				for(int i = 0; i < count; i++) {
					String temp = m.group(i+1);
					if(temp != null) {
						items.add(temp.trim());
					}
				}
			}
			
			allItems.add(items);
		}
		
		return allItems;
	}

	public static String findFirstMatchedItem(String regex, String source) {
		if(regex == null || source == null) {
			return null;
		}
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		if(m.find()) {
			return m.group(1);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param source What is your {0}, {1}?
	 * @param values Name, Buddy
	 * @return What is your Name, Buddy?
	 */
	public static String occupy(String source, Object... values) {
		if(source == null) {
			return null;
		}
		Pattern p = Pattern.compile("\\{(\\d{1,2})\\}");
		Matcher m = p.matcher(source);
		String temp = source;
		while(m.find()) {
			String key = m.group();
			int position = Integer.parseInt(m.group(1));
			if(position >=0 && position < values.length) {
				temp = temp.replace(key, values[position] + "");
			}
		}
		
		return temp;
	}
	
	/****
	 * 
	 * @param source, and ${save} as JPG file, other formats are ${image.formats}
	 * @param params
	 * @return
	 */
	public static String occupyKeyValues(String source, String key, Object value) {
		return occupyKeyValues(source, Amaps.createMap(key, value));
	}
	
	public static String occupyKeyValues(String source, Map<String, Object> params) {
		if(EmptyUtil.isNullOrEmpty(params)) {
			return source;
		}
		
		String regex = "\\$\\{([^${}()]{1,100})\\}";
		
		String temp = source;
		
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(source);
		while(m.find()) {
			String whole = m.group(0);
			String item = m.group(1);
			Object obj = params.get(item);
			if(obj != null) {
				temp = temp.replace(whole, obj.toString());
			}
		}
		
		return temp;
	}

	public static String mask(String plainText) {
		String temp = plainText.replaceAll(".", "*");
		return temp;
	}
	
	public static String maskSome(String plainText) {
		int len = plainText.length();
		if(len <= 2) {
			return plainText;
		}
		
		char firstChar = plainText.charAt(0);
		char lastChar = plainText.charAt(len  - 1);
		String temp = plainText.substring(1, len - 1);
		temp = temp.replaceAll(".", "*");
		temp = firstChar + temp + lastChar;
		
		return temp;
	}
	
	public static String displaySome(String source, int len) {
		int size = source.length();
		if(size <= len) {
			return source;
		}
		
		String temp = source.substring(0, len);
		temp += "..., " + len + " chars out of " + size;
		
		return temp;
	}
	
	/***
	 * 
	 * @param source flatCamelCase
	 * @return flat camel case
	 */
	public static String flatCamelCase(String source) {
		Pattern p = Pattern.compile("[A-Z]");
		Matcher m = p.matcher(source);
		String temp = source;
		while(m.find()) {
			String target = m.group(0);
			temp = temp.replace(target, " " + target.toLowerCase());
		}
		
		return temp;
	}
	
	public static String reduceMultipleSpacesToOne(String source) {
		String temp = source.replaceAll("\t", " ");
		temp = temp.replaceAll("\\s{2,}", " ");
		
		return temp;
	}
	
	public static <T extends Object> int maxAsciiLengthOf(List<T> records) {
		int max = 0;
		for(T item : records) {
			int len = countOfAscii(item + "");
			if(len > max) {
				max = len;
			}
		}
		
		return max;
	}
	
	public static <T extends Object> int maxLengthOf(List<T> records) {
		int max = 0;
		for(T item : records) {
			int len = (item + "").length();
			if(len > max) {
				max = len;
			}
		}
		
		return max;
	}
	
	public static String bytesToHexString(byte[] bytes) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String shaHex = Integer.toHexString(bytes[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexString.append(0);
			}
			hexString.append(shaHex);
		}
		
		return hexString.toString();
	}
	
	public static byte hexCharsToByte(String twoHex) {
		if(!StrUtil.isRegexMatched("[0-9A-F]{2}", twoHex)) {
			throw new MexException("invalid source " + twoHex);
		}
		
		int intValue = Integer.parseInt(twoHex, 16);
		
		return (byte)(intValue);
	}
	
	public static String secondsCost(long start, long end) {
		double seconds = Math.abs(end - start + 0.0) / Konstants.MILLI_PER_SECOND;
		String bd = MathUtil.formatNumber(seconds, 2);
		String template = "{0} second{1}.";
		String suffix = seconds > 1 ? "s" : "";
		String value = StrUtil.occupy(template, bd, suffix);
		
		return value;
	}
	
	public static String removePointZeroes(String source) {
		if(source.indexOf('.') >= 0) {
			String temp = source.replaceAll("\\.?0*$", "");
			return temp;
		} else {
			return source;
		}
	}

	public static String getUrlParams(String wholeUrl) {
		String regex = "\\?(.+)";
		String params =  findFirstMatchedItem(regex, wholeUrl);
		if(params == null) {
			params = "";
		}
		
		return params;
	}
	
	/***
	 * "\\uE59BBE\\uE781B5"
	 * @param utf8EncodedString
	 * @return
	 */
	public static String utf8ToWhatever(String source) {
		return XCodeUtil.replaceHexChars(source, Konstants.CODE_UTF8);
	}
	
	/***
	 * Various regex.
	 * http://blog.csdn.net/lcugym/article/details/22173117
	 * @param regex
	 * @param content
	 * @return
	 */
	public static Matcher createMatcher(String regex, String content) {
		return createMatcher(regex, content, false);
	}
	
	public static Matcher createMatcher(String regex, String content, boolean caseSensitive) {
		Pattern pa = null;
		try {
			pa = caseSensitive ? Pattern.compile(regex) : Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} catch (Exception ex) {
			throw new MexException(ex);
		}
		
		Matcher ma = pa.matcher(content);

		return ma;
	}
	
	public static String of(Object obj) {
		String temp;
		if(obj == null) {
			temp = null;
		} else {
			Class<?> carrie = obj.getClass();
			boolean isObjArr = obj instanceof Object[];
			if(Konstants.PRIMITIVE_ARRAY_CLASSES.contains(carrie) || isObjArr) {
				Class<?> clazzParam = isObjArr ? Object[].class : carrie;
				Object value = ObjectUtil.execute(Arrays.class, "toString", new Class[]{clazzParam}, obj);
				temp = value + "";
				temp = temp.replaceAll("^\\[", "{");
				temp = temp.replaceAll("\\]$", "}");
				int size = temp.length() == 2 ? 0 : split(temp).size();
				String type = carrie.getSimpleName();
				type = type.replace("[", "[" + size);
				temp = type + temp;
			} else {
				temp = obj.toString();
			}
		}

		if(temp != null) {
			if(temp.isEmpty()) {
				temp = Konstants.FAKED_EMPTY;
			} else {
				temp = temp.replaceAll("(^\\s|\\s$)", Konstants.FAKED_SPACE);
			}
		}
		
		return temp;
	}

	public static String useSeparator(Object... items) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < items.length; i++) {
			String item = items[i] + "";
			sb.append(item);
			
			if(i == items.length - 1) {
				break;
			}

			if(!item.endsWith("/") && !item.endsWith("\\")) {
				sb.append("/");
			}
		}
		
		return sb.toString();
	}
	
	public static String useDelimiter(String delimiter, Object... items) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < items.length; i++) {
			String item = items[i] + "";
			sb.append(item);
			
			if(i == items.length - 1) {
				break;
			}

			if(!item.endsWith(delimiter)) {
				sb.append(delimiter);
			}
		}
		
		return sb.toString();
	}
	
	public static String useSlash(Object... items) {
		String delimiter = "/";
		return useDelimiter(delimiter, items);
	}
	
	public static StringBuffer sb() {
		return new StringBuffer();
	}
	
	public static StringBuffer sb(String value) {
		return new StringBuffer(value);
	}
	
	public static String insertComma(String value) {
		StringBuffer sb = sb(value);
		int len = value.length();
		int index = len - 3;
		while(index > 0) {
			sb.insert(index, ",");
			index = index - 3;
		}
		
		return sb.toString();
	}
	
	public static List<String> listOf(String[] arr) {
		List<String> items = Lists.newArrayList();
		if(!EmptyUtil.isNullOrEmpty(arr)) {
			for(int i = 0; i < arr.length; i++) {
				items.add(arr[i]);
			}
		}
		
		return items;
	}
	
	public static String addHttpProtoclIfNeeded(String url) {
		String temp = url;
		String key = "http";
		if(!StrUtil.startsWith(url, "http")) {
			temp = key + "://" + url;
		}
		
		return temp;
	}
	
	/***
	 * james dean => James Dean
	 * @param source
	 * @return
	 */
	public static String uppercaseInitials(String source) {
		String regex = "\\s[a-z]|^[a-z]";
		StringBuffer sb = sb();
		Matcher ma = createMatcher(regex, source);
		while(ma.find()) {
			String stuff = ma.group(0).toUpperCase();
			ma.appendReplacement(sb, stuff);
		}
		ma.appendTail(sb);
		
		return sb.toString();
	}
	
	public static boolean isPositive(String str) {
		return str != null && !str.isEmpty();
	}
	
	public static String recoverSpace(String source) {
		if(EmptyUtil.isNullOrEmpty(source)) {
			return null;
		}
		
		String regex = "#s(\\d{0,3})";
		Matcher mat = StrUtil.createMatcher(regex, source);
		StringBuffer sb = StrUtil.sb();
		while(mat.find()) {
			String repeat = mat.group(1);
			String what = " ";
			int times = repeat.isEmpty() ? 1 : Integer.parseInt(repeat);
			String stuff = StrUtil.repeat(what, times);
			mat.appendReplacement(sb, stuff);
		}
		mat.appendTail(sb);
		
		return sb.toString();
	}
	
	/***
	 * 
	 * @param source
	 * @return
	 */
	public static Number numberOf(String source) {
		String[] params = StrUtil.parseParams(Konstants.REGEX_NUMBER, source);
		if(params == null) {
			return null;
		}
		
		BigDecimal jack = new BigDecimal(source);
		
		return jack;
	}
	
	/***
	 * if given char is preceded by zero or double backslashes
	 * @param source
	 * @param nowIndex
	 * @return
	 */
	public static boolean isPrecededByEvenBackslashes(String source, int nowIndex) {
		int count = 0;
		int previous = nowIndex - 1;
//		D.pla(source.toCharArray());
		while(previous >= 0 && source.charAt(previous) == '\\') {
			count++;
			previous = previous - 1;
		}
		
		return count % 2 == 0;
	}
	
	public static String newString(String origin, String encodingFrom, String encodingTo) {
		try {
			String temp = new String(origin.getBytes(encodingFrom), encodingTo);
			return temp;
		} catch (UnsupportedEncodingException ex) {
			throw new MexException(ex);
		}
	}
}