package com.sirap.basic.tool;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sirap.basic.util.DateUtil;
import com.sirap.basic.util.EmptyUtil;
import com.sirap.basic.util.StrUtil;
import com.sirap.basic.util.XXXUtil;

/***
 * D for Debug
 * @author dell
 * @Date September 19, 2014
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class D {
	
	public static boolean eq(Object a, Object b) {
		if(!a.equals(b)) {
			throw new RuntimeException(a + " is not equal to " + b);
		}
		
		return true;
	}
	
	public static void pr(Object... obj) {
		System.out.print(convert(obj));
	}

	private static Object convert(Object... obj) {
		Object temp = null;
		if(obj == null) {
			C.pl("Null stuff.");
			return null;
		}
		if(obj.length == 1) {
			temp = StrUtil.arrayToString(obj[0]);
			if(temp == null) {
				temp = obj[0];
			}
		} else {
			temp = Arrays.toString(obj);
			if(temp == null) {
				temp = obj;
			}
		}
		
		return temp;
	}
	
	public static void pl(Object... obj) {
		System.out.println(convert(obj));
	}

	public static void pl() {
		System.out.println();
	}
	
	public static <E extends Object > void ls(List<E> list) {
		ls(list, false);
	}
	
	public static void ps(Object line) {
		C.pl("[" + line + "]");
	}
	
	public static <E extends Object > void ls(List<E> list, boolean neverShowTotal) {
		XXXUtil.nullCheck(list, "list");
		
		for(Object obj: list) {
			C.pl(obj);
		}
		
		if(!neverShowTotal && list.size() > 5) {
			C.total(list.size());
		}
	}

	public static void debug(Class<?> clz, String method, Object... objects) {
		StringBuffer sb = new StringBuffer();
		sb.append("Debug:" + clz.getName() + "." + method);
		sb.append(Arrays.asList(objects));
		D.pl(sb);
	}
	
	public static void sink() {
		D.pl("SINK");
	}
	
	public static void sink(Object stuff) {
		D.pl("SINK> " + stuff);
	}
	
	public static void ts() {
		String str = DateUtil.displayDate(new Date(), DateUtil.DATE_TIME_FULL);
		D.pl(str);
	}
	
	public static void ts(Object stuff) {
		String str = DateUtil.displayDate(new Date(), DateUtil.DATE_TIME_FULL);
		D.pl(str + "> " + stuff);
	}
	
	public static void pl(Map map) {
		Iterator it = map.keySet().iterator();
		while(it.hasNext()) {
			Object key = it.next();
			pl(key + " => " + map.get(key));
		}
	}
	
	public static <E extends Object > void list(E... objArr) {
		List list = Arrays.asList(objArr);
		C.list(list);
	}

	public static void list(List list) {
		if(EmptyUtil.isNullOrEmpty(list)) {
			D.sink("empty list");
		} else {
			D.pl("D.list start");
			for(Object obj : list) {
				D.pl(obj.getClass().getName() + " " + obj);
			}
			D.pl("D.list end, total: " + list.size());
		}
		
	}

	public static void list2(List list) {
		if(EmptyUtil.isNullOrEmpty(list)) {
			D.sink("empty list");
		} else {
			D.pl("D.list start");
			for(Object obj : list) {
				D.pl((obj != null ? obj.getClass() + "  " : "") + obj);
			}
			D.pl("D.list end, total: " + list.size());
		}
		
	}
}
