package com.sirap.basic.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sirap.basic.component.StringSenseComparator;
import com.sirap.basic.domain.MexFile;
import com.sirap.basic.domain.MexItem;
import com.sirap.basic.domain.MexObject;
import com.sirap.basic.search.MexFilter;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CollectionUtil {

	public static List reverseOrder(List list) {
		XXXUtil.nullCheck(list, "list");
		List newList = new ArrayList();
		
		for(int i = list.size() - 1; i >= 0; i--) {
			newList.add(list.get(i));
		}
		
		return newList;
	}

	public static <T> List<T> convert(List list) {
		List<T> newList = new ArrayList<T>();
		
		if(list != null) {
			newList.addAll(list);
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
	
	public static <T extends MexItem> List<T> map2List(Map<String, ? extends Collection<T>> map) {
		List<T> allItems = new ArrayList<T>();
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			allItems.addAll(map.get(key));
		}
		
		return allItems;
	}
	
	public static List map2RegularList(Map map) {
		List allItems = new ArrayList();
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			Object value = map.get(key);
			allItems.add(key + "=" + value);
		}
		
		return allItems;
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

	public static <E extends MexItem> List<String> items2PrintRecords(List<E> items) {
		return items2PrintRecords(items, Collections.EMPTY_MAP);
	}
	
	public static <E extends MexItem> List<String> items2PrintRecords(List<E> items, String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		
		return items2PrintRecords(items, map);
	}
	
	public static <E extends MexItem> List<String> items2PrintRecords(List<E> items, Map<String, Object> params) {
		List<String> records = new ArrayList<String>();
		
		for(MexItem item:items) {
			records.add(item.toPrint(params));
		}
		
		return records;
	}

	public static <E extends MexItem> List<String> items2PrintRecords(List<E> items, String options) {
		List<String> records = new ArrayList<String>();
		
		for(MexItem item:items) {
			records.add(item.toPrint(options));
		}
		
		return records;
	}
	

	
	public static <E extends MexItem> List<List<String>> items2PDFRecords(List<E> items) {
		List<List<String>> records = new ArrayList<>();
		
		for(MexItem item:items) {
			records.add(item.toPDF());
		}
		
		return records;
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
	
	public static List<MexObject> toMexedObjects(List items) {
		List<MexObject> records = new ArrayList<MexObject>();
		
		for(Object item:items) {
			records.add(new MexObject(item));
		}
		
		return records;
	}
	
	public static List<Object> toRegularObjects(List<MexObject> items) {
		List<Object> records = new ArrayList<Object>();
		
		for(MexObject item:items) {
			records.add(item.getObj());
		}
		
		return records;
	}
	
	public static List<String> filterNullItems(List items) {
		List<String> records = new ArrayList<String>();
		for(Object obj:items) {
			if(obj == null) {
				continue;
			}
			records.add(obj.toString());
		}
		
		return records;
	}
	
	public static List<File> toFileList(List items) {
		List<File> files = new ArrayList<File>();
		for(Object obj:items) {
			if(obj == null) {
				continue;
			}
			
			File file = null;
			if(obj instanceof MexFile) {
				file = ((MexFile)obj).getFile();
			} else {
				file = FileUtil.getIfNormalFile(obj.toString());
			}
			if(file != null) {
				files.add(file);
			}
		}
		
		return files;
	}

	public static <T extends MexItem> List<T> filter(List<T> mexItems, String mexCriteria) {
		return filter(mexItems, mexCriteria, false);
	}
	
	public static <T extends MexItem> List<T> filter(List<T> mexItems, String mexCriteria, boolean isCaseSensitive) {
		MexFilter<T> filter = new MexFilter<T>(mexCriteria, mexItems, isCaseSensitive);
		List<T> result = filter.process();
		
		return result;
	}
	
	public static List<MexObject> search(List items, String criteria) {
		List<MexObject> mexItems = toMexedObjects(items);
		
		MexFilter<MexObject> filter = new MexFilter<MexObject>(criteria, mexItems);
		List<MexObject> result = filter.process();	
		
		return result;
	}
	
	public static List<String> toList(Map map) {
		List<String> list = new ArrayList<>();
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			list.add(key + " => " + map.get(key));
		}

		return list;
	}
	
	public static void sort(List<String> items, boolean sensitive) {
		Collections.sort(items, new StringSenseComparator(sensitive));
	}
	
	public static void sortIgnoreCase(List<String> items) {
		Collections.sort(items, new StringSenseComparator());
	}
	
	public static List lineNumber(List<String> records, boolean align) {
		List<String> items = new ArrayList<>();
		int maxLen = (records.size() + "").length();
		for(int i = 0; i < records.size(); i++) {
			int lineNumber = i + 1;
			String line = "#" + lineNumber;
			if(align) {
				line = StrUtil.extend(line, maxLen + 1);
			}
			
			items.add(line + " " + records.get(i));
		}
		
		return items;
	}

	public static List<String> sortAndMarkSame(List<String> items, boolean sensitive) {
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
				String occurent = count > 2 ? (occurentIndex + "") : "";
				String mark = marks[markIndex%marks.length] + occurent + " ";
				records.add(mark + origin);
			}
			lastKey = key;
		}
		
		return records;
	}
}
