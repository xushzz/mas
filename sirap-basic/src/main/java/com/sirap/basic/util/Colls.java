package com.sirap.basic.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.sirap.basic.component.comparator.StringSenseComparator;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.search.MexFilter;

public class Colls {

	public static List reverse(List list) {
		XXXUtil.nullCheck(list, "list");
		List newList = new ArrayList();
		
		for(int i = list.size() - 1; i >= 0; i--) {
			newList.add(list.get(i));
		}
		
		return newList;
	}
	
	public static <T> List<T> top(List<T> list, int size) {
		List<T> subList = new ArrayList<T>();
		for(int i = 0; i < list.size(); i++) {
			if(subList.size() < size) {
				subList.add(list.get(i));
			}
		}
		
		return subList;
	}
	
	public static <T> List<T> last(List<T> list, int size) {
		List<T> subList = new ArrayList<T>();
		int start = list.size() - size;
		if(start < 0) {
			start = 0;
		}
		for(int i = start; i < list.size(); i++) {
			subList.add(list.get(i));
		}
		
		return subList;
	}
	
	public static void putAndIncrease(Map<String, Integer> typeValueMap, String type) {
		putAndIncrease(typeValueMap, type, null);
	}
	
	public static void putAndIncrease(Map<String, Integer> typeValueMap, String type, String defaultValue) {
		if(EmptyUtil.isNullOrEmptyOrBlankOrLiterallyNull(type)) {
			type = defaultValue;
		}
		
		Integer count = typeValueMap.get(type);
		if(count == null) {
			typeValueMap.put(type, 1);
		} else {
			typeValueMap.put(type, count + 1);
		}
	}
	
	public static List<String> splitIntoRecords(String source, int charsPerRecord) {
		if(source == null) {
			return null;
		}
		
		List<String> records = new ArrayList<String>();
		if(source.isEmpty()) {
			records.add(source);
			return records;
		}
		char space = ' ';
		int len = source.length();
		int start = 0;
		while(start < len) {
			boolean isPrettyFormat = false;
			int end = start + charsPerRecord;
			if(end >= len) {
				isPrettyFormat = true;
				end = len;
			}
			
			String temp = source.substring(start, end);
			if(!isPrettyFormat) {
				char endOfLine = source.charAt(end - 1);
				char startOfNextLine = source.charAt(end);
				if(endOfLine == space || startOfNextLine == space) {
					isPrettyFormat = true;
				}
				if(!isPrettyFormat) {
					int lastSpace = temp.lastIndexOf(space);
					if(lastSpace > 0) {
						int diff = temp.length() - (lastSpace + 1);
						end = end - diff;
						temp = source.substring(start, end);
					}
				}
			}
			records.add(temp);
			start = end;
		}
		
		return records;
	}
	
	public static List<File> fileListOf(List items) {
		List<File> files = Lists.newArrayList();
		for(Object item:items) {
			File file = FileUtil.of(item);
			if(file != null) {
				files.add(file);
			}
		}
		
		return files;
	}

	public static Object findFirst(List rawItems, String mexCriteria, boolean isCaseSensitive) {
		List items = filterMix(rawItems, mexCriteria, isCaseSensitive);
		if(!items.isEmpty()) {
			return items.get(0);
		}
		
		return null;
	}
		
	public static List<MexItem> filterMix(List rawItems, String mexCriteria, boolean isCaseSensitive) {
		if(EmptyUtil.isNullOrEmpty(mexCriteria)) {
			return rawItems;
		}
		
		List<MexItem> mexItems = Lists.newArrayList();
		for(Object obj : rawItems) {
			if(obj instanceof MexItem) {
				mexItems.add((MexItem)obj);
			} else {
				mexItems.add(new MexObject(obj));
			}
		}

		List<MexItem> after = filter(mexItems, mexCriteria, isCaseSensitive);
		
		return after;
	}

	public static <T extends MexItem> List<T> filter(List<T> mexItems, String mexCriteria) {
		return filter(mexItems, mexCriteria, false);
	}
	
	public static <T extends MexItem> List<T> filter(List<T> mexItems, String mexCriteria, boolean isCaseSensitive) {
		return filter(mexItems, mexCriteria, isCaseSensitive, false);
	}
	
	public static <T extends MexItem> List<T> filter(List<T> mexItems, String mexCriteria, boolean isCaseSensitive, boolean noSplit) {
		if(EmptyUtil.isNullOrEmpty(mexCriteria)) {
			return mexItems;
		}
		
		MexFilter<T> filter = new MexFilter<T>(mexCriteria, mexItems, isCaseSensitive);
		filter.setNoSplit(noSplit);
		List<T> result = filter.process();
		
		return result;
	}
	
	/***
	 * 
	 * @param lines
	 * @param prefixes
	 * @return
	 */
	public static List<String> filterSome(List<String> lines, List<String> prefixesToIgnoreOneline) {
		List<String> items = Lists.newArrayList();
		for(String line : lines) {
			boolean toIgnore = StrUtil.startsWith(line.trim(), prefixesToIgnoreOneline);
			if(!toIgnore) {
				items.add(line);
			}
		}
		
		return items;
		
		
	}
	
	public static List<String> addPrefix(List<String> lines, String prefix) {
		List<String> items = Lists.newArrayList();
		for(String line : lines) {
			items.add(prefix + line);
		}
		
		return items;
	}
	
	public static List<String> sort(List<String> items, boolean sensitive) {
		Collections.sort(items, new StringSenseComparator(sensitive));
		return items;
	}
	
	public static <T extends Object> void sortIgnoreCase(List<T> items) {
		Collections.sort(items, new Comparator<T>(){

			@Override
			public int compare(Object a, Object b) {
				return a.toString().compareToIgnoreCase(b.toString());
			}
			
		});
	}
	
	public static List lineNumber(List<String> records, boolean align) {
		List<String> items = new ArrayList<>();
		int maxLen = (records.size() + "").length();
		for(int i = 0; i < records.size(); i++) {
			int lineNumber = i + 1;
			String line = "L" + lineNumber;
			if(align) {
				line = StrUtil.padRight(line, maxLen + 1);
			}
			
			items.add(line + " " + records.get(i));
		}
		
		return items;
	}

	public static List<String> sortAndMarkOccurrence(List<String> items, boolean sensitive) {
		List<String> sortedItems = new ArrayList<>(items);
		sort(sortedItems, sensitive);

		char[] marks = {'#', '*'};
		Map<String, Integer> box = new HashMap<>();
		
		for(String origin : sortedItems) {
			String item = origin;
			if(!sensitive) {
				item = origin.toLowerCase();
			}
			Integer count = box.get(item);
			if(count == null) {
				box.put(item, 1);
			} else {
				box.put(item, count + 1);
			}
		}

		int markIndex = -1;
		List<String> records = new ArrayList<>();
		String lastKey = null;
		int occurentIndex = 0;
		for(String origin : sortedItems) {
			String key = origin;
			if(!sensitive) {
				key = origin.toLowerCase();
			}
			int count = box.get(key);
			if(count == 1) {
				occurentIndex = 0;
				records.add(origin);
			} else if(count > 1) {
				if(!key.equals(lastKey)) {
					markIndex++;
				}
				occurentIndex++;
				String occurent = count > 5 ? (occurentIndex + "") : "";
				String mark = marks[markIndex%marks.length] + occurent + " ";
				records.add(mark + origin);
			}
			lastKey = key;
		}
		
		return records;
	}
	
	public static List listOf(Object obj) {
		return listOf(obj, "");
	}
	
	public static List listOf(Object obj, String options) {
		XXXUtil.nullCheck(obj, "obj");
		List items = null;
		if (obj instanceof List) {
			items = (List)obj;
		} else if(obj instanceof String) {
			String splitBy = OptionUtil.readString(options, "sp", ",");
			items = StrUtil.split(obj + "", splitBy);
		} else if (obj instanceof MexItem) {
			MexItem item = (MexItem)obj;
			items = item.toList(options);
		} else {
			XXXUtil.alert("Unsupported data type {0} of {1}", obj.getClass(), obj);
		}
		
		return items;
	}
	
	public static <T extends Object> List<T> distinctOf(List<T> items) {
		Set<T> okset = new LinkedHashSet<>(items);
		return Lists.newArrayList(okset);
	}
}
